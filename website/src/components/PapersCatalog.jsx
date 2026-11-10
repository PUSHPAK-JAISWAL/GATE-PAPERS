import React, { useState } from 'react';
import { 
  BookOpen, Search, Download, ExternalLink, FileText, CheckCircle, 
  ArrowDownUp, ArrowDown, ArrowUp, Eye, Sparkles
} from 'lucide-react';
import { FaGithub as Github } from 'react-icons/fa';
import { useDynamicPapers } from '../data/DynamicPapersContext';

function extractYear(fileName, fallbackYear) {
  const match = fileName.match(/(19\d{2}|20\d{2})/);
  return match ? parseInt(match[1], 10) : (fallbackYear || 0);
}

export default function PapersCatalog({ onDownloadClick, onViewPaper }) {
  const { papers, stats, config } = useDynamicPapers();
  const [selectedDiscipline, setSelectedDiscipline] = useState('ALL');
  const [search, setSearch] = useState('');
  const [sortOrder, setSortOrder] = useState('year-desc'); // 'year-desc', 'year-asc', 'name-asc', 'name-desc'

  const filtered = papers.filter(p => {
    const matchesDisc = selectedDiscipline === 'ALL' || p.code === selectedDiscipline;
    const matchesSearch = 
      p.title.toLowerCase().includes(search.toLowerCase()) ||
      (p.topics && p.topics.some(t => t.toLowerCase().includes(search.toLowerCase()))) ||
      p.fileName.toLowerCase().includes(search.toLowerCase()) ||
      (p.year && p.year.toString().includes(search));
    return matchesDisc && matchesSearch;
  });

  const sorted = [...filtered].sort((a, b) => {
    const yearA = extractYear(a.fileName, a.year);
    const yearB = extractYear(b.fileName, b.year);
    if (sortOrder === 'year-desc') {
      if (yearB !== yearA) return yearB - yearA;
      return b.fileName.localeCompare(a.fileName);
    }
    if (sortOrder === 'year-asc') {
      if (yearA !== yearB) return yearA - yearB;
      return a.fileName.localeCompare(b.fileName);
    }
    if (sortOrder === 'name-asc') {
      return a.fileName.localeCompare(b.fileName);
    }
    if (sortOrder === 'name-desc') {
      return b.fileName.localeCompare(a.fileName);
    }
    return 0;
  });

  const handleDownloadPaper = (paper, e) => {
    e.stopPropagation();
    const link = document.createElement('a');
    link.href = paper.rawUrl;
    link.download = paper.fileName || 'GATE-Paper.pdf';
    link.target = '_blank';
    link.rel = 'noopener noreferrer';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <section id="papers" className="py-24 bg-[#0B101D] relative border-t border-slate-800">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="flex flex-col md:flex-row md:items-end justify-between gap-6 mb-12">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-cyan-500/10 border border-cyan-500/30 text-cyan-400 text-xs font-semibold uppercase tracking-wider mb-3">
              <BookOpen className="w-3.5 h-3.5" />
              Pushpak Jaiswal's Verified Repositories
            </div>
            <h2 className="text-3xl sm:text-4xl font-extrabold text-white tracking-tight">
              Authentic Question Papers Included ({stats.totalPapers})
            </h2>
            <p className="text-slate-400 text-sm sm:text-base mt-2 max-w-2xl">
              All question papers ({stats.yearRange}) are directly synchronized from verified repositories (<a href={config.gateCsRepo} target="_blank" rel="noopener noreferrer" className="text-orange-400 hover:underline">gatecs</a> & <a href={config.gateDaRepo} target="_blank" rel="noopener noreferrer" className="text-cyan-400 hover:underline">gateda</a>).
              Read every question directly in the online viewer without downloading, or download offline copies anytime.
            </p>
          </div>

          {/* Discipline Filters */}
          <div className="flex items-center gap-2 bg-[#111726] p-1.5 rounded-xl border border-slate-800 shrink-0">
            {[
              { id: 'ALL', label: `All Papers (${stats.totalPapers})` },
              { id: 'CS', label: `GATE CS (${stats.csPapersCount})` },
              { id: 'DA', label: `GATE DA (${stats.daPapersCount})` }
            ].map(tab => (
              <button
                key={tab.id}
                onClick={() => setSelectedDiscipline(tab.id)}
                className={`text-xs font-semibold px-3.5 py-2 rounded-lg transition-all ${
                  selectedDiscipline === tab.id
                    ? 'bg-orange-500 text-white shadow-md shadow-orange-500/20'
                    : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
                }`}
              >
                {tab.label}
              </button>
            ))}
          </div>
        </div>

        {/* Filter and Sort Toolbar */}
        <div className="flex flex-col sm:flex-row items-stretch sm:items-center justify-between gap-4 mb-8">
          {/* Search Bar */}
          <div className="relative flex-1 max-w-md">
            <Search className="w-4 h-4 absolute left-3.5 top-3 text-slate-400" />
            <input
              type="text"
              placeholder="Search by file name (e.g. 2026, CS1, DA)..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full bg-[#111726] text-sm text-white pl-10 pr-4 py-2.5 rounded-xl border border-slate-800 focus:outline-none focus:border-orange-500 font-mono"
            />
          </div>

          {/* Sort Controls */}
          <div className="flex items-center gap-2.5 shrink-0">
            <span className="text-xs text-slate-400 font-medium flex items-center gap-1">
              <ArrowDownUp className="w-3.5 h-3.5 text-orange-400" />
              Sort by:
            </span>
            <div className="flex items-center gap-1.5 bg-[#111726] p-1 rounded-xl border border-slate-800">
              <button
                onClick={() => setSortOrder(sortOrder === 'year-desc' ? 'year-asc' : 'year-desc')}
                className={`flex items-center gap-1.5 text-xs font-semibold px-3 py-1.5 rounded-lg transition-all ${
                  sortOrder.startsWith('year')
                    ? 'bg-orange-500/20 text-orange-400 border border-orange-500/40'
                    : 'text-slate-400 hover:text-slate-200'
                }`}
                title="Toggle Year Sort"
              >
                {sortOrder === 'year-desc' ? <ArrowDown className="w-3.5 h-3.5" /> : <ArrowUp className="w-3.5 h-3.5" />}
                <span>{sortOrder === 'year-desc' ? `Year (${stats.maxYear} → ${stats.minYear})` : `Year (${stats.minYear} → ${stats.maxYear})`}</span>
              </button>

              <select
                value={sortOrder}
                onChange={(e) => setSortOrder(e.target.value)}
                className="bg-transparent text-xs font-semibold text-slate-300 py-1.5 px-2 rounded-lg border-none focus:outline-none cursor-pointer"
              >
                <option value="year-desc" className="bg-[#111726] text-white">Year: Newest First ({stats.maxYear} → {stats.minYear})</option>
                <option value="year-asc" className="bg-[#111726] text-white">Year: Oldest First ({stats.minYear} → {stats.maxYear})</option>
                <option value="name-asc" className="bg-[#111726] text-white">File Name (A → Z)</option>
                <option value="name-desc" className="bg-[#111726] text-white">File Name (Z → A)</option>
              </select>
            </div>
          </div>
        </div>

        {/* Papers Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-12">
          {sorted.map(paper => (
            <div
              key={paper.id}
              className="p-6 rounded-2xl bg-[#111726] border border-slate-800 hover:border-slate-700 transition-all duration-200 flex flex-col justify-between group shadow-sm hover:shadow-md cursor-pointer"
              onClick={() => onViewPaper && onViewPaper(paper)}
            >
              <div>
                <div className="flex items-center justify-between mb-3">
                  <div className="flex items-center gap-2">
                    <span
                      className={`text-xs font-bold px-2.5 py-1 rounded-md font-mono ${
                        paper.code === 'CS'
                          ? 'bg-orange-500/10 text-orange-400 border border-orange-500/30'
                          : 'bg-cyan-500/10 text-cyan-400 border border-cyan-500/30'
                      }`}
                    >
                      {paper.section}
                    </span>
                    <span className="text-xs font-bold px-2 py-0.5 rounded bg-slate-800 text-slate-300 font-mono">
                      {paper.year}
                    </span>
                  </div>
                  <span className="text-xs text-slate-400 font-mono">
                    {paper.fileSize}
                  </span>
                </div>

                <h4 className="text-base font-bold text-white font-mono mb-1 group-hover:text-orange-400 transition-colors flex items-center justify-between">
                  <span>{paper.fileName}</span>
                  <Eye className="w-4 h-4 text-slate-500 group-hover:text-orange-400 transition-colors shrink-0" />
                </h4>

                <p className="text-xs text-slate-400 mb-3 font-mono">
                  repo: {paper.githubRepo}
                </p>

                {/* Topics covered */}
                <div className="flex flex-wrap gap-1.5 mb-6">
                  {(paper.topics || ['Computer Science & IT', 'GATE PYQ']).map((topic, i) => (
                    <span
                      key={i}
                      className="text-[11px] px-2 py-0.5 rounded bg-slate-900 text-slate-300 border border-slate-800"
                    >
                      {topic}
                    </span>
                  ))}
                </div>
              </div>

              {/* Card Footer Actions: View Online & Download */}
              <div 
                className="pt-4 border-t border-slate-800/80 flex items-center justify-between gap-2"
                onClick={(e) => e.stopPropagation()}
              >
                {/* 1. View Online Button (No download required) */}
                <button
                  type="button"
                  onClick={() => onViewPaper && onViewPaper(paper)}
                  className="flex-1 inline-flex items-center justify-center gap-1.5 bg-orange-500/10 hover:bg-orange-500 text-orange-400 hover:text-white border border-orange-500/30 hover:border-orange-500 font-semibold text-xs py-2 px-3 rounded-xl transition-all shadow-sm"
                  title={`View ${paper.fileName} online without downloading`}
                >
                  <Eye className="w-3.5 h-3.5" />
                  <span>View Online</span>
                </button>

                {/* 2. Direct Download Button (For those who want to download) */}
                <button
                  type="button"
                  onClick={(e) => handleDownloadPaper(paper, e)}
                  className="inline-flex items-center justify-center gap-1.5 bg-slate-800/90 hover:bg-slate-700 text-slate-200 hover:text-white border border-slate-700 text-xs font-semibold py-2 px-3 rounded-xl transition-all"
                  title={`Download ${paper.fileName} for offline study`}
                >
                  <Download className="w-3.5 h-3.5 text-slate-400" />
                  <span>Download</span>
                </button>

                {/* 3. GitHub Source Link */}
                <a
                  href={paper.repoUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="p-2 rounded-xl bg-slate-800/40 hover:bg-slate-800 text-slate-400 hover:text-white border border-slate-800 transition-colors"
                  title="View on GitHub"
                >
                  <Github className="w-3.5 h-3.5" />
                </a>
              </div>
            </div>
          ))}
        </div>

        {/* CTA Bottom Banner */}
        <div className="text-center p-8 rounded-2xl bg-gradient-to-r from-orange-500/10 via-amber-500/10 to-cyan-500/10 border border-orange-500/20 max-w-4xl mx-auto">
          <h3 className="text-xl font-bold text-white mb-2">Want to practice and solve all {stats.totalPapers} papers offline on your phone?</h3>
          <p className="text-sm text-slate-300 mb-6 max-w-xl mx-auto">
            Get the full GATE Papers APK directly on your Android phone. Built-in PDF reader, offline SQLite caching, preparation tracker, and print spooling.
          </p>
          <button
            onClick={onDownloadClick}
            className="inline-flex items-center gap-2 bg-orange-500 hover:bg-orange-600 text-white font-bold text-sm px-6 py-3 rounded-xl shadow-lg shadow-orange-500/20 transition-all hover:-translate-y-0.5"
          >
            <Download className="w-4 h-4" />
            <span>Download GATE Papers APK ({stats.version})</span>
          </button>
        </div>
      </div>
    </section>
  );
}
