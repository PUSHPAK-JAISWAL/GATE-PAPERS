import React, { useState, useEffect, useRef } from 'react';
import { 
  X, Download, ExternalLink, Eye, BookOpen, Maximize2, Minimize2, 
  RefreshCw, FileText, CheckCircle2, AlertCircle, Printer, ShieldCheck,
  ChevronRight, ArrowLeft
} from 'lucide-react';
import { FaGithub as Github } from 'react-icons/fa';

export default function PaperViewerModal({ isOpen, paper, onClose }) {
  const [viewerMode, setViewerMode] = useState('gdocs'); // 'gdocs' or 'direct'
  const [isLoading, setIsLoading] = useState(true);
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [hasError, setHasError] = useState(false);
  const modalRef = useRef(null);
  const iframeRef = useRef(null);

  useEffect(() => {
    if (isOpen) {
      setIsLoading(true);
      setHasError(false);
      // Disable body scroll when modal is open
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'unset';
    }

    return () => {
      document.body.style.overflow = 'unset';
    };
  }, [isOpen, paper, viewerMode]);

  useEffect(() => {
    const handleKeyDown = (e) => {
      if (!isOpen) return;
      if (e.key === 'Escape') {
        if (isFullscreen) {
          setIsFullscreen(false);
        } else {
          onClose();
        }
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, isFullscreen, onClose]);

  if (!isOpen || !paper) return null;

  // Google Docs Viewer renders PDFs online without triggering browser download
  const gdocsUrl = `https://docs.google.com/viewer?url=${encodeURIComponent(paper.rawUrl)}&embedded=true`;
  const directUrl = paper.rawUrl;

  const currentViewerUrl = viewerMode === 'gdocs' ? gdocsUrl : directUrl;

  const handleDownload = (e) => {
    e?.stopPropagation();
    const link = document.createElement('a');
    link.href = paper.rawUrl;
    link.download = paper.fileName || 'GATE-Paper.pdf';
    link.target = '_blank';
    link.rel = 'noopener noreferrer';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const handleIframeLoad = () => {
    setIsLoading(false);
  };

  const handleIframeError = () => {
    setIsLoading(false);
    setHasError(true);
  };

  return (
    <div 
      className="fixed inset-0 z-50 flex items-center justify-center p-2 sm:p-4 md:p-6 bg-slate-950/85 backdrop-blur-md animate-in fade-in duration-200"
      onClick={(e) => {
        if (e.target === e.currentTarget) onClose();
      }}
    >
      <div 
        ref={modalRef}
        className={`bg-[#0d1322] border border-slate-700/80 rounded-2xl shadow-2xl flex flex-col overflow-hidden transition-all duration-200 ${
          isFullscreen 
            ? 'w-full h-full rounded-none fixed inset-0 z-50' 
            : 'w-full max-w-6xl h-[92vh] max-h-[950px]'
        }`}
      >
        {/* Top Header Bar */}
        <div className="bg-[#111728] border-b border-slate-800 px-4 py-3 sm:px-6 flex flex-wrap items-center justify-between gap-3 shrink-0">
          {/* Paper Title & Badges */}
          <div className="flex items-center gap-3 min-w-0">
            <button
              onClick={onClose}
              className="sm:hidden p-1.5 rounded-lg bg-slate-800 text-slate-300 hover:text-white hover:bg-slate-700"
              title="Back"
            >
              <ArrowLeft className="w-4 h-4" />
            </button>

            <div className="w-9 h-9 rounded-xl bg-orange-500/10 border border-orange-500/20 text-orange-400 flex items-center justify-center shrink-0">
              <FileText className="w-5 h-5" />
            </div>

            <div className="min-w-0">
              <div className="flex items-center gap-2 flex-wrap">
                <h3 className="text-sm sm:text-base font-bold text-white font-mono truncate">
                  {paper.fileName}
                </h3>
                <span className={`text-[10px] font-bold px-2 py-0.5 rounded font-mono ${
                  paper.code === 'CS' 
                    ? 'bg-orange-500/20 text-orange-400 border border-orange-500/30' 
                    : 'bg-cyan-500/20 text-cyan-400 border border-cyan-500/30'
                }`}>
                  {paper.section || `GATE ${paper.code}`}
                </span>
                <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-slate-800 text-slate-300 font-mono">
                  {paper.year} {paper.session ? `• ${paper.session}` : ''}
                </span>
              </div>
              <p className="text-xs text-slate-400 font-mono flex items-center gap-1.5 mt-0.5">
                <ShieldCheck className="w-3.5 h-3.5 text-emerald-400 inline" />
                <span>Verified authentic paper • {paper.fileSize || '2.5 MB'} • 65 Questions</span>
              </p>
            </div>
          </div>

          {/* Action Buttons & Viewer Engine Controls */}
          <div className="flex items-center gap-2 shrink-0 ml-auto">
            {/* Viewer Engine Toggle */}
            <div className="hidden md:flex items-center bg-[#0a0e17] p-1 rounded-xl border border-slate-800 text-xs">
              <button
                onClick={() => {
                  setViewerMode('gdocs');
                  setIsLoading(true);
                  setHasError(false);
                }}
                className={`px-2.5 py-1 rounded-lg font-semibold transition-all ${
                  viewerMode === 'gdocs'
                    ? 'bg-orange-500 text-white shadow-sm'
                    : 'text-slate-400 hover:text-slate-200'
                }`}
                title="Universal Online Viewer (no download required)"
              >
                Online Reader
              </button>
              <button
                onClick={() => {
                  setViewerMode('direct');
                  setIsLoading(true);
                  setHasError(false);
                }}
                className={`px-2.5 py-1 rounded-lg font-semibold transition-all ${
                  viewerMode === 'direct'
                    ? 'bg-orange-500 text-white shadow-sm'
                    : 'text-slate-400 hover:text-slate-200'
                }`}
                title="Native Browser Stream"
              >
                Native Stream
              </button>
            </div>

            {/* Direct Download Button (For users who wish to download) */}
            <button
              onClick={handleDownload}
              className="inline-flex items-center gap-1.5 bg-gradient-to-r from-orange-500 to-amber-500 hover:from-orange-600 hover:to-amber-600 text-white font-bold text-xs px-3.5 py-2 rounded-xl shadow-md shadow-orange-500/20 transition-all hover:-translate-y-0.5"
              title={`Download ${paper.fileName} (${paper.fileSize})`}
            >
              <Download className="w-3.5 h-3.5" />
              <span>Download PDF</span>
              <span className="hidden lg:inline text-[10px] opacity-80 font-mono">({paper.fileSize})</span>
            </button>

            {/* Open in External Tab */}
            <a
              href={paper.rawUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="hidden sm:inline-flex items-center gap-1 p-2 rounded-xl bg-slate-800/80 hover:bg-slate-700 text-slate-300 hover:text-white text-xs border border-slate-700/60"
              title="Open raw PDF in new tab"
            >
              <ExternalLink className="w-3.5 h-3.5" />
            </a>

            {/* Fullscreen Toggle */}
            <button
              onClick={() => setIsFullscreen(!isFullscreen)}
              className="p-2 rounded-xl bg-slate-800/80 hover:bg-slate-700 text-slate-300 hover:text-white text-xs border border-slate-700/60"
              title={isFullscreen ? "Exit Fullscreen" : "Fullscreen Viewer"}
            >
              {isFullscreen ? <Minimize2 className="w-3.5 h-3.5" /> : <Maximize2 className="w-3.5 h-3.5" />}
            </button>

            {/* Close Button */}
            <button
              onClick={onClose}
              className="p-2 rounded-xl bg-slate-800 hover:bg-red-500/20 hover:text-red-400 text-slate-300 transition-colors border border-slate-700/60"
              title="Close Viewer (Esc)"
            >
              <X className="w-4 h-4" />
            </button>
          </div>
        </div>

        {/* Viewer Body / Container */}
        <div className="relative flex-1 w-full bg-[#0a0f1d] overflow-hidden">
          {/* Loading Indicator */}
          {isLoading && (
            <div className="absolute inset-0 z-20 flex flex-col items-center justify-center bg-[#0a0f1d] text-center p-6">
              <div className="relative mb-4">
                <div className="w-12 h-12 rounded-full border-2 border-orange-500/20 border-t-orange-500 animate-spin" />
                <BookOpen className="w-5 h-5 text-orange-400 absolute inset-0 m-auto" />
              </div>
              <h4 className="text-sm font-bold text-white mb-1">
                Loading Authentic Question Paper
              </h4>
              <p className="text-xs text-slate-400 font-mono max-w-sm mb-4">
                Opening {paper.fileName} in zero-download reader mode...
              </p>
              <div className="flex items-center gap-2 text-[11px] text-slate-500 font-mono">
                <span>Repository: {paper.githubRepo}</span>
                <span>•</span>
                <span>{paper.fileSize}</span>
              </div>
            </div>
          )}

          {/* Fallback / Error Alert */}
          {hasError && (
            <div className="absolute inset-0 z-20 flex flex-col items-center justify-center bg-[#0a0f1d] text-center p-6">
              <div className="w-12 h-12 rounded-2xl bg-amber-500/10 text-amber-400 flex items-center justify-center border border-amber-500/20 mb-4">
                <AlertCircle className="w-6 h-6" />
              </div>
              <h4 className="text-base font-bold text-white mb-2">
                Viewer connection timed out
              </h4>
              <p className="text-xs text-slate-400 max-w-md mb-6 leading-relaxed">
                If your browser or network blocks embedded frames, you can read it directly in a new tab or download the authentic file for offline study.
              </p>
              <div className="flex flex-wrap items-center justify-center gap-3">
                <button
                  onClick={() => {
                    setIsLoading(true);
                    setHasError(false);
                    if (iframeRef.current) {
                      iframeRef.current.src = currentViewerUrl;
                    }
                  }}
                  className="inline-flex items-center gap-1.5 px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-white text-xs font-semibold"
                >
                  <RefreshCw className="w-3.5 h-3.5" />
                  <span>Retry Viewer</span>
                </button>
                <a
                  href={gdocsUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="inline-flex items-center gap-1.5 px-4 py-2 rounded-xl bg-orange-500/20 text-orange-400 hover:bg-orange-500/30 border border-orange-500/30 text-xs font-semibold"
                >
                  <ExternalLink className="w-3.5 h-3.5" />
                  <span>Open Full Viewer in New Tab</span>
                </a>
                <button
                  onClick={handleDownload}
                  className="inline-flex items-center gap-1.5 px-4 py-2 rounded-xl bg-orange-500 text-white hover:bg-orange-600 text-xs font-bold"
                >
                  <Download className="w-3.5 h-3.5" />
                  <span>Download PDF Directly</span>
                </button>
              </div>
            </div>
          )}

          {/* Embedded Viewer Iframe */}
          <iframe
            ref={iframeRef}
            key={`${paper.id}-${viewerMode}`}
            src={currentViewerUrl}
            title={`GATE Question Paper: ${paper.fileName}`}
            className="w-full h-full border-0 bg-white"
            onLoad={handleIframeLoad}
            onError={handleIframeError}
            allow="fullscreen"
          />
        </div>

        {/* Bottom Status & Info Ribbon */}
        <div className="bg-[#111728] border-t border-slate-800/80 px-4 py-2.5 sm:px-6 flex flex-wrap items-center justify-between gap-3 text-xs text-slate-400 font-mono shrink-0">
          <div className="flex items-center gap-3">
            <span className="flex items-center gap-1.5 text-slate-300">
              <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
              <span>Online View Active</span>
            </span>
            <span className="text-slate-600">|</span>
            <span className="hidden sm:inline">No download required to read questions</span>
            <span className="text-slate-600 hidden sm:inline">|</span>
            <a 
              href={paper.repoUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="text-slate-400 hover:text-orange-400 transition-colors inline-flex items-center gap-1"
            >
              <Github className="w-3 h-3" />
              <span>GitHub Source</span>
            </a>
          </div>

          <div className="flex items-center gap-2 ml-auto">
            <button
              onClick={handleDownload}
              className="text-orange-400 hover:text-orange-300 font-bold hover:underline flex items-center gap-1"
            >
              <Download className="w-3 h-3" />
              <span>Save Offline Copy</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
