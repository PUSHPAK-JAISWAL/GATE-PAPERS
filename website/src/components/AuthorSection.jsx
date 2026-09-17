import React from 'react';
import { Mail, Heart, Code2, GitFork, Star, MessageSquare } from 'lucide-react';
import { FaGithub as Github } from 'react-icons/fa';
import { APP_CONFIG } from '../data/papersData';
import { useDynamicPapers } from '../data/DynamicPapersContext';

export default function AuthorSection() {
  const { config } = useDynamicPapers();
  const author = config?.author || APP_CONFIG.author || {
    name: 'Pushpak Jaiswal',
    github: 'PUSHPAK-JAISWAL',
    githubUrl: 'https://github.com/PUSHPAK-JAISWAL',
    email: 'pushpakmjaiswal@gmail.com'
  };

  const authorName = author.name || 'Pushpak Jaiswal';
  const authorGithub = author.github || 'PUSHPAK-JAISWAL';
  const authorGithubUrl = author.githubUrl || 'https://github.com/PUSHPAK-JAISWAL';
  const authorEmail = author.email || 'pushpakmjaiswal@gmail.com';
  const repoUrl = config?.repoUrl || APP_CONFIG.repoUrl || 'https://github.com/PUSHPAK-JAISWAL/gate-papers';

  return (
    <section className="py-20 bg-[#0A0E17] relative border-t border-slate-800">
      <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="p-8 sm:p-12 rounded-3xl bg-gradient-to-br from-[#111726] to-[#0D1322] border border-slate-800 relative overflow-hidden shadow-2xl">
          {/* Subtle background highlight */}
          <div className="absolute top-0 right-0 w-80 h-80 bg-orange-500/5 rounded-full blur-3xl pointer-events-none" />

          <div className="flex flex-col md:flex-row items-center gap-8 relative z-10">
            {/* Avatar & Badge */}
            <div className="relative shrink-0">
              <div className="w-28 h-28 sm:w-32 sm:h-32 rounded-3xl bg-gradient-to-br from-orange-500 to-amber-600 p-1 shadow-xl shadow-orange-500/20">
                <img
                  src="https://github.com/PUSHPAK-JAISWAL.png"
                  alt={authorName}
                  className="w-full h-full object-cover rounded-[22px] bg-slate-900"
                  onError={(e) => {
                    // Fallback initials if GitHub avatar fails to load
                    e.currentTarget.style.display = 'none';
                    if (e.currentTarget.parentElement) {
                      e.currentTarget.parentElement.innerHTML = '<div class="w-full h-full flex items-center justify-center font-black text-2xl text-white">PJ</div>';
                    }
                  }}
                />
              </div>
              <div className="absolute -bottom-2 -right-2 bg-emerald-500 text-black text-[10px] font-bold px-2 py-0.5 rounded-full uppercase tracking-wider shadow">
                Maintainer
              </div>
            </div>

            {/* Content & Social Links */}
            <div className="flex-1 text-center md:text-left space-y-4">
              <div>
                <span className="text-xs uppercase font-mono tracking-widest text-orange-400 font-bold">
                  Created & Maintained By
                </span>
                <h3 className="text-2xl sm:text-3xl font-extrabold text-white mt-1">
                  {authorName}
                </h3>
                <p className="text-sm text-slate-400 mt-1">
                  Passionate Software Engineer & Open Source Contributor
                </p>
              </div>

              <p className="text-sm text-slate-300 leading-relaxed max-w-xl">
                "I built <strong>GATE Papers</strong> to give every GATE CS and DA student a clean, lightning-fast, and completely distraction-free tool to study. No annoying ad popups, no premium paywalls — just pure engineering and high-speed offline preparation."
              </p>

              {/* Action Buttons */}
              <div className="flex flex-wrap items-center justify-center md:justify-start gap-3 pt-2">
                <a
                  href={authorGithubUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-white text-xs font-semibold border border-slate-700 transition-colors shadow-sm"
                >
                  <Github className="w-4 h-4" />
                  <span>@{authorGithub}</span>
                </a>

                <a
                  href={`mailto:${authorEmail}`}
                  className="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-white text-xs font-semibold border border-slate-700 transition-colors shadow-sm"
                >
                  <Mail className="w-4 h-4 text-orange-400" />
                  <span>{authorEmail}</span>
                </a>

                <a
                  href={repoUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-orange-500/10 hover:bg-orange-500/20 text-orange-400 text-xs font-semibold border border-orange-500/30 transition-colors"
                >
                  <Star className="w-4 h-4" />
                  <span>Star on GitHub</span>
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
