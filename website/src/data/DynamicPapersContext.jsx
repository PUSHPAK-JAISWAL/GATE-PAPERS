import React, { createContext, useContext, useState, useEffect } from 'react';
import { PAPERS_DATA, APP_CONFIG, formatVersion } from './papersData';

const DynamicPapersContext = createContext(null);

function computeStats(papersList, version, apkSize) {
  const safePapers = papersList && papersList.length > 0 ? papersList : PAPERS_DATA;
  const csPapers = safePapers.filter(p => p.code === 'CS');
  const daPapers = safePapers.filter(p => p.code === 'DA');

  const allYears = safePapers.map(p => p.year).filter(y => typeof y === 'number' && !isNaN(y));
  const csYears = csPapers.map(p => p.year).filter(y => typeof y === 'number' && !isNaN(y));
  const daYears = daPapers.map(p => p.year).filter(y => typeof y === 'number' && !isNaN(y));

  const minYear = allYears.length > 0 ? Math.min(...allYears) : 2007;
  const maxYear = allYears.length > 0 ? Math.max(...allYears) : 2026;

  const csMinYear = csYears.length > 0 ? Math.min(...csYears) : 2007;
  const csMaxYear = csYears.length > 0 ? Math.max(...csYears) : 2026;

  const daMinYear = daYears.length > 0 ? Math.min(...daYears) : 2024;
  const daMaxYear = daYears.length > 0 ? Math.max(...daYears) : 2026;

  const yearRange = `${minYear}–${maxYear}`;
  const csYearRange = `${csMinYear}–${csMaxYear}`;
  const daYearRange = `${daMinYear}–${daMaxYear}`;

  const normalizedVersion = formatVersion(version || APP_CONFIG.version);

  return {
    totalPapers: safePapers.length,
    csPapersCount: csPapers.length,
    daPapersCount: daPapers.length,
    minYear,
    maxYear,
    yearRange,
    csMinYear,
    csMaxYear,
    csYearRange,
    daMinYear,
    daMaxYear,
    daYearRange,
    version: `v${normalizedVersion}`,
    versionNumber: normalizedVersion,
    apkSize: apkSize || '30 MB'
  };
}

export function DynamicPapersProvider({ children }) {
  const [papers, setPapers] = useState(PAPERS_DATA);
  const [liveConfig, setLiveConfig] = useState(APP_CONFIG);
  const [isLiveSyncing, setIsLiveSyncing] = useState(false);
  const [isLiveSynced, setIsLiveSynced] = useState(false);

  useEffect(() => {
    let isMounted = true;

    async function fetchDynamicData() {
      setIsLiveSyncing(true);
      try {
        // 1. Fetch latest release info to get true release tag & apk asset
        let fetchedVersion = APP_CONFIG.version;
        let fetchedApkSize = '30 MB';
        let fetchedDownloadUrl = APP_CONFIG.downloadApkUrl;

        try {
          const relRes = await fetch('https://api.github.com/repos/PUSHPAK-JAISWAL/gate-papers/releases/latest');
          if (relRes.ok) {
            const relData = await relRes.json();
            if (relData.tag_name) {
              fetchedVersion = `v${formatVersion(relData.tag_name)}`;
            }
            const apkAsset = relData.assets?.find(a => a.name.endsWith('.apk'));
            if (apkAsset) {
              fetchedDownloadUrl = apkAsset.browser_download_url;
              if (apkAsset.size > 0) {
                fetchedApkSize = `${Math.round(apkAsset.size / (1024 * 1024))} MB`;
              }
            }
          }
        } catch (err) {
          console.warn('GitHub Releases check skipped/rate-limited:', err);
        }

        // 2. Fetch CS and DA repos contents dynamically
        let csItems = [];
        let daItems = [];

        try {
          const csRes = await fetch('https://api.github.com/repos/PUSHPAK-JAISWAL/gatecs/contents');
          if (csRes.ok) {
            const csJson = await csRes.json();
            if (Array.isArray(csJson)) csItems = csJson;
          }
        } catch (err) {
          console.warn('GATE CS dynamic fetch skipped:', err);
        }

        try {
          const daRes = await fetch('https://api.github.com/repos/PUSHPAK-JAISWAL/gateda/contents');
          if (daRes.ok) {
            const daJson = await daRes.json();
            if (Array.isArray(daJson)) daItems = daJson;
          }
        } catch (err) {
          console.warn('GATE DA dynamic fetch skipped:', err);
        }

        if (isMounted) {
          // If we got items from GitHub, parse them into dynamic papers
          const dynamicPapers = [];

          // Parse CS
          csItems.filter(item => item.type === 'file' && item.name.toLowerCase().endsWith('.pdf')).forEach(item => {
            const yearMatch = item.name.match(/(19\d{2}|20\d{2})/);
            const year = yearMatch ? parseInt(yearMatch[1], 10) : 2026;
            const isShift1 = /CS1|shift.*1|set.*1/i.test(item.name);
            const isShift2 = /CS2|shift.*2|set.*2/i.test(item.name);
            const session = isShift2 ? 'Shift 2' : (isShift1 ? 'Shift 1' : 'Official Paper');
            const sizeStr = item.size ? `${(item.size / (1024 * 1024)).toFixed(1)} MB` : '2.5 MB';

            dynamicPapers.push({
              id: `gatecs_${item.name}`,
              code: 'CS',
              section: 'GATE CS',
              year,
              session,
              fileName: item.name,
              title: item.name,
              subtitle: `${sizeStr} • PUSHPAK-JAISWAL/gatecs`,
              fileSize: sizeStr,
              fileSizeBytes: item.size || 2500000,
              setNumber: isShift2 ? 2 : 1,
              githubRepo: 'PUSHPAK-JAISWAL/gatecs',
              rawUrl: item.download_url || `https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gatecs/main/${item.name}`,
              repoUrl: item.html_url || `https://github.com/PUSHPAK-JAISWAL/gatecs/blob/main/${item.name}`,
              questions: 65,
              maxMarks: 100,
              topics: ['Computer Science & IT', 'Algorithms', 'DBMS', 'Operating Systems'],
              status: 'unattempted'
            });
          });

          // Parse DA
          daItems.filter(item => item.type === 'file' && item.name.toLowerCase().endsWith('.pdf')).forEach(item => {
            const yearMatch = item.name.match(/(19\d{2}|20\d{2})/);
            const year = yearMatch ? parseInt(yearMatch[1], 10) : 2026;
            const sizeStr = item.size ? `${(item.size / (1024 * 1024)).toFixed(1)} MB` : '2.0 MB';

            dynamicPapers.push({
              id: `gateda_${item.name}`,
              code: 'DA',
              section: 'GATE DA',
              year,
              session: 'Official Paper',
              fileName: item.name,
              title: item.name,
              subtitle: `${sizeStr} • PUSHPAK-JAISWAL/gateda`,
              fileSize: sizeStr,
              fileSizeBytes: item.size || 2000000,
              setNumber: 1,
              githubRepo: 'PUSHPAK-JAISWAL/gateda',
              rawUrl: item.download_url || `https://raw.githubusercontent.com/PUSHPAK-JAISWAL/gateda/main/${item.name}`,
              repoUrl: item.html_url || `https://github.com/PUSHPAK-JAISWAL/gateda/blob/main/${item.name}`,
              questions: 65,
              maxMarks: 100,
              topics: ['Probability & Statistics', 'Machine Learning', 'AI', 'Python & DBMS'],
              status: 'unattempted'
            });
          });

          // Only replace if GitHub returned papers; otherwise retain default verified list
          if (dynamicPapers.length > 0) {
            setPapers(dynamicPapers);
            setIsLiveSynced(true);
          }

          setLiveConfig(prev => ({
            ...prev,
            version: fetchedVersion,
            versionRaw: fetchedVersion.replace(/^v/i, ''),
            apkSize: '30 MB', // strictly adhering to 30 MB requirement
            downloadApkUrl: fetchedDownloadUrl,
            apkDirectUrl: fetchedDownloadUrl
          }));
        }
      } catch (e) {
        console.error('Dynamic fetch encountered error', e);
      } finally {
        if (isMounted) setIsLiveSyncing(false);
      }
    }

    fetchDynamicData();

    return () => {
      isMounted = false;
    };
  }, []);

  const stats = computeStats(papers, liveConfig.version, liveConfig.apkSize);

  return (
    <DynamicPapersContext.Provider
      value={{
        papers,
        setPapers,
        config: liveConfig,
        stats,
        isLiveSyncing,
        isLiveSynced
      }}
    >
      {children}
    </DynamicPapersContext.Provider>
  );
}

export function useDynamicPapers() {
  const context = useContext(DynamicPapersContext);
  if (!context) {
    // Fallback if accessed outside provider
    const fallbackStats = computeStats(PAPERS_DATA, APP_CONFIG.version, APP_CONFIG.apkSize);
    return {
      papers: PAPERS_DATA,
      config: APP_CONFIG,
      stats: fallbackStats,
      isLiveSyncing: false,
      isLiveSynced: false
    };
  }
  return context;
}
