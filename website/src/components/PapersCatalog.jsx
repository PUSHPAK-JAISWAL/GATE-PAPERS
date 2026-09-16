import React, { useState } from 'react';
import { BookOpen, Search, Download, ExternalLink, Github, FileText, CheckCircle } from 'lucide-react';
import { PAPERS_DATA, APP_CONFIG } from '../data/papersData';

export default function PapersCatalog({ onDownloadClick }) {
  const [selectedDiscipline, setSelectedDiscipline] = useState('ALL');
  const [search, setSearch] = useState('');

  const filtered = PAPERS_DATA.filter(p => {
    const matchesDisc = selectedDiscipline === 'ALL' || p.code === selectedDiscipline;
    const matchesSearch = 
      p.title.toLowerCase().includes(search.toLowerCase()) ||
      p.topics.some(t => t.toLowerCase().includes(search.toLowerCase())) ||
      p.fileName.toLowerCase().includes(search.toLowerCase()) ||
      p.year.toString().includes(search);
    return matchesDisc && matchesSearch;
  });

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
              Authentic Question Papers Included ({PAPERS_DATA.length})
            </h2>
            <p className="text-slate-400 text-sm sm:text-base mt-2 max-w-2xl">
              All question papers are directly synchronized from authentic repositories (<a href={APP_CONFIG.gateCsRepo} target="_blank" rel="noopener noreferrer" className="text-orange-400 hover:underline">gatecs</a> & <a href={APP_CONFIG.gateDaRepo} target="_blank" rel="noopener noreferrer" className="text-cyan-400 hover:underline">gateda</a>).
              Zero altered scans, zero watermarks, and 100% verified question papers.
            </p>
          </div>

          {/* Discipline Filters */}
          <div className="flex items-center gap-2 bg-[#111726] p-1.5 rounded-xl border border-slate-800 shrink-0">
            {[
              { id: 'ALL', label: `All Papers (${PAPERS_DATA.length})` },
              { id: 'CS', label: `GATE CS (${APP_CONFIG.csPapersCount})` },
              { id: 'DA', label: `GATE DA (${APP_CONFIG.daPapersCount})` }
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

        {/* Search Bar */}
        <div className="relative mb-8 max-w-md">
          <Search className="w-4 h-4 absolute left-3.5 top-3 text-slate-400" />
          <input
            type="text"
            placeholder="Search by file name (e.g. 2026, CS1, DA)..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full bg-[#111726] text-sm text-white pl-10 pr-4 py-2.5 rounded-xl border border-slate-800 focus:outline-none focus:border-orange-500 font-mono"
          />
        </div>

        {/* Papers Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-12">
          {filtered.map(paper => (
            <div
              key={paper.id}
              className="p-6 rounded-2xl bg-[#111726] border border-slate-800 hover:border-slate-700 transition-all duration-200 flex flex-col justify-between group shadow-sm hover:shadow-md"
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

                <h4 className="text-base font-bold text-white font-mono mb-1 group-hover:text-orange-400 transition-colors">
                  {paper.fileName}
                </h4>

                <p className="text-xs text-slate-400 mb-3 font-mono">
                  repo: {paper.githubRepo}
                </p>

                {/* Topics covered */}
                <div className="flex flex-wrap gap-1.5 mb-6">
                  {paper.topics.map((topic, i) => (
                    <span
                      key={i}
                      className="text-[11px] px-2 py-0.5 rounded bg-slate-900 text-slate-300 border border-slate-800"
                    >
                      {topic}
                    </span>
                  ))}
                </div>
              </div>

              {/* Card Footer Actions */}
              <div className="pt-4 border-t border-slate-800/80 flex items-center justify-between gap-2">
                <a
                  href={paper.rawUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-xs text-orange-400 font-semibold hover:text-orange-300 flex items-center gap-1.5"
                >
                  <FileText className="w-3.5 h-3.5" />
                  <span>Raw PDF</span>
                </a>
                <a
                  href={paper.repoUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-xs text-slate-400 hover:text-slate-200 flex items-center gap-1 font-mono"
                >
                  <Github className="w-3 h-3" />
                  <span>GitHub File</span>
                  <ExternalLink className="w-2.5 h-2.5 ml-0.5 text-slate-500" />
                </a>
              </div>
            </div>
          ))}
        </div>

        {/* CTA Bottom Banner */}
        <div className="text-center p-8 rounded-2xl bg-gradient-to-r from-orange-500/10 via-amber-500/10 to-cyan-500/10 border border-orange-500/20 max-w-4xl mx-auto">
          <h3 className="text-xl font-bold text-white mb-2">Want to practice and solve all 27 papers offline?</h3>
          <p className="text-sm text-slate-300 mb-6 max-w-xl mx-auto">
            Get the full GATE Papers APK directly on your Android phone. Built-in PDF reader, offline SQLite caching, preparation tracker, and print spooling.
          </p>
          <button
            onClick={onDownloadClick}
            className="inline-flex items-center gap-2 bg-orange-500 hover:bg-orange-600 text-white font-bold text-sm px-6 py-3 rounded-xl shadow-lg shadow-orange-500/20 transition-all hover:-translate-y-0.5"
          >
            <Download className="w-4 h-4" />
            <span>Download GATE Papers APK ({APP_CONFIG.version})</span>
          </button>
        </div>
      </div>
    </section>
  );
}
