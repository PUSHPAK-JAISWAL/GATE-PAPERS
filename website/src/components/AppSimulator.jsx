import React, { useState } from 'react';
import { 
  Search, BookOpen, CheckCircle, Clock, ExternalLink, ArrowLeft, 
  Printer, ZoomIn, ZoomOut, RotateCcw, Share2, Sparkles, Filter, ChevronRight
} from 'lucide-react';
import { PAPERS_DATA } from '../data/papersData';

export default function AppSimulator() {
  const [papers, setPapers] = useState(PAPERS_DATA);
  const [filter, setFilter] = useState('ALL'); // ALL, CS, DA, FINISHED, PENDING
  const [searchQuery, setSearchQuery] = useState('');
  const [activeReadingPaper, setActiveReadingPaper] = useState(null);
  const [simulatedPage, setSimulatedPage] = useState(1);
  const [zoomLevel, setZoomLevel] = useState(100);
  const [printToast, setPrintToast] = useState(false);

  // Toggle status in simulator
  const toggleStatus = (id, e) => {
    e.stopPropagation();
    setPapers(prev =>
      prev.map(p => {
        if (p.id === id) {
          return { ...p, status: p.status === 'finished' ? 'pending' : 'finished' };
        }
        return p;
      })
    );
  };

  const finishedCount = papers.filter(p => p.status === 'finished').length;
  const progressPercent = Math.round((finishedCount / papers.length) * 100);

  // Filtered list
  const filteredPapers = papers.filter(paper => {
    const matchesSearch = 
      paper.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      paper.code.toLowerCase().includes(searchQuery.toLowerCase()) ||
      paper.year.toString().includes(searchQuery);

    if (!matchesSearch) return false;

    if (filter === 'CS') return paper.code === 'CS';
    if (filter === 'DA') return paper.code === 'DA';
    if (filter === 'FINISHED') return paper.status === 'finished';
    if (filter === 'PENDING') return paper.status === 'pending';
    return true;
  });

  const handlePrint = () => {
    setPrintToast(true);
    setTimeout(() => setPrintToast(false), 3000);
  };

  return (
    <section id="simulator" className="py-20 relative">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Section Header */}
        <div className="text-center max-w-3xl mx-auto mb-16">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-cyan-500/10 border border-cyan-500/30 text-cyan-400 text-xs font-semibold uppercase tracking-wider mb-3">
            <Sparkles className="w-3.5 h-3.5" />
            Interactive Preview
          </div>
          <h2 className="text-3xl sm:text-4xl font-extrabold text-white tracking-tight mb-4">
            Test Drive the App Right Here
          </h2>
          <p className="text-slate-400 text-base sm:text-lg">
            Experience the actual Android UI before downloading. Try searching, filtering by CS/DA, 
            marking papers finished, and viewing the native PDF reader.
          </p>
        </div>

        {/* Device Container */}
        <div className="flex flex-col lg:flex-row items-center justify-center gap-12 max-w-6xl mx-auto">
          {/* Left Guide / Highlights */}
          <div className="w-full lg:w-5/12 space-y-6 text-left">
            <div className="p-6 rounded-2xl bg-[#111726] border border-slate-800 shadow-xl">
              <div className="flex items-center gap-3 mb-3">
                <div className="w-10 h-10 rounded-xl bg-orange-500/10 text-orange-400 flex items-center justify-center border border-orange-500/20">
                  <CheckCircle className="w-5 h-5" />
                </div>
                <div>
                  <h4 className="text-base font-bold text-white">Live Progress Tracking</h4>
                  <p className="text-xs text-slate-400">Synced to SQLite Room Database</p>
                </div>
              </div>
              <p className="text-sm text-slate-300 leading-relaxed">
                Click <strong>"Mark Done"</strong> on any paper in the device on the right. Notice how the completion bar updates in real time, keeping your study goals on track.
              </p>
            </div>

            <div className="p-6 rounded-2xl bg-[#111726] border border-slate-800 shadow-xl">
              <div className="flex items-center gap-3 mb-3">
                <div className="w-10 h-10 rounded-xl bg-cyan-500/10 text-cyan-400 flex items-center justify-center border border-cyan-500/20">
                  <BookOpen className="w-5 h-5" />
                </div>
                <div>
                  <h4 className="text-base font-bold text-white">Full-Screen PDF Viewer</h4>
                  <p className="text-xs text-slate-400">Zero external PDF viewers needed</p>
                </div>
              </div>
              <p className="text-sm text-slate-300 leading-relaxed">
                Tap on any paper card to open the embedded PDF engine. Try zooming, changing pages, and testing the Android Print simulator.
              </p>
            </div>

            <div className="p-4 rounded-xl bg-orange-500/10 border border-orange-500/20 flex items-center justify-between text-xs text-orange-300">
              <span className="font-medium">Ready to install on your real phone?</span>
              <a href="#install" className="text-orange-400 font-bold hover:underline flex items-center gap-1">
                Install Guide <ChevronRight className="w-3.5 h-3.5" />
              </a>
            </div>
          </div>

          {/* Right: The Simulated Phone Frame */}
          <div className="w-full max-w-[390px] h-[780px] bg-[#0A0E17] rounded-[48px] p-3 border-4 border-slate-700 shadow-2xl shadow-orange-500/10 relative flex flex-col overflow-hidden">
            {/* Phone Screen Bezel */}
            <div className="w-full h-full bg-[#0A0E17] rounded-[38px] flex flex-col overflow-hidden border border-slate-800 relative">
              
              {/* Top Speaker / Dynamic Island */}
              <div className="w-full bg-[#0A0E17] pt-2 pb-1 px-6 flex items-center justify-between z-20 text-[11px] text-slate-400 font-mono">
                <span>09:41</span>
                <div className="w-20 h-4 bg-black rounded-full flex items-center justify-center">
                  <div className="w-2 h-2 rounded-full bg-slate-800" />
                </div>
                <div className="flex items-center gap-1">
                  <span>5G</span>
                  <span className="w-3.5 h-2 border border-slate-400 rounded-sm inline-block" />
                </div>
              </div>

              {/* Toast Notification in Simulator */}
              {printToast && (
                <div className="absolute top-12 left-4 right-4 z-40 bg-emerald-600 text-white text-xs font-semibold py-2 px-3 rounded-lg shadow-lg flex items-center justify-center gap-2 animate-bounce">
                  <Printer className="w-4 h-4" />
                  <span>Android PrintManager: Job sent to spooler!</span>
                </div>
              )}

              {/* MAIN APP VIEW (Or PDF READER VIEW) */}
              {!activeReadingPaper ? (
                <div className="flex-1 flex flex-col overflow-hidden">
                  {/* App Header */}
                  <div className="bg-[#111726] p-4 border-b border-slate-800">
                    <div className="flex items-center justify-between mb-3">
                      <div className="flex items-center gap-2">
                        <div className="w-7 h-7 rounded-lg bg-orange-500 flex items-center justify-center text-white">
                          <BookOpen className="w-4 h-4" />
                        </div>
                        <h3 className="font-bold text-white text-sm">GATE Papers</h3>
                      </div>
                      <span className="text-[10px] font-mono px-2 py-0.5 rounded bg-slate-800 text-orange-400 border border-slate-700">
                        v1.0.0
                      </span>
                    </div>

                    {/* Search Bar */}
                    <div className="relative">
                      <Search className="w-3.5 h-3.5 absolute left-3 top-2.5 text-slate-400" />
                      <input
                        type="text"
                        placeholder="Search CS, DA, 2024..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        className="w-full bg-[#0A0E17] text-xs text-white pl-8 pr-3 py-1.5 rounded-lg border border-slate-700 focus:outline-none focus:border-orange-500"
                      />
                    </div>
                  </div>

                  {/* Preparation Progress Bar */}
                  <div className="px-4 py-2.5 bg-[#0D1322] border-b border-slate-800/80">
                    <div className="flex items-center justify-between text-[11px] mb-1">
                      <span className="text-slate-300 font-medium">Preparation Status</span>
                      <span className="text-orange-400 font-bold font-mono">
                        {finishedCount}/{papers.length} ({progressPercent}%)
                      </span>
                    </div>
                    <div className="w-full h-1.5 bg-slate-800 rounded-full overflow-hidden">
                      <div
                        className="h-full bg-gradient-to-r from-orange-500 to-emerald-400 transition-all duration-300"
                        style={{ width: `${progressPercent}%` }}
                      />
                    </div>
                  </div>

                  {/* Filter Chips */}
                  <div className="px-3 py-2 flex items-center gap-1.5 overflow-x-auto no-scrollbar border-b border-slate-800/60 bg-[#0A0E17]">
                    {[
                      { id: 'ALL', label: 'All' },
                      { id: 'CS', label: 'CS & IT' },
                      { id: 'DA', label: 'DA & AI' },
                      { id: 'FINISHED', label: 'Done' },
                      { id: 'PENDING', label: 'Pending' }
                    ].map(tab => (
                      <button
                        key={tab.id}
                        onClick={() => setFilter(tab.id)}
                        className={`text-[10px] font-semibold px-2.5 py-1 rounded-full whitespace-nowrap transition-colors ${
                          filter === tab.id
                            ? 'bg-orange-500 text-white'
                            : 'bg-[#161F33] text-slate-400 hover:text-slate-200 border border-slate-800'
                        }`}
                      >
                        {tab.label}
                      </button>
                    ))}
                  </div>

                  {/* Paper List */}
                  <div className="flex-1 overflow-y-auto p-3 space-y-2.5">
                    {filteredPapers.length === 0 ? (
                      <div className="text-center py-12 text-slate-500 text-xs">
                        No papers match your search.
                      </div>
                    ) : (
                      filteredPapers.map(paper => (
                        <div
                          key={paper.id}
                          onClick={() => {
                            setActiveReadingPaper(paper);
                            setSimulatedPage(1);
                          }}
                          className="p-3 rounded-xl bg-[#111726] border border-slate-800 hover:border-orange-500/40 cursor-pointer transition-all shadow-sm group"
                        >
                          <div className="flex items-start justify-between gap-2 mb-1.5">
                            <div className="flex items-center gap-1.5">
                              <span
                                className={`text-[9px] font-bold px-1.5 py-0.5 rounded ${
                                  paper.code === 'CS'
                                    ? 'bg-cyan-500/10 text-cyan-400 border border-cyan-500/20'
                                    : 'bg-orange-500/10 text-orange-400 border border-orange-500/20'
                                }`}
                              >
                                {paper.code}
                              </span>
                              <span className="text-[10px] text-slate-400 font-mono">
                                {paper.year} • {paper.session}
                              </span>
                            </div>

                            {/* Done toggle */}
                            <button
                              onClick={(e) => toggleStatus(paper.id, e)}
                              className={`text-[9px] px-2 py-0.5 rounded-full font-medium transition-colors flex items-center gap-1 ${
                                paper.status === 'finished'
                                  ? 'bg-emerald-500/20 text-emerald-300 border border-emerald-500/40'
                                  : 'bg-slate-800 text-slate-400 hover:text-slate-200 border border-slate-700'
                              }`}
                            >
                              {paper.status === 'finished' ? (
                                <>
                                  <CheckCircle className="w-2.5 h-2.5 text-emerald-400" />
                                  Done
                                </>
                              ) : (
                                <>
                                  <Clock className="w-2.5 h-2.5" />
                                  Mark
                                </>
                              )}
                            </button>
                          </div>

                          <h4 className="text-xs font-semibold text-white group-hover:text-orange-400 transition-colors line-clamp-1 mb-1">
                            {paper.title}
                          </h4>

                          <div className="flex items-center justify-between text-[10px] text-slate-400 pt-1 border-t border-slate-800/60">
                            <span>{paper.conductedBy}</span>
                            <span className="text-cyan-400 group-hover:underline flex items-center gap-0.5">
                              Read PDF <ChevronRight className="w-3 h-3" />
                            </span>
                          </div>
                        </div>
                      ))
                    )}
                  </div>
                </div>
              ) : (
                /* PDF READER SIMULATED VIEW */
                <div className="flex-1 flex flex-col bg-[#111726] overflow-hidden">
                  {/* Top Bar of Reader */}
                  <div className="bg-[#0A0E17] p-2.5 border-b border-slate-800 flex items-center justify-between">
                    <button
                      onClick={() => setActiveReadingPaper(null)}
                      className="p-1 rounded-lg hover:bg-slate-800 text-slate-300"
                    >
                      <ArrowLeft className="w-4 h-4" />
                    </button>
                    <div className="text-center truncate px-2">
                      <p className="text-[11px] font-bold text-white truncate">
                        {activeReadingPaper.title}
                      </p>
                      <p className="text-[9px] text-slate-400 font-mono">
                        Page {simulatedPage} of 32 • {zoomLevel}%
                      </p>
                    </div>
                    <button
                      onClick={handlePrint}
                      className="p-1.5 rounded-lg bg-orange-500/20 text-orange-400 hover:bg-orange-500/30"
                      title="Print Paper"
                    >
                      <Printer className="w-3.5 h-3.5" />
                    </button>
                  </div>

                  {/* Simulated Question Paper Canvas */}
                  <div className="flex-1 bg-slate-950 p-3 overflow-y-auto flex flex-col items-center">
                    <div 
                      className="w-full bg-white text-black p-4 rounded shadow-md transition-transform duration-200"
                      style={{ transform: `scale(${zoomLevel / 100})`, transformOrigin: 'top center' }}
                    >
                      {/* Paper Header */}
                      <div className="border-b-2 border-black pb-2 mb-3 text-center">
                        <p className="text-[8px] font-bold tracking-widest text-slate-600">GRADUATE APTITUDE TEST IN ENGINEERING</p>
                        <h5 className="text-[11px] font-black">{activeReadingPaper.code} — {activeReadingPaper.year}</h5>
                        <p className="text-[7px] text-slate-700">{activeReadingPaper.conductedBy}</p>
                      </div>

                      {/* Question 1 Sample */}
                      <div className="text-[9px] leading-snug space-y-1.5 border-b border-slate-200 pb-2 mb-2">
                        <p className="font-bold text-slate-900">Q.1 (1 Mark)</p>
                        <p className="text-slate-800 font-serif">
                          {activeReadingPaper.code === 'CS' 
                            ? 'Which one of the following regular expressions represents the set of all binary strings with an even number of 0s?'
                            : 'Consider a dataset with features X and target Y. If PCA is applied to reduce dimensions, which matrix is diagonalized?'}
                        </p>
                        <div className="grid grid-cols-2 gap-1 text-[8px] font-mono text-slate-700 pl-2">
                          <div>(A) (1* 0 1* 0 1*)*</div>
                          <div>(B) (1 + 01*0)*</div>
                          <div>(C) (0 + 1)*00(0 + 1)*</div>
                          <div>(D) (1*0)*</div>
                        </div>
                      </div>

                      {/* Question 2 Sample */}
                      <div className="text-[9px] leading-snug space-y-1.5">
                        <p className="font-bold text-slate-900">Q.2 (2 Marks)</p>
                        <p className="text-slate-800 font-serif">
                          Consider the time complexity of the algorithm for finding strongly connected components in a directed graph G = (V, E)...
                        </p>
                        <div className="text-[8px] bg-slate-100 p-1.5 rounded font-mono text-slate-700">
                          Answer format: Numerical Answer Type (NAT)
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* Bottom Toolbar */}
                  <div className="bg-[#0A0E17] p-2 border-t border-slate-800 flex items-center justify-between px-4 text-xs">
                    <div className="flex items-center gap-1 text-slate-300">
                      <button
                        onClick={() => setZoomLevel(prev => Math.max(80, prev - 10))}
                        className="p-1 rounded hover:bg-slate-800"
                      >
                        <ZoomOut className="w-3.5 h-3.5" />
                      </button>
                      <span className="font-mono text-[10px]">{zoomLevel}%</span>
                      <button
                        onClick={() => setZoomLevel(prev => Math.min(140, prev + 10))}
                        className="p-1 rounded hover:bg-slate-800"
                      >
                        <ZoomIn className="w-3.5 h-3.5" />
                      </button>
                    </div>

                    <div className="flex items-center gap-2">
                      <button
                        disabled={simulatedPage <= 1}
                        onClick={() => setSimulatedPage(prev => Math.max(1, prev - 1))}
                        className="px-2 py-0.5 rounded bg-slate-800 text-[10px] text-white disabled:opacity-30"
                      >
                        Prev
                      </button>
                      <button
                        onClick={() => setSimulatedPage(prev => prev + 1)}
                        className="px-2 py-0.5 rounded bg-orange-500 text-[10px] text-white font-medium"
                      >
                        Next
                      </button>
                    </div>
                  </div>
                </div>
              )}

              {/* Bottom Android Home Bar Indicator */}
              <div className="w-full bg-[#0A0E17] py-2 flex justify-center border-t border-slate-800/40">
                <div className="w-28 h-1 bg-slate-600 rounded-full" />
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
