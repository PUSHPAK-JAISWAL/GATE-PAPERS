import React, { useState, useEffect } from 'react';
import { Download, Github, Menu, X, BookOpen, Smartphone, HelpCircle, Layers } from 'lucide-react';
import { APP_CONFIG } from '../data/papersData';

export default function Navbar({ onDownloadClick }) {
  const [scrolled, setScrolled] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setScrolled(window.scrollY > 20);
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  return (
    <nav
      className={`fixed top-0 left-0 right-0 z-50 transition-all duration-300 ${
        scrolled
          ? 'bg-[#0A0E17]/90 backdrop-blur-md border-b border-slate-800 shadow-xl shadow-black/40 py-3'
          : 'bg-transparent py-5'
      }`}
    >
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between">
          {/* Logo & Brand */}
          <a href="#" className="flex items-center space-x-3 group">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-orange-500 to-amber-600 p-0.5 shadow-lg shadow-orange-500/20 group-hover:shadow-orange-500/40 transition-all duration-300">
              <div className="w-full h-full bg-[#0A0E17] rounded-[10px] flex items-center justify-center">
                <BookOpen className="w-5 h-5 text-orange-400 group-hover:scale-110 transition-transform duration-300" />
              </div>
            </div>
            <div>
              <span className="text-xl font-bold tracking-tight text-white flex items-center gap-1.5">
                GATE <span className="text-orange-500">Papers</span>
                <span className="text-[10px] uppercase font-mono px-1.5 py-0.5 rounded bg-cyan-500/10 text-cyan-400 border border-cyan-500/20">
                  Android
                </span>
              </span>
              <p className="text-[11px] text-slate-400 hidden sm:block">CS & DA Study Companion</p>
            </div>
          </a>

          {/* Desktop Nav Links */}
          <div className="hidden md:flex items-center space-x-1 lg:space-x-2">
            <a
              href="#features"
              className="px-3 py-2 text-sm font-medium text-slate-300 hover:text-orange-400 hover:bg-slate-800/40 rounded-lg transition-colors"
            >
              Features
            </a>
            <a
              href="#simulator"
              className="px-3 py-2 text-sm font-medium text-slate-300 hover:text-cyan-400 hover:bg-slate-800/40 rounded-lg transition-colors flex items-center gap-1.5"
            >
              <Smartphone className="w-4 h-4 text-cyan-400" />
              Live Simulator
            </a>
            <a
              href="#papers"
              className="px-3 py-2 text-sm font-medium text-slate-300 hover:text-orange-400 hover:bg-slate-800/40 rounded-lg transition-colors"
            >
              Paper Catalog
            </a>
            <a
              href="#install"
              className="px-3 py-2 text-sm font-medium text-slate-300 hover:text-orange-400 hover:bg-slate-800/40 rounded-lg transition-colors"
            >
              How to Install
            </a>
            <a
              href="#faq"
              className="px-3 py-2 text-sm font-medium text-slate-300 hover:text-orange-400 hover:bg-slate-800/40 rounded-lg transition-colors"
            >
              FAQ
            </a>
          </div>

          {/* CTA Buttons */}
          <div className="hidden sm:flex items-center space-x-3">
            <a
              href={APP_CONFIG.repoUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="p-2.5 text-slate-300 hover:text-white hover:bg-slate-800/60 rounded-lg transition-colors border border-slate-800 hover:border-slate-700"
              title="GitHub Repository"
            >
              <Github className="w-5 h-5" />
            </a>

            <button
              onClick={onDownloadClick}
              className="inline-flex items-center space-x-2 bg-gradient-to-r from-orange-500 to-amber-500 hover:from-orange-600 hover:to-amber-600 text-white font-semibold text-sm px-4 py-2.5 rounded-xl shadow-lg shadow-orange-500/25 hover:shadow-orange-500/40 hover:-translate-y-0.5 transition-all duration-200"
            >
              <Download className="w-4 h-4" />
              <span>Download APK</span>
              <span className="text-xs bg-white/20 px-1.5 py-0.5 rounded font-mono">
                {APP_CONFIG.version}
              </span>
            </button>
          </div>

          {/* Mobile Menu Toggle */}
          <div className="flex md:hidden items-center space-x-2">
            <button
              onClick={onDownloadClick}
              className="inline-flex items-center space-x-1.5 bg-orange-500 hover:bg-orange-600 text-white font-semibold text-xs px-3 py-2 rounded-lg shadow-md shadow-orange-500/20"
            >
              <Download className="w-3.5 h-3.5" />
              <span>APK</span>
            </button>
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="p-2 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 focus:outline-none"
              aria-label="Toggle menu"
            >
              {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile Menu Dropdown */}
      {mobileMenuOpen && (
        <div className="md:hidden bg-[#0D1321] border-b border-slate-800 px-4 pt-3 pb-6 space-y-2 shadow-2xl">
          <a
            href="#features"
            onClick={() => setMobileMenuOpen(false)}
            className="block px-3 py-2 rounded-md text-base font-medium text-slate-300 hover:bg-slate-800 hover:text-white"
          >
            Features
          </a>
          <a
            href="#simulator"
            onClick={() => setMobileMenuOpen(false)}
            className="block px-3 py-2 rounded-md text-base font-medium text-cyan-400 hover:bg-slate-800"
          >
            Live App Simulator
          </a>
          <a
            href="#papers"
            onClick={() => setMobileMenuOpen(false)}
            className="block px-3 py-2 rounded-md text-base font-medium text-slate-300 hover:bg-slate-800 hover:text-white"
          >
            Paper Catalog
          </a>
          <a
            href="#install"
            onClick={() => setMobileMenuOpen(false)}
            className="block px-3 py-2 rounded-md text-base font-medium text-slate-300 hover:bg-slate-800 hover:text-white"
          >
            Installation Guide
          </a>
          <a
            href="#faq"
            onClick={() => setMobileMenuOpen(false)}
            className="block px-3 py-2 rounded-md text-base font-medium text-slate-300 hover:bg-slate-800 hover:text-white"
          >
            FAQ
          </a>
          <div className="pt-4 border-t border-slate-800 flex items-center justify-between">
            <a
              href={APP_CONFIG.repoUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="flex items-center gap-2 text-slate-400 hover:text-white text-sm"
            >
              <Github className="w-5 h-5" />
              <span>GitHub Repo</span>
            </a>
            <span className="text-xs text-slate-500 font-mono">By Pushpak Jaiswal</span>
          </div>
        </div>
      )}
    </nav>
  );
}
