import React from 'react';
import { Download, QrCode, ShieldCheck, Zap, Sparkles, CheckCircle2, ChevronRight, FileText, Printer, WifiOff } from 'lucide-react';
import { APP_CONFIG } from '../data/papersData';

export default function Hero({ onDownloadClick, onOpenQrModal }) {
  return (
    <section className="relative pt-32 pb-20 md:pt-40 md:pb-28 overflow-hidden">
      {/* Background Ambient Glows */}
      <div className="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] ambient-glow rounded-full blur-3xl pointer-events-none -z-10" />
      <div className="absolute top-1/3 left-1/4 w-[400px] h-[400px] ambient-cyan-glow rounded-full blur-3xl pointer-events-none -z-10" />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center max-w-3xl mx-auto">
          {/* Badge */}
          <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-slate-900/90 border border-orange-500/30 text-orange-400 text-xs sm:text-sm font-medium mb-6 shadow-inner shadow-orange-500/10">
            <span className="flex h-2 w-2 rounded-full bg-orange-500 animate-ping" />
            <span>Latest Release {APP_CONFIG.version} is Live</span>
            <span className="text-slate-600">•</span>
            <span className="text-slate-300 font-semibold">GATE CS & DA 2013–2026 (27 Authentic Papers)</span>
          </div>

          {/* Main Title */}
          <h1 className="text-4xl sm:text-5xl md:text-6xl font-extrabold text-white tracking-tight leading-[1.15] mb-6">
            Official GATE Papers (2013–2026) with{' '}
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-orange-400 via-amber-400 to-orange-500">
              Zero Distractions
            </span>
          </h1>

          {/* Subtitle */}
          <p className="text-lg sm:text-xl text-slate-300 mb-10 leading-relaxed max-w-2xl mx-auto">
            The free, open-source Android study companion for <strong>GATE CS (Computer Science)</strong> & <strong>GATE DA (Data Science & AI)</strong> aspirants. 
            Native in-app PDF rendering, 100% authentic past year question papers (PYQs), year/name sorting, offline Room database, solved progress tracking, and wireless printing.
          </p>

          {/* Download & Action Buttons */}
          <div className="flex flex-col sm:flex-row items-center justify-center gap-4 mb-12">
            <button
              onClick={onDownloadClick}
              className="w-full sm:w-auto inline-flex items-center justify-center gap-3 bg-gradient-to-r from-orange-500 to-amber-500 hover:from-orange-600 hover:to-amber-600 text-white font-bold text-base px-8 py-4 rounded-2xl shadow-xl shadow-orange-500/30 hover:shadow-orange-500/50 hover:-translate-y-1 transition-all duration-200 group"
            >
              <Download className="w-5 h-5 group-hover:animate-bounce" />
              <span>Download Direct APK</span>
              <span className="text-xs bg-black/20 font-mono px-2 py-0.5 rounded-full text-orange-100">
                {APP_CONFIG.apkSize}
              </span>
            </button>

            <button
              onClick={onOpenQrModal}
              className="w-full sm:w-auto inline-flex items-center justify-center gap-2.5 bg-[#111726] hover:bg-[#162035] text-slate-200 font-semibold text-base px-6 py-4 rounded-2xl border border-slate-700/80 hover:border-slate-600 transition-all duration-200 shadow-md"
            >
              <QrCode className="w-5 h-5 text-orange-400" />
              <span>Scan QR Code</span>
            </button>

            <a
              href="#simulator"
              className="w-full sm:w-auto inline-flex items-center justify-center gap-2 text-cyan-400 hover:text-cyan-300 font-medium text-sm px-4 py-2 hover:underline"
            >
              <span>Try Interactive Simulator</span>
              <ChevronRight className="w-4 h-4" />
            </a>
          </div>

          {/* Trust & Spec Badges */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 max-w-3xl mx-auto text-left">
            <div className="flex items-center gap-2.5 p-3 rounded-xl bg-slate-900/50 border border-slate-800">
              <ShieldCheck className="w-5 h-5 text-emerald-400 shrink-0" />
              <div>
                <p className="text-xs font-semibold text-white">100% Free & Open</p>
                <p className="text-[11px] text-slate-400">Zero Ads, No Login</p>
              </div>
            </div>

            <div className="flex items-center gap-2.5 p-3 rounded-xl bg-slate-900/50 border border-slate-800">
              <WifiOff className="w-5 h-5 text-cyan-400 shrink-0" />
              <div>
                <p className="text-xs font-semibold text-white">Offline First</p>
                <p className="text-[11px] text-slate-400">Local Room SQLite</p>
              </div>
            </div>

            <div className="flex items-center gap-2.5 p-3 rounded-xl bg-slate-900/50 border border-slate-800">
              <FileText className="w-5 h-5 text-orange-400 shrink-0" />
              <div>
                <p className="text-xs font-semibold text-white">Built-in Reader</p>
                <p className="text-[11px] text-slate-400">Native PDF Engine</p>
              </div>
            </div>

            <div className="flex items-center gap-2.5 p-3 rounded-xl bg-slate-900/50 border border-slate-800">
              <Printer className="w-5 h-5 text-purple-400 shrink-0" />
              <div>
                <p className="text-xs font-semibold text-white">Print & Export</p>
                <p className="text-[11px] text-slate-400">1-Click Android Print</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
