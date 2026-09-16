import React from 'react';
import { X, Smartphone, Download, ExternalLink, CheckCircle } from 'lucide-react';
import { APP_CONFIG } from '../data/papersData';

export default function QrCodeModal({ isOpen, onClose }) {
  if (!isOpen) return null;

  // We can use a free, fast, reliable QR code generator image API or fallback direct link
  const qrCodeUrl = `https://api.qrserver.com/v1/create-qr-code/?size=240x240&data=${encodeURIComponent(
    APP_CONFIG.apkDirectUrl
  )}&bgcolor=0A0E17&color=F97316&margin=1`;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm animate-in fade-in duration-200">
      <div className="relative w-full max-w-md bg-[#111726] border border-slate-700/80 rounded-2xl p-6 sm:p-8 shadow-2xl shadow-orange-500/10 text-center">
        {/* Close button */}
        <button
          onClick={onClose}
          className="absolute top-4 right-4 p-2 text-slate-400 hover:text-white rounded-lg hover:bg-slate-800 transition-colors"
          aria-label="Close modal"
        >
          <X className="w-5 h-5" />
        </button>

        <div className="inline-flex items-center justify-center w-12 h-12 rounded-xl bg-orange-500/10 text-orange-400 border border-orange-500/20 mb-4">
          <Smartphone className="w-6 h-6" />
        </div>

        <h3 className="text-xl font-bold text-white mb-1">Scan to Install on Android</h3>
        <p className="text-sm text-slate-400 mb-6">
          Point your phone camera to download the APK directly to your device
        </p>

        {/* QR Code Container */}
        <div className="flex justify-center mb-6">
          <div className="p-3 bg-[#0A0E17] border-2 border-orange-500/40 rounded-xl shadow-lg relative group">
            <img
              src={qrCodeUrl}
              alt="Scan QR code to download GATE Papers APK"
              className="w-48 h-48 rounded-lg"
              loading="lazy"
            />
          </div>
        </div>

        <div className="space-y-2 mb-6 text-left bg-slate-900/60 p-3.5 rounded-xl border border-slate-800 text-xs text-slate-300">
          <div className="flex items-center gap-2">
            <CheckCircle className="w-4 h-4 text-emerald-400 shrink-0" />
            <span>Direct APK: <strong>{APP_CONFIG.apkName}</strong> (~{APP_CONFIG.apkSize})</span>
          </div>
          <div className="flex items-center gap-2">
            <CheckCircle className="w-4 h-4 text-emerald-400 shrink-0" />
            <span>Supports Android 8.0+ (Oreo, Pie, 10, 11, 12, 13, 14, 15)</span>
          </div>
          <div className="flex items-center gap-2">
            <CheckCircle className="w-4 h-4 text-emerald-400 shrink-0" />
            <span>Zero malware, open source & cryptographically signed</span>
          </div>
        </div>

        <div className="flex flex-col sm:flex-row gap-2.5">
          <a
            href={APP_CONFIG.apkDirectUrl}
            className="flex-1 inline-flex items-center justify-center gap-2 bg-orange-500 hover:bg-orange-600 text-white font-semibold py-2.5 px-4 rounded-xl text-sm transition-colors shadow-md shadow-orange-500/20"
          >
            <Download className="w-4 h-4" />
            <span>Direct Download Now</span>
          </a>
          <button
            onClick={onClose}
            className="sm:w-28 py-2.5 px-4 bg-slate-800 hover:bg-slate-700 text-slate-300 font-medium rounded-xl text-sm transition-colors"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
}
