import React from 'react';
import { Download, Settings, CheckCircle2, ShieldAlert, Smartphone, ArrowRight } from 'lucide-react';
import { useDynamicPapers } from '../data/DynamicPapersContext';

export default function InstallGuide({ onDownloadClick }) {
  const { stats, config } = useDynamicPapers();

  const steps = [
    {
      num: '01',
      title: 'Download the APK',
      desc: `Tap the Download button directly on this website. The signed package file (${config.apkName || 'GATE-Papers.apk'}) will begin downloading immediately to your Android device.`,
      badge: 'Direct Download',
      badgeColor: 'text-orange-400 bg-orange-500/10 border-orange-500/20'
    },
    {
      num: '02',
      title: 'Allow Installation',
      desc: 'Open the downloaded file from your notification bar or Downloads folder. If Android prompts "Install unknown apps", simply toggle "Allow from this source" for Chrome or your browser.',
      badge: 'Standard Android Security',
      badgeColor: 'text-cyan-400 bg-cyan-500/10 border-cyan-500/20'
    },
    {
      num: '03',
      title: 'Install & Start Practicing',
      desc: 'Tap "Install" on the system dialog. Once installed, launch GATE Papers from your app drawer and begin your offline preparation right away!',
      badge: 'Zero Configuration',
      badgeColor: 'text-emerald-400 bg-emerald-500/10 border-emerald-500/20'
    }
  ];

  return (
    <section id="install" className="py-24 bg-[#0A0E17] relative border-t border-slate-800">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center max-w-3xl mx-auto mb-16">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 text-xs font-semibold uppercase tracking-wider mb-3">
            <Smartphone className="w-3.5 h-3.5" />
            Simple Installation
          </div>
          <h2 className="text-3xl sm:text-4xl font-extrabold text-white tracking-tight mb-4">
            How to Install on Android in 3 Simple Steps
          </h2>
          <p className="text-slate-400 text-base sm:text-lg">
            No need to navigate complicated GitHub release pages. Install the APK directly on any Android smartphone or tablet.
          </p>
        </div>

        {/* Steps Grid */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-14">
          {steps.map((step, idx) => (
            <div
              key={idx}
              className="p-8 rounded-2xl bg-[#111726] border border-slate-800 relative hover:border-slate-700 transition-all flex flex-col justify-between"
            >
              <div>
                <div className="flex items-center justify-between mb-6">
                  <span className="text-3xl font-extrabold text-slate-700 font-mono">
                    {step.num}
                  </span>
                  <span className={`text-xs font-semibold px-2.5 py-1 rounded-md border ${step.badgeColor}`}>
                    {step.badge}
                  </span>
                </div>
                <h3 className="text-xl font-bold text-white mb-3">{step.title}</h3>
                <p className="text-sm text-slate-300 leading-relaxed">{step.desc}</p>
              </div>

              <div className="mt-6 pt-4 border-t border-slate-800/80 flex items-center gap-2 text-xs text-slate-400">
                <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
                <span>Compatible with Android 8.0 to Android 15</span>
              </div>
            </div>
          ))}
        </div>

        {/* Action Bar */}
        <div className="flex flex-col sm:flex-row items-center justify-center gap-4">
          <button
            onClick={onDownloadClick}
            className="w-full sm:w-auto inline-flex items-center justify-center gap-2.5 bg-orange-500 hover:bg-orange-600 text-white font-bold text-base px-8 py-4 rounded-xl shadow-xl shadow-orange-500/25 transition-all hover:-translate-y-0.5"
          >
            <Download className="w-5 h-5" />
            <span>Download APK Now ({stats.apkSize})</span>
          </button>

          <a
            href={config.repoUrl ? `${config.repoUrl}/releases` : 'https://github.com/PUSHPAK-JAISWAL/gate-papers/releases'}
            target="_blank"
            rel="noopener noreferrer"
            className="text-xs text-slate-400 hover:text-slate-200 underline underline-offset-4"
          >
            Prefer GitHub Releases? View release logs & checksums →
          </a>
        </div>
      </div>
    </section>
  );
}
