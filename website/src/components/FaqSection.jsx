import React, { useState } from 'react';
import { ChevronDown, ChevronUp, HelpCircle } from 'lucide-react';

const FAQS = [
  {
    q: 'Are the question papers official and authentic?',
    a: 'Yes, 100%. All question papers are sourced directly from the official organizing institutes of GATE across respective years (IISc Bangalore, IIT Kanpur, IIT Kharagpur, IIT Bombay, IIT Delhi, IIT Madras). No unofficial, altered, or watermarked scans are included.'
  },
  {
    q: 'Can I read and practice papers completely offline?',
    a: 'Absolutely! When you open a paper in the app for the first time, it is automatically cached in your device’s local storage. You can subsequently open, view, and zoom into question papers anytime without needing an active mobile data or Wi-Fi connection.'
  },
  {
    q: 'Why does Android display "Install unknown apps"?',
    a: 'Because you are downloading the APK directly from our official website instead of the Google Play Store, Android shows a standard security caution. This is normal for all direct APK installs. The app is completely open source, contains zero tracking or adware, and you can inspect every line of code on our public GitHub repository.'
  },
  {
    q: 'Which Android versions are supported?',
    a: 'GATE Papers supports Android 8.0 (Oreo) and above (API Level 26+), up to Android 15. It runs smoothly on smartphones, tablets, and foldable displays with adaptive responsive scaling.'
  },
  {
    q: 'Can I print papers for offline mock tests?',
    a: 'Yes! The app features a native integration with the Android PrintManager. With 1 tap, you can send the question paper to a wireless Wi-Fi printer or export it as a standard PDF file to practice in a realistic timed exam environment.'
  },
  {
    q: 'Is the app really completely free with zero ads?',
    a: 'Yes. GATE Papers is a non-profit open-source initiative built by Pushpak Jaiswal to support the engineering student community. There are no subscriptions, in-app purchases, or advertisement banners.'
  }
];

export default function FaqSection() {
  const [openIndex, setOpenIndex] = useState(0);

  return (
    <section id="faq" className="py-24 bg-[#0B101D] relative border-t border-slate-800">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-16">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-orange-500/10 border border-orange-500/30 text-orange-400 text-xs font-semibold uppercase tracking-wider mb-3">
            <HelpCircle className="w-3.5 h-3.5" />
            Got Questions?
          </div>
          <h2 className="text-3xl sm:text-4xl font-extrabold text-white tracking-tight mb-4">
            Frequently Asked Questions
          </h2>
          <p className="text-slate-400 text-base">
            Everything you need to know about the GATE Papers Android app and study workflow.
          </p>
        </div>

        <div className="space-y-4">
          {FAQS.map((faq, idx) => {
            const isOpen = openIndex === idx;
            return (
              <div
                key={idx}
                className="rounded-2xl bg-[#111726] border border-slate-800 overflow-hidden transition-all"
              >
                <button
                  onClick={() => setOpenIndex(isOpen ? -1 : idx)}
                  className="w-full p-6 text-left flex items-center justify-between gap-4 focus:outline-none"
                >
                  <span className="text-base sm:text-lg font-bold text-white">
                    {faq.q}
                  </span>
                  <span className="p-1 rounded-lg bg-slate-800 text-orange-400 shrink-0">
                    {isOpen ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
                  </span>
                </button>

                {isOpen && (
                  <div className="px-6 pb-6 pt-1 text-slate-300 text-sm leading-relaxed border-t border-slate-800/60">
                    {faq.a}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
}
