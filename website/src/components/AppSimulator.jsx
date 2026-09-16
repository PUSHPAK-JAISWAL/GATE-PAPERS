import React, { useState } from 'react';
import { 
  Search, BookOpen, CheckCircle, Clock, ArrowLeft, 
  Printer, ZoomIn, ZoomOut, Share2, Sparkles, Filter, Settings,
  RotateCcw, Bookmark, FileText, Check, X, RefreshCw, ExternalLink,
  Code, Cpu, BarChart2, ShieldCheck, Mail, Github, Trash2
} from 'lucide-react';
import { PAPERS_DATA, APP_CONFIG } from '../data/papersData';

export default function AppSimulator() {
  // Initialize state with all 27 authentic papers
  const [papers, setPapers] = useState(PAPERS_DATA);
  const [activeSectionFilter, setActiveSectionFilter] = useState('ALL'); // ALL, CS, DA, PENDING, FINISHED, UNATTEMPTED
  const [searchQuery, setSearchQuery] = useState('');
  const [activeReadingPaper, setActiveReadingPaper] = useState(null);
  const [simulatedPage, setSimulatedPage] = useState(1);
  const [zoomLevel, setZoomLevel] = useState(100);
  const [printToast, setPrintToast] = useState(false);
  const [showSettings, setShowSettings] = useState(false);
  const [isCheckingUpdate, setIsCheckingUpdate] = useState(false);
  const [updateMsg, setUpdateMsg] = useState(null);
  const [syncToast, setSyncToast] = useState(false);

  // Statistics calculation
  const totalCount = papers.length;
  const finishedCount = papers.filter(p => p.status === 'finished').length;
  const pendingCount = papers.filter(p => p.status === 'pending').length;
  const unattemptedCount = papers.filter(p => p.status === 'unattempted' || !p.status).length;
  const overallProgress = Math.round((finishedCount / (totalCount || 1)) * 100);

  const csTotal = papers.filter(p => p.code === 'CS').length;
  const csFinished = papers.filter(p => p.code === 'CS' && p.status === 'finished').length;
  const csPercent = Math.round((csFinished / (csTotal || 1)) * 100);

  const daTotal = papers.filter(p => p.code === 'DA').length;
  const daFinished = papers.filter(p => p.code === 'DA' && p.status === 'finished').length;
  const daPercent = Math.round((daFinished / (daTotal || 1)) * 100);

  // Status updates
  const markFinished = (id, e) => {
    e?.stopPropagation();
    setPapers(prev => prev.map(p => {
      if (p.id === id) {
        const nextStatus = p.status === 'finished' ? 'unattempted' : 'finished';
        return { ...p, status: nextStatus };
      }
      return p;
    }));
  };

  const toggleFlagRevisit = (id, e) => {
    e?.stopPropagation();
    setPapers(prev => prev.map(p => {
      if (p.id === id) {
        const nextStatus = p.status === 'pending' ? 'unattempted' : 'pending';
        return { ...p, status: nextStatus };
      }
      return p;
    }));
  };

  const resetAllProgress = () => {
    setPapers(prev => prev.map(p => ({ ...p, status: 'unattempted' })));
    setShowSettings(false);
  };

  const handleSyncRepos = () => {
    setSyncToast(true);
    setTimeout(() => setSyncToast(false), 2500);
  };

  const handleCheckUpdates = () => {
    setIsCheckingUpdate(true);
    setUpdateMsg(null);
    setTimeout(() => {
      setIsCheckingUpdate(false);
      setUpdateMsg(`You are on the latest version (${APP_CONFIG.version}). No new updates.`);
    }, 1200);
  };

  const handlePrint = () => {
    setPrintToast(true);
    setTimeout(() => setPrintToast(false), 3000);
  };

  // Filtered papers list
  const filteredPapers = papers.filter(paper => {
    const q = searchQuery.toLowerCase().trim();
    const matchesSearch = 
      q === '' ||
      paper.fileName.toLowerCase().includes(q) ||
      paper.title.toLowerCase().includes(q) ||
      paper.year.toString().includes(q) ||
      paper.topics.some(t => t.toLowerCase().includes(q));

    if (!matchesSearch) return false;

    if (activeSectionFilter === 'CS') return paper.code === 'CS';
    if (activeSectionFilter === 'DA') return paper.code === 'DA';
    if (activeSectionFilter === 'FINISHED') return paper.status === 'finished';
    if (activeSectionFilter === 'PENDING') return paper.status === 'pending';
    if (activeSectionFilter === 'UNATTEMPTED') return paper.status === 'unattempted' || !paper.status;
    return true;
  });

  return (
    <section id="simulator" className="py-20 relative">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Section Header */}
        <div className="text-center max-w-3xl mx-auto mb-16">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-cyan-500/10 border border-cyan-500/30 text-cyan-400 text-xs font-semibold uppercase tracking-wider mb-3">
            <Sparkles className="w-3.5 h-3.5" />
            Interactive Android Simulator
          </div>
          <h2 className="text-3xl sm:text-4xl font-extrabold text-white tracking-tight mb-4">
            Experience the Android App Live
          </h2>
          <p className="text-slate-400 text-base sm:text-lg">
            This simulator reproduces the exact layout, styling, and authentic 27 question papers from Pushpak Jaiswal's repositories. Try marking papers done, filtering by branch, opening the PDF viewer, or opening the Settings modal.
          </p>
        </div>

        {/* Device Container */}
        <div className="flex flex-col lg:flex-row items-center justify-center gap-12 max-w-6xl mx-auto">
          {/* Left Guide / Features */}
          <div className="w-full lg:w-5/12 space-y-5 text-left">
            <div className="p-5 rounded-2xl bg-[#111726] border border-slate-800 shadow-xl">
              <div className="flex items-center gap-3 mb-2">
                <div className="w-9 h-9 rounded-xl bg-orange-500/10 text-orange-400 flex items-center justify-center border border-orange-500/20">
                  <CheckCircle className="w-4 h-4" />
                </div>
                <div>
                  <h4 className="text-sm font-bold text-white">Exact Android Architecture</h4>
                  <p className="text-[11px] text-slate-400">Jetpack Compose UI & Room Persistence</p>
                </div>
              </div>
              <p className="text-xs text-slate-300 leading-relaxed">
                The cards below mirror the exact Android app with dark theme, GATE CS & DA summary cards, progress tracking, and authentic PDFs from <strong>gatecs</strong> & <strong>gateda</strong>.
              </p>
            </div>

            <div className="p-5 rounded-2xl bg-[#111726] border border-slate-800 shadow-xl">
              <div className="flex items-center gap-3 mb-2">
                <div className="w-9 h-9 rounded-xl bg-cyan-500/10 text-cyan-400 flex items-center justify-center border border-cyan-500/20">
                  <BookOpen className="w-4 h-4" />
                </div>
                <div>
                  <h4 className="text-sm font-bold text-white">Full-Screen In-App PDF Reader</h4>
                  <p className="text-[11px] text-slate-400">Tap "Solve (PDF)" on any paper card</p>
                </div>
              </div>
              <p className="text-xs text-slate-300 leading-relaxed">
                Experience high-fidelity document simulation, multi-page zooming, and Android PrintManager spooling simulation.
              </p>
            </div>

            <div className="p-5 rounded-2xl bg-[#111726] border border-slate-800 shadow-xl">
              <div className="flex items-center gap-3 mb-2">
                <div className="w-9 h-9 rounded-xl bg-purple-500/10 text-purple-400 flex items-center justify-center border border-purple-500/20">
                  <Settings className="w-4 h-4" />
                </div>
                <div>
                  <h4 className="text-sm font-bold text-white">Live Settings & Updates Menu</h4>
                  <p className="text-[11px] text-slate-400">Tap the Gear icon on the top right</p>
                </div>
              </div>
              <p className="text-xs text-slate-300 leading-relaxed">
                Test the GitHub Release checker, view developer credentials for Pushpak Jaiswal, and reset progress.
              </p>
            </div>
          </div>

          {/* Right: Simulated Android Device */}
          <div className="w-full max-w-[390px] h-[780px] bg-[#0A0E17] rounded-[48px] p-3 border-4 border-slate-700 shadow-2xl shadow-orange-500/10 relative flex flex-col overflow-hidden">
            {/* Phone Screen Bezel */}
            <div className="w-full h-full bg-[#0F172A] rounded-[38px] flex flex-col overflow-hidden border border-slate-800 relative text-white select-none">
              
              {/* Top System Status Bar */}
              <div className="w-full bg-[#0F172A] pt-2 pb-1 px-6 flex items-center justify-between z-20 text-[11px] text-slate-400 font-mono">
                <span>09:41</span>
                <div className="w-20 h-4 bg-black rounded-full flex items-center justify-center">
                  <div className="w-2 h-2 rounded-full bg-slate-800" />
                </div>
                <div className="flex items-center gap-1.5">
                  <span>5G</span>
                  <div className="w-4 h-2.5 border border-slate-400 rounded-sm p-0.5 flex items-center">
                    <div className="w-full h-full bg-slate-300 rounded-[1px]" />
                  </div>
                </div>
              </div>

              {/* Toast Messages */}
              {printToast && (
                <div className="absolute top-12 left-4 right-4 z-50 bg-emerald-600 text-white text-xs font-semibold py-2 px-3 rounded-xl shadow-lg flex items-center justify-center gap-2 animate-bounce">
                  <Printer className="w-4 h-4" />
                  <span>Android PrintManager: Job spooled!</span>
                </div>
              )}

              {syncToast && (
                <div className="absolute top-12 left-4 right-4 z-50 bg-cyan-600 text-white text-xs font-semibold py-2 px-3 rounded-xl shadow-lg flex items-center justify-center gap-2 animate-bounce">
                  <RefreshCw className="w-4 h-4 animate-spin" />
                  <span>Synced 27 papers from GitHub repos!</span>
                </div>
              )}

              {/* ================= VIEW 1: MAIN ANDROID HOME SCREEN ================= */}
              {!activeReadingPaper ? (
                <div className="flex-1 flex flex-col overflow-hidden bg-[#0F172A]">
                  
                  {/* Top App Header Row (Branding & Settings Action) */}
                  <div className="px-4 py-2.5 flex items-center justify-between border-b border-slate-800/80 bg-[#0F172A]">
                    <div className="flex items-center gap-2">
                      <h3 className="text-base font-extrabold text-white tracking-tight">
                        GATE Papers
                      </h3>
                      <span className="text-[10px] font-bold px-1.5 py-0.5 rounded bg-orange-500/20 text-orange-400 border border-orange-500/30">
                        CS
                      </span>
                      <span className="text-[10px] font-bold px-1.5 py-0.5 rounded bg-cyan-500/20 text-cyan-400 border border-cyan-500/30">
                        DA
                      </span>
                    </div>

                    {/* Settings Gear Button */}
                    <button
                      onClick={() => setShowSettings(true)}
                      className="w-8 h-8 rounded-full bg-slate-800/80 hover:bg-slate-700 flex items-center justify-center text-slate-300 border border-slate-700 transition-colors"
                      title="Settings"
                    >
                      <Settings className="w-4 h-4" />
                    </button>
                  </div>

                  {/* Scrollable Main Content */}
                  <div className="flex-1 overflow-y-auto p-3 space-y-3.5 no-scrollbar">
                    
                    {/* Search Field (matching Android UI) */}
                    <div className="relative">
                      <Search className="w-4 h-4 absolute left-3.5 top-3 text-slate-400" />
                      <input
                        type="text"
                        placeholder="Search by file name (e.g. 2026, CS1, DA)..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        className="w-full bg-[#1E293B] text-xs text-white pl-9 pr-8 py-2.5 rounded-2xl border border-slate-700 focus:outline-none focus:border-orange-500"
                      />
                      {searchQuery && (
                        <button 
                          onClick={() => setSearchQuery('')}
                          className="absolute right-3 top-3 text-slate-400 hover:text-white"
                        >
                          <X className="w-3.5 h-3.5" />
                        </button>
                      )}
                    </div>

                    {/* GitHub Repo Sync Status Banner */}
                    <div className="p-3 rounded-2xl bg-[#1E293B] border border-slate-800 flex items-center justify-between">
                      <div className="flex items-center gap-2.5">
                        <span className="w-2.5 h-2.5 rounded-full bg-emerald-400 animate-pulse shrink-0" />
                        <div>
                          <p className="text-[11.5px] font-bold text-white leading-tight">
                            Connected to gatecs & gateda
                          </p>
                          <p className="text-[10px] text-slate-400">
                            Synced {totalCount} papers from GitHub repos
                          </p>
                        </div>
                      </div>
                      <button
                        onClick={handleSyncRepos}
                        className="w-7 h-7 rounded-lg bg-slate-800 hover:bg-slate-700 flex items-center justify-center text-emerald-400 border border-slate-700"
                        title="Sync now"
                      >
                        <RefreshCw className="w-3.5 h-3.5" />
                      </button>
                    </div>

                    {/* Filter Chips Bar (Exact replica of Android filter row) */}
                    <div className="flex items-center gap-1.5 overflow-x-auto no-scrollbar py-0.5">
                      {[
                        { id: 'ALL', label: `All Sections [${totalCount}]` },
                        { id: 'CS', label: `GATE CS [${csTotal}]` },
                        { id: 'DA', label: `GATE DA [${daTotal}]` },
                        { id: 'UNATTEMPTED', label: `Unattempted [${unattemptedCount}]` },
                        { id: 'FINISHED', label: `Finished [${finishedCount}]` },
                        { id: 'PENDING', label: `Pending [${pendingCount}]` }
                      ].map(chip => (
                        <button
                          key={chip.id}
                          onClick={() => setActiveSectionFilter(chip.id)}
                          className={`text-[10.5px] font-bold px-3 py-1.5 rounded-full whitespace-nowrap transition-all ${
                            activeSectionFilter === chip.id
                              ? 'bg-white text-slate-900 shadow-md font-extrabold'
                              : 'bg-[#1E293B] text-slate-300 border border-slate-700 hover:bg-slate-700'
                          }`}
                        >
                          {chip.label}
                        </button>
                      ))}
                    </div>

                    {/* Discipline Hero Cards: GATE CS & GATE DA */}
                    <div className="grid grid-cols-2 gap-2.5">
                      {/* GATE CS Card */}
                      <div 
                        onClick={() => setActiveSectionFilter(activeSectionFilter === 'CS' ? 'ALL' : 'CS')}
                        className="p-3.5 rounded-2xl bg-gradient-to-br from-orange-600 to-amber-600 text-white cursor-pointer shadow-md hover:scale-[1.02] transition-transform"
                      >
                        <div className="flex items-center justify-between mb-2">
                          <Code className="w-5 h-5 opacity-90" />
                          <span className="text-[11px] font-mono font-bold bg-black/20 px-1.5 py-0.5 rounded">
                            {csPercent}%
                          </span>
                        </div>
                        <h4 className="text-sm font-extrabold">GATE CS</h4>
                        <p className="text-[10px] text-orange-100 font-medium">Computer Science</p>
                        <p className="text-[10px] text-orange-200 mt-1 font-mono">
                          {csFinished}/{csTotal} Finished
                        </p>
                      </div>

                      {/* GATE DA Card */}
                      <div 
                        onClick={() => setActiveSectionFilter(activeSectionFilter === 'DA' ? 'ALL' : 'DA')}
                        className="p-3.5 rounded-2xl bg-gradient-to-br from-cyan-600 to-teal-600 text-white cursor-pointer shadow-md hover:scale-[1.02] transition-transform"
                      >
                        <div className="flex items-center justify-between mb-2">
                          <Cpu className="w-5 h-5 opacity-90" />
                          <span className="text-[11px] font-mono font-bold bg-black/20 px-1.5 py-0.5 rounded">
                            {daPercent}%
                          </span>
                        </div>
                        <h4 className="text-sm font-extrabold">GATE DA</h4>
                        <p className="text-[10px] text-cyan-100 font-medium">Data Science & AI</p>
                        <p className="text-[10px] text-cyan-200 mt-1 font-mono">
                          {daFinished}/{daTotal} Finished
                        </p>
                      </div>
                    </div>

                    {/* Progress Tracker Card */}
                    <div className="p-3.5 rounded-2xl bg-gradient-to-r from-purple-900 via-indigo-900 to-slate-900 border border-purple-500/30 text-white shadow-md">
                      <div className="flex items-center justify-between mb-1.5">
                        <div className="flex items-center gap-1.5">
                          <BarChart2 className="w-3.5 h-3.5 text-purple-300" />
                          <span className="text-[10px] font-bold uppercase tracking-wider text-purple-300 font-mono">
                            PROGRESS TRACKER
                          </span>
                        </div>
                        <span className="text-xs font-mono font-bold text-amber-300">
                          {overallProgress}% Done
                        </span>
                      </div>

                      <p className="text-xs font-bold text-white mb-2">
                        Overall Preparation
                      </p>

                      {/* Progress Bar */}
                      <div className="w-full h-2 bg-black/40 rounded-full overflow-hidden mb-2.5">
                        <div
                          className="h-full bg-gradient-to-r from-orange-400 to-emerald-400 transition-all duration-300"
                          style={{ width: `${overallProgress}%` }}
                        />
                      </div>

                      {/* Stats Badges */}
                      <div className="flex items-center justify-between text-[10px] font-mono text-slate-300">
                        <span className="text-emerald-300 font-bold">✓ {finishedCount} Done</span>
                        <span className="text-amber-300 font-bold">⏳ {pendingCount} Pending</span>
                        <span className="text-slate-400">{unattemptedCount} Left</span>
                      </div>
                    </div>

                    {/* Question Papers List Heading */}
                    <div className="flex items-center justify-between pt-1">
                      <h4 className="text-xs font-bold text-white">
                        All Question Papers [{filteredPapers.length}]
                      </h4>
                      <span className="text-[10px] text-slate-400 font-mono">
                        Tap paper to open PDF
                      </span>
                    </div>

                    {/* Question Paper Cards */}
                    <div className="space-y-2.5 pb-6">
                      {filteredPapers.length === 0 ? (
                        <div className="text-center py-10 text-slate-500 text-xs">
                          No papers found matching "{searchQuery}"
                        </div>
                      ) : (
                        filteredPapers.map(paper => {
                          const isDone = paper.status === 'finished';
                          const isPending = paper.status === 'pending';

                          return (
                            <div
                              key={paper.id}
                              onClick={() => {
                                setActiveReadingPaper(paper);
                                setSimulatedPage(1);
                              }}
                              className="p-3 rounded-2xl bg-[#1E293B] border border-slate-700/80 hover:border-orange-500/50 cursor-pointer transition-all shadow-sm group"
                            >
                              {/* Top metadata row */}
                              <div className="flex items-center justify-between mb-1.5">
                                <div className="flex items-center gap-1.5">
                                  <span
                                    className={`text-[9.5px] font-bold px-2 py-0.5 rounded-md font-mono ${
                                      paper.code === 'CS'
                                        ? 'bg-orange-500/20 text-orange-400 border border-orange-500/30'
                                        : 'bg-cyan-500/20 text-cyan-400 border border-cyan-500/30'
                                    }`}
                                  >
                                    {paper.section}
                                  </span>
                                  <span className="text-[9.5px] font-mono px-1.5 py-0.5 rounded bg-slate-800 text-slate-300 font-bold">
                                    {paper.year}
                                  </span>
                                  {isDone && (
                                    <span className="text-[9px] font-bold px-1.5 py-0.5 rounded bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                                      FINISHED
                                    </span>
                                  )}
                                  {isPending && (
                                    <span className="text-[9px] font-bold px-1.5 py-0.5 rounded bg-amber-500/20 text-amber-400 border border-amber-500/30">
                                      REVISIT
                                    </span>
                                  )}
                                  {!isDone && !isPending && (
                                    <span className="text-[9px] font-bold px-1.5 py-0.5 rounded bg-slate-800 text-slate-400">
                                      UNATTEMPTED
                                    </span>
                                  )}
                                </div>

                                {/* Revisit Bookmark Icon */}
                                <button
                                  onClick={(e) => toggleFlagRevisit(paper.id, e)}
                                  className={`p-1 rounded-md transition-colors ${
                                    isPending ? 'text-amber-400' : 'text-slate-500 hover:text-slate-300'
                                  }`}
                                  title="Flag for revisit"
                                >
                                  <Bookmark className="w-3.5 h-3.5 fill-current" />
                                </button>
                              </div>

                              {/* Title (File Name) */}
                              <h5 className="text-xs font-bold text-white font-mono mb-0.5 group-hover:text-orange-400 transition-colors">
                                {paper.fileName}
                              </h5>

                              {/* Subtitle */}
                              <p className="text-[10px] text-slate-400 font-mono mb-2">
                                {paper.subtitle}
                              </p>

                              {/* Action Buttons Row */}
                              <div className="pt-2 border-t border-slate-700/60 flex items-center justify-between gap-1.5">
                                <span className="text-[10px] text-orange-400 font-bold flex items-center gap-1 group-hover:underline">
                                  <FileText className="w-3 h-3" />
                                  <span>Solve (PDF)</span>
                                </span>

                                <div className="flex items-center gap-1.5">
                                  {/* Mark Done Button */}
                                  <button
                                    onClick={(e) => markFinished(paper.id, e)}
                                    className={`text-[9px] font-bold px-2 py-1 rounded-lg flex items-center gap-1 transition-all ${
                                      isDone
                                        ? 'bg-emerald-500 text-white'
                                        : 'bg-slate-800 text-slate-300 hover:bg-slate-700 border border-slate-700'
                                    }`}
                                  >
                                    <Check className="w-3 h-3" />
                                    <span>{isDone ? 'Done' : 'Mark Done'}</span>
                                  </button>
                                </div>
                              </div>
                            </div>
                          );
                        })
                      )}
                    </div>
                  </div>
                </div>
              ) : (
                /* ================= VIEW 2: FULL-SCREEN PDF READER ================= */
                <div className="flex-1 flex flex-col overflow-hidden bg-[#0A0E17]">
                  {/* PDF Reader Top App Bar */}
                  <div className="bg-[#111726] p-3 border-b border-slate-800 flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <button
                        onClick={() => setActiveReadingPaper(null)}
                        className="w-7 h-7 rounded-lg bg-slate-800 text-slate-300 flex items-center justify-center hover:bg-slate-700"
                      >
                        <ArrowLeft className="w-4 h-4" />
                      </button>
                      <div>
                        <h4 className="text-xs font-bold text-white font-mono truncate max-w-[170px]">
                          {activeReadingPaper.fileName}
                        </h4>
                        <p className="text-[9px] text-slate-400 font-mono">
                          Page {simulatedPage} of 32 • {zoomLevel}%
                        </p>
                      </div>
                    </div>

                    {/* PDF Actions */}
                    <div className="flex items-center gap-1">
                      <button
                        onClick={() => setZoomLevel(prev => Math.max(70, prev - 15))}
                        className="p-1 rounded bg-slate-800 text-slate-300 hover:text-white"
                        title="Zoom Out"
                      >
                        <ZoomOut className="w-3.5 h-3.5" />
                      </button>
                      <button
                        onClick={() => setZoomLevel(prev => Math.min(150, prev + 15))}
                        className="p-1 rounded bg-slate-800 text-slate-300 hover:text-white"
                        title="Zoom In"
                      >
                        <ZoomIn className="w-3.5 h-3.5" />
                      </button>
                      <button
                        onClick={handlePrint}
                        className="p-1.5 rounded-lg bg-orange-500 text-white hover:bg-orange-600 ml-1"
                        title="Android Print Spooler"
                      >
                        <Printer className="w-3.5 h-3.5" />
                      </button>
                    </div>
                  </div>

                  {/* PDF Simulated Canvas Sheet */}
                  <div className="flex-1 overflow-y-auto p-3 flex flex-col items-center justify-start bg-[#060911]">
                    <div 
                      className="w-full bg-white text-slate-900 rounded-xl shadow-2xl p-5 font-serif transition-transform duration-200 min-h-[440px]"
                      style={{ transform: `scale(${zoomLevel / 100})`, transformOrigin: 'top center' }}
                    >
                      {/* Document Header */}
                      <div className="border-b-2 border-slate-900 pb-2 mb-3 text-center">
                        <h3 className="text-xs font-black tracking-wider uppercase">
                          GRADUATE APTITUDE TEST IN ENGINEERING
                        </h3>
                        <p className="text-[10px] font-bold text-slate-700">
                          {activeReadingPaper.section} ({activeReadingPaper.code}) • {activeReadingPaper.year}
                        </p>
                        <p className="text-[9px] text-slate-500 font-mono">
                          Duration: 180 Mins • Maximum Marks: 100
                        </p>
                      </div>

                      {/* Simulated Question */}
                      <div className="space-y-3 text-[11px] leading-relaxed text-slate-800">
                        <div className="p-2 rounded bg-slate-100 border border-slate-200 font-sans">
                          <span className="font-bold text-[10px] text-orange-600 block mb-0.5">
                            Q.{simulatedPage} [1 Mark, Negative: 0.33]
                          </span>
                          <p className="font-serif">
                            {activeReadingPaper.code === 'CS'
                              ? 'Let G = (V, E) be a connected undirected simple graph with |V| = n and |E| = m. Which of the following statements is ALWAYS TRUE regarding the minimum spanning tree of G?'
                              : 'Let X be a normally distributed continuous random variable with mean μ = 0 and variance σ² = 1. What is the probability density function value at X = 0?'}
                          </p>
                        </div>

                        {/* Options */}
                        <div className="space-y-1 pl-2 font-sans text-[10.5px]">
                          <p>(A) Every edge of minimum weight in G is part of every MST.</p>
                          <p>(B) If all edge weights in G are distinct, then G has a unique MST.</p>
                          <p>(C) The shortest path between any two vertices in G is always an edge in the MST.</p>
                          <p>(D) Cycle property does not hold when negative weight edges exist.</p>
                        </div>

                        <div className="pt-3 border-t border-slate-200 text-center text-[9px] text-slate-400 font-mono">
                          Authentic source: github.com/{activeReadingPaper.githubRepo}/{activeReadingPaper.fileName}
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* PDF Reader Bottom Control Bar */}
                  <div className="p-2.5 bg-[#111726] border-t border-slate-800 flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <button
                        onClick={() => setSimulatedPage(prev => Math.max(1, prev - 1))}
                        disabled={simulatedPage === 1}
                        className="text-[10px] px-2 py-1 rounded bg-slate-800 text-slate-300 disabled:opacity-40"
                      >
                        Prev Page
                      </button>
                      <span className="text-[10px] font-mono text-slate-400">
                        {simulatedPage} / 32
                      </span>
                      <button
                        onClick={() => setSimulatedPage(prev => Math.min(32, prev + 1))}
                        disabled={simulatedPage === 32}
                        className="text-[10px] px-2 py-1 rounded bg-slate-800 text-slate-300 disabled:opacity-40"
                      >
                        Next Page
                      </button>
                    </div>

                    <button
                      onClick={() => markFinished(activeReadingPaper.id)}
                      className={`text-[10px] font-bold px-3 py-1.5 rounded-lg flex items-center gap-1.5 transition-colors ${
                        activeReadingPaper.status === 'finished'
                          ? 'bg-emerald-600 text-white'
                          : 'bg-orange-500 text-white'
                      }`}
                    >
                      <Check className="w-3 h-3" />
                      <span>{activeReadingPaper.status === 'finished' ? 'Solved ✓' : 'Mark Solved'}</span>
                    </button>
                  </div>
                </div>
              )}

              {/* ================= MODAL: ANDROID SETTINGS DIALOG ================= */}
              {showSettings && (
                <div className="absolute inset-0 z-50 bg-black/80 backdrop-blur-sm flex items-center justify-center p-3 animate-fadeIn">
                  <div className="w-full max-h-[92%] bg-[#111726] rounded-3xl border border-slate-700 flex flex-col overflow-hidden shadow-2xl">
                    
                    {/* Settings Header */}
                    <div className="p-4 border-b border-slate-800 flex items-center justify-between">
                      <div className="flex items-center gap-2">
                        <div className="w-8 h-8 rounded-xl bg-orange-500/20 text-orange-400 flex items-center justify-center">
                          <Settings className="w-4 h-4" />
                        </div>
                        <div>
                          <h4 className="text-sm font-bold text-white">Settings & Info</h4>
                          <p className="text-[10px] text-slate-400">GATE Papers Companion</p>
                        </div>
                      </div>
                      <button
                        onClick={() => setShowSettings(false)}
                        className="w-7 h-7 rounded-full bg-slate-800 text-slate-400 hover:text-white flex items-center justify-center"
                      >
                        <X className="w-4 h-4" />
                      </button>
                    </div>

                    {/* Settings Body */}
                    <div className="p-4 space-y-4 overflow-y-auto no-scrollbar text-xs">
                      
                      {/* Section 1: App Updates */}
                      <div className="p-3 rounded-2xl bg-[#1E293B] border border-slate-700">
                        <div className="flex items-center justify-between mb-2">
                          <span className="font-bold text-white text-xs">App Updates & Releases</span>
                          <span className="text-[10px] font-mono px-2 py-0.5 rounded bg-slate-800 text-orange-400">
                            {APP_CONFIG.version}
                          </span>
                        </div>
                        <p className="text-[11px] text-slate-400 mb-3">
                          Directly connected to GitHub Releases API.
                        </p>

                        <button
                          onClick={handleCheckUpdates}
                          disabled={isCheckingUpdate}
                          className="w-full h-10 rounded-xl bg-slate-800 hover:bg-slate-700 border border-slate-700 text-white font-bold flex items-center justify-center gap-2 transition-colors"
                        >
                          <RefreshCw className={`w-3.5 h-3.5 text-orange-400 ${isCheckingUpdate ? 'animate-spin' : ''}`} />
                          <span>{isCheckingUpdate ? 'Checking GitHub...' : 'Check for Updates'}</span>
                        </button>

                        {updateMsg && (
                          <p className="text-[10.5px] text-emerald-400 mt-2 font-medium">
                            {updateMsg}
                          </p>
                        )}
                      </div>

                      {/* Section 2: Developer Details */}
                      <div className="p-3 rounded-2xl bg-[#1E293B] border border-slate-700 space-y-2">
                        <span className="font-bold text-white text-xs block mb-1">Developer & Project Details</span>
                        
                        <div className="flex items-center justify-between py-1 border-b border-slate-800">
                          <span className="text-slate-400 text-[11px]">Author</span>
                          <span className="font-semibold text-white text-[11px]">Pushpak Jaiswal</span>
                        </div>

                        <div className="flex items-center justify-between py-1 border-b border-slate-800">
                          <span className="text-slate-400 text-[11px]">GitHub</span>
                          <a href={APP_CONFIG.author.githubUrl} target="_blank" rel="noopener noreferrer" className="text-orange-400 text-[11px] hover:underline flex items-center gap-1 font-mono">
                            @PUSHPAK-JAISWAL <ExternalLink className="w-2.5 h-2.5" />
                          </a>
                        </div>

                        <div className="flex items-center justify-between py-1 border-b border-slate-800">
                          <span className="text-slate-400 text-[11px]">Support Email</span>
                          <a href={`mailto:${APP_CONFIG.author.email}`} className="text-cyan-400 text-[11px] hover:underline font-mono">
                            {APP_CONFIG.author.email}
                          </a>
                        </div>

                        <div className="flex items-center justify-between py-1">
                          <span className="text-slate-400 text-[11px]">Repositories</span>
                          <span className="text-slate-300 text-[10px] font-mono">gatecs & gateda</span>
                        </div>
                      </div>

                      {/* Section 3: Preparation Data & Reset */}
                      <div className="p-3 rounded-2xl bg-[#1E293B] border border-slate-700 space-y-2.5">
                        <span className="font-bold text-white text-xs block">Data & Preparation Tracker</span>
                        
                        <div className="p-2 rounded-xl bg-slate-800/80 flex items-center justify-between text-[11px]">
                          <span className="text-slate-400">Offline Cached Papers</span>
                          <span className="font-bold text-white">{totalCount} ready offline</span>
                        </div>

                        <button
                          onClick={handleSyncRepos}
                          className="w-full h-10 rounded-xl bg-slate-800 hover:bg-slate-700 border border-slate-700 text-white font-semibold text-[11px] flex items-center justify-center gap-2"
                        >
                          <RefreshCw className="w-3.5 h-3.5 text-emerald-400" />
                          <span>Sync Question Papers from Repos</span>
                        </button>

                        <button
                          onClick={resetAllProgress}
                          className="w-full h-10 rounded-xl bg-rose-500/10 hover:bg-rose-500/20 border border-rose-500/30 text-rose-400 font-bold text-[11px] flex items-center justify-center gap-2"
                        >
                          <Trash2 className="w-3.5 h-3.5" />
                          <span>Reset Solved Preparation Progress</span>
                        </button>
                      </div>

                    </div>
                  </div>
                </div>
              )}

            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
