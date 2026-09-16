import React from 'react';
import { BookOpen, Github, Mail, Heart, ArrowUp } from 'lucide-react';
import { APP_CONFIG } from '../data/papersData';

export default function Footer() {
  const scrollToTop = () => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <footer className="bg-[#070A10] border-t border-slate-900 text-slate-400 text-xs py-12">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col md:flex-row items-center justify-between gap-6 pb-8 border-b border-slate-900">
          {/* Brand */}
          <div className="flex items-center space-x-3">
            <div className="w-8 h-8 rounded-lg bg-orange-500 flex items-center justify-center text-white">
              <BookOpen className="w-4 h-4" />
            </div>
            <div>
              <span className="text-base font-bold text-white">
                GATE <span className="text-orange-500">Papers</span>
              </span>
              <p className="text-[11px] text-slate-500">Android Study Companion for CS & DA Aspirants</p>
            </div>
          </div>

          {/* Links */}
          <div className="flex flex-wrap items-center gap-6 text-slate-400">
            <a href="#features" className="hover:text-white transition-colors">Features</a>
            <a href="#simulator" className="hover:text-white transition-colors">Simulator</a>
            <a href="#papers" className="hover:text-white transition-colors">Papers</a>
            <a href="#install" className="hover:text-white transition-colors">Install Guide</a>
            <a href="#faq" className="hover:text-white transition-colors">FAQ</a>
            <a
              href={`${APP_CONFIG.repoUrl}/blob/main/LICENSE`}
              target="_blank"
              rel="noopener noreferrer"
              className="hover:text-white transition-colors"
            >
              MIT License
            </a>
          </div>

          {/* Back to top */}
          <button
            onClick={scrollToTop}
            className="p-2 rounded-lg bg-slate-900 hover:bg-slate-800 text-slate-400 hover:text-white transition-colors flex items-center gap-1.5"
            aria-label="Back to top"
          >
            <span>Top</span>
            <ArrowUp className="w-3.5 h-3.5" />
          </button>
        </div>

        <div className="pt-8 flex flex-col sm:flex-row items-center justify-between gap-4 text-center sm:text-left text-slate-500">
          <p>
            © 2026 <strong>Pushpak Jaiswal</strong>. Free and open source under the MIT License.
          </p>
          <div className="flex items-center gap-4">
            <a
              href={APP_CONFIG.author.githubUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="hover:text-slate-300 transition-colors flex items-center gap-1"
            >
              <Github className="w-4 h-4" />
              <span>@PUSHPAK-JAISWAL</span>
            </a>
            <a
              href={`mailto:${APP_CONFIG.author.email}`}
              className="hover:text-slate-300 transition-colors flex items-center gap-1"
            >
              <Mail className="w-4 h-4" />
              <span>Contact</span>
            </a>
          </div>
        </div>
      </div>
    </footer>
  );
}
