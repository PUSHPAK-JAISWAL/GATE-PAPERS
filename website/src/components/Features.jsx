import React from 'react';
import { 
  FileText, WifiOff, CheckCircle2, Printer, Shield, Smartphone, 
  DownloadCloud, Layers, Cpu, Compass, BookCheck, Zap
} from 'lucide-react';

const FEATURE_LIST = [
  {
    icon: FileText,
    iconColor: 'text-orange-400',
    iconBg: 'bg-orange-500/10 border-orange-500/20',
    title: 'Native In-App PDF Reader',
    description: 'No bloated 3rd-party PDF viewers with full-screen ads. Read GATE question papers directly inside the app with fluid pan, pinch-to-zoom, and page jumping.'
  },
  {
    icon: WifiOff,
    iconColor: 'text-cyan-400',
    iconBg: 'bg-cyan-500/10 border-cyan-500/20',
    title: '100% Offline First Caching',
    description: 'Papers are automatically cached on device after first view. Study in libraries, trains, or low-connectivity zones without interruptions.'
  },
  {
    icon: BookCheck,
    iconColor: 'text-emerald-400',
    iconBg: 'bg-emerald-500/10 border-emerald-500/20',
    title: 'Preparation Progress Tracker',
    description: 'Mark papers as Finished or keep them Pending. Backed by Room SQLite, your progress stays saved permanently across app restarts.'
  },
  {
    icon: Printer,
    iconColor: 'text-purple-400',
    iconBg: 'bg-purple-500/10 border-purple-500/20',
    title: '1-Click Android Print & Export',
    description: 'Directly hooks into Android PrintManager. Spool papers to your home printer or export high-resolution printable PDFs for real exam mock drills.'
  },
  {
    icon: DownloadCloud,
    iconColor: 'text-blue-400',
    iconBg: 'bg-blue-500/10 border-blue-500/20',
    title: 'Direct GitHub Sync',
    description: 'Paper indices and PDFs are served through verified, raw GitHub repository links, guaranteeing official question papers and instant updates.'
  },
  {
    icon: Shield,
    iconColor: 'text-amber-400',
    iconBg: 'bg-amber-500/10 border-amber-500/20',
    title: 'Zero Ads & Privacy First',
    description: 'No signups, no analytics trackers, no promotional pop-ups. Crafted purely by an engineer for students who respect their time and mental focus.'
  }
];

export default function Features() {
  return (
    <section id="features" className="py-24 bg-[#0A0E17] relative border-t border-slate-800/80">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="text-center max-w-3xl mx-auto mb-16">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-orange-500/10 border border-orange-500/30 text-orange-400 text-xs font-semibold uppercase tracking-wider mb-3">
            <Zap className="w-3.5 h-3.5" />
            Engineered for Aspirants
          </div>
          <h2 className="text-3xl sm:text-4xl font-extrabold text-white tracking-tight mb-4">
            Everything You Need to Crack GATE
          </h2>
          <p className="text-slate-400 text-base sm:text-lg">
            Built using modern Jetpack Compose and clean Android architecture. Here is why GATE Papers is the study companion students trust.
          </p>
        </div>

        {/* Feature Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {FEATURE_LIST.map((feat, idx) => {
            const Icon = feat.icon;
            return (
              <div
                key={idx}
                className="p-8 rounded-2xl bg-[#111726] border border-slate-800 hover:border-orange-500/40 transition-all duration-300 hover:-translate-y-1 shadow-lg hover:shadow-orange-500/5 group"
              >
                <div className={`w-12 h-12 rounded-xl flex items-center justify-center border mb-6 ${feat.iconBg} ${feat.iconColor} group-hover:scale-110 transition-transform duration-300`}>
                  <Icon className="w-6 h-6" />
                </div>
                <h3 className="text-xl font-bold text-white mb-3 group-hover:text-orange-400 transition-colors">
                  {feat.title}
                </h3>
                <p className="text-sm text-slate-300 leading-relaxed">
                  {feat.description}
                </p>
              </div>
            );
          })}
        </div>

        {/* Comparison Highlight Banner */}
        <div className="mt-16 p-8 rounded-3xl bg-gradient-to-r from-[#111726] via-[#162035] to-[#111726] border border-slate-700/80 shadow-2xl">
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 items-center">
            <div className="lg:col-span-2 space-y-3">
              <span className="text-xs uppercase font-mono tracking-widest text-orange-400 font-bold">
                Why Choose GATE Papers Over Web Portals?
              </span>
              <h3 className="text-2xl sm:text-3xl font-bold text-white">
                Eliminate Dead Links, Spam Portals, and Broken Downloads
              </h3>
              <p className="text-sm text-slate-300 leading-relaxed">
                Most educational websites force you through captcha walls, spam notifications, and outdated Google Drive links. GATE Papers delivers authentic papers in a clean, lightweight native Android package with zero clutter.
              </p>
            </div>
            <div className="flex flex-col sm:flex-row lg:flex-col gap-3 justify-center">
              <div className="flex items-center gap-3 bg-slate-900/80 p-3 rounded-xl border border-slate-800">
                <span className="text-emerald-400 text-lg font-bold">✓</span>
                <span className="text-xs text-slate-200">100% Native Kotlin & Jetpack Compose</span>
              </div>
              <div className="flex items-center gap-3 bg-slate-900/80 p-3 rounded-xl border border-slate-800">
                <span className="text-emerald-400 text-lg font-bold">✓</span>
                <span className="text-xs text-slate-200">Under 5 MB Compact App Size</span>
              </div>
              <div className="flex items-center gap-3 bg-slate-900/80 p-3 rounded-xl border border-slate-800">
                <span className="text-emerald-400 text-lg font-bold">✓</span>
                <span className="text-xs text-slate-200">Official IISc & IIT Question Sets</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
