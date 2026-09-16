import React, { useState } from 'react';
import Navbar from './components/Navbar';
import Hero from './components/Hero';
import AppSimulator from './components/AppSimulator';
import Features from './components/Features';
import PapersCatalog from './components/PapersCatalog';
import InstallGuide from './components/InstallGuide';
import FaqSection from './components/FaqSection';
import AuthorSection from './components/AuthorSection';
import Footer from './components/Footer';
import QrCodeModal from './components/QrCodeModal';
import { APP_CONFIG } from './data/papersData';
import { CheckCircle2, Download, AlertCircle, X } from 'lucide-react';

export default function App() {
  const [isQrModalOpen, setIsQrModalOpen] = useState(false);
  const [downloadNotification, setDownloadNotification] = useState(false);

  const handleDownload = () => {
    // 1. Trigger the download immediately in the browser
    const link = document.createElement('a');
    link.href = APP_CONFIG.apkDirectUrl;
    link.setAttribute('download', APP_CONFIG.apkName);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    // 2. Show on-screen installation notice toast
    setDownloadNotification(true);
    setTimeout(() => {
      setDownloadNotification(false);
    }, 8000);
  };

  return (
    <div className="min-h-screen bg-[#0A0E17] text-slate-100 selection:bg-orange-500 selection:text-white relative">
      {/* Floating Download Started Toast */}
      {downloadNotification && (
        <div className="fixed bottom-6 right-6 z-50 max-w-sm w-full bg-[#111726] border border-orange-500/50 p-4 rounded-2xl shadow-2xl shadow-orange-500/20 animate-in slide-in-from-bottom-5 duration-300">
          <div className="flex items-start gap-3">
            <div className="w-9 h-9 rounded-xl bg-orange-500/20 text-orange-400 flex items-center justify-center shrink-0">
              <Download className="w-5 h-5 animate-pulse" />
            </div>
            <div className="flex-1">
              <h4 className="text-sm font-bold text-white flex items-center gap-1.5">
                <span>Download Started!</span>
                <span className="text-[10px] bg-emerald-500/20 text-emerald-400 px-1.5 py-0.5 rounded font-mono">
                  {APP_CONFIG.apkSize}
                </span>
              </h4>
              <p className="text-xs text-slate-300 mt-1 leading-relaxed">
                Check your browser downloads for <strong>{APP_CONFIG.apkName}</strong>. Tap the file and select "Install" to start practicing!
              </p>
              <div className="mt-2 text-[11px] text-orange-300 flex items-center gap-1">
                <span>Need help?</span>
                <a href="#install" className="underline font-semibold">View 3-step install guide</a>
              </div>
            </div>
            <button
              onClick={() => setDownloadNotification(false)}
              className="text-slate-400 hover:text-white p-1"
              aria-label="Dismiss toast"
            >
              <X className="w-4 h-4" />
            </button>
          </div>
        </div>
      )}

      {/* Navigation */}
      <Navbar onDownloadClick={handleDownload} />

      {/* Hero Section */}
      <Hero
        onDownloadClick={handleDownload}
        onOpenQrModal={() => setIsQrModalOpen(true)}
      />

      {/* Interactive App Simulator */}
      <AppSimulator />

      {/* Key Features Breakdown */}
      <Features />

      {/* Verified Papers Catalog Directory */}
      <PapersCatalog onDownloadClick={handleDownload} />

      {/* Step-by-Step Android Installation Guide */}
      <InstallGuide onDownloadClick={handleDownload} />

      {/* Aspirants FAQs */}
      <FaqSection />

      {/* Author & Maintainer Spotlight */}
      <AuthorSection />

      {/* Footer */}
      <Footer />

      {/* QR Code Modal for Phone Scanning */}
      <QrCodeModal
        isOpen={isQrModalOpen}
        onClose={() => setIsQrModalOpen(false)}
      />
    </div>
  );
}
