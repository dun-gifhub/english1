import React, { useState, useMemo, useEffect } from 'react';
import { lookupDictionary, lookupUniversalOnline } from '../services/dictionaryDatabase';
import { audioService } from '../services/audioService';
import { DictionaryEntry } from '../types';
import {
  Search,
  Volume2,
  ArrowRightLeft,
  Sparkles,
  X,
  Globe2,
  Loader2,
  BookOpen,
  Check,
  TrendingUp
} from 'lucide-react';

export const DictionaryScreen: React.FC = () => {
  const [query, setQuery] = useState('');
  const [isEnToVi, setIsEnToVi] = useState(true);
  const [onlineResult, setOnlineResult] = useState<DictionaryEntry | null>(null);
  const [isLoadingOnline, setIsLoadingOnline] = useState(false);

  // Popular sample words for quick test & learning
  const POPULAR_TAGS = [
    { label: 'perseverance', isEn: true },
    { label: 'kiên trì', isEn: false },
    { label: 'biodiversity', isEn: true },
    { label: 'môi trường', isEn: false },
    { label: 'serendipity', isEn: true },
    { label: 'thành công', isEn: false },
    { label: 'infrastructure', isEn: true },
    { label: 'bền vững', isEn: false }
  ];

  // Auto-detect language if Vietnamese characters are detected
  useEffect(() => {
    const hasViAccents = /[àáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ]/i.test(query);
    if (hasViAccents && isEnToVi) {
      setIsEnToVi(false);
    }
  }, [query, isEnToVi]);

  // Synchronous immediate results from extensive offline database
  const localResults = useMemo(() => {
    return lookupDictionary(query, isEnToVi);
  }, [query, isEnToVi]);

  // Asynchronous Universal lookup for ANY word online
  useEffect(() => {
    const clean = query.trim();
    if (!clean || clean.length < 2) {
      setOnlineResult(null);
      setIsLoadingOnline(false);
      return;
    }

    let isMounted = true;
    setIsLoadingOnline(true);

    const timer = setTimeout(async () => {
      const res = await lookupUniversalOnline(clean, isEnToVi);
      if (isMounted) {
        setOnlineResult(res);
        setIsLoadingOnline(false);
      }
    }, 350);

    return () => {
      isMounted = false;
      clearTimeout(timer);
    };
  }, [query, isEnToVi]);

  // Combine results: online result (if fresh and unique) placed at top
  const displayResults = useMemo(() => {
    if (!query.trim()) return localResults;
    if (onlineResult) {
      const filtered = localResults.filter(
        (r) => r.wordEn.toLowerCase() !== onlineResult.wordEn.toLowerCase()
      );
      return [onlineResult, ...filtered];
    }
    return localResults;
  }, [localResults, onlineResult, query]);

  return (
    <div className="max-w-4xl mx-auto space-y-6 pb-20">
      {/* Header Banner */}
      <div className="p-5 sm:p-6 rounded-3xl bg-[#1B263B] border border-[#27384E] shadow-xl flex items-center justify-between gap-4 flex-wrap">
        <div>
          <span className="text-[10px] font-extrabold uppercase tracking-widest text-[#00E5FF] block">
            Tra Cứu Song Ngữ Toàn Diện (Mọi Từ Vựng Ngoài Sách)
          </span>
          <h2 className="text-xl sm:text-2xl font-black text-white mt-0.5">
            Từ Điển Thông Minh Anh ↔ Việt
          </h2>
          <p className="text-xs text-[#778DA9]">
            Tra cứu bất kỳ từ tiếng Anh hoặc tiếng Việt nào — phát âm IPA, từ loại, dịch nghĩa chính xác và câu ví dụ
          </p>
        </div>

        {/* Direction Switch Toggle */}
        <button
          onClick={() => {
            setIsEnToVi(!isEnToVi);
            setOnlineResult(null);
          }}
          className="px-4 py-2.5 rounded-2xl bg-[#131F2E] hover:bg-[#27384E] border border-[#00E5FF]/40 text-white text-xs font-bold transition-all flex items-center gap-2 cursor-pointer shadow-md"
        >
          <ArrowRightLeft size={16} className="text-[#00E5FF]" />
          <span>{isEnToVi ? 'Anh ➡️ Việt (EN - VI)' : 'Việt ➡️ Anh (VI - EN)'}</span>
        </button>
      </div>

      {/* Search Input Bar */}
      <div className="space-y-3">
        <div className="relative">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-[#778DA9]" size={20} />
          <input
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder={
              isEnToVi
                ? 'Nhập bất kỳ từ tiếng Anh nào (vd: apple, magnificent, serendipity, infrastructure...)'
                : 'Nhập bất kỳ từ tiếng Việt nào (vd: kiên trì, tương lai, trường học, máy bay...)'
            }
            className="w-full pl-12 pr-12 py-3.5 rounded-2xl bg-[#131F2E] border border-[#27384E] focus:outline-hidden focus:border-[#00E5FF] focus:ring-1 focus:ring-[#00E5FF] text-white text-sm placeholder-[#778DA9] transition-all shadow-inner"
          />
          {isLoadingOnline ? (
            <Loader2 className="absolute right-4 top-1/2 -translate-y-1/2 text-[#00E5FF] animate-spin" size={18} />
          ) : query ? (
            <button
              onClick={() => {
                setQuery('');
                setOnlineResult(null);
              }}
              className="absolute right-4 top-1/2 -translate-y-1/2 text-[#778DA9] hover:text-white"
            >
              <X size={18} />
            </button>
          ) : null}
        </div>

        {/* Popular / Sample keywords for quick tap */}
        <div className="flex items-center gap-1.5 flex-wrap text-xs text-[#778DA9]">
          <span className="flex items-center gap-1 text-[11px] font-semibold">
            <TrendingUp size={12} className="text-[#FFD166]" />
            <span>Từ phổ biến:</span>
          </span>
          {POPULAR_TAGS.map((tag, idx) => (
            <button
              key={idx}
              onClick={() => {
                setQuery(tag.label);
                setIsEnToVi(tag.isEn);
              }}
              className="px-2.5 py-1 rounded-lg bg-[#1B263B] hover:bg-[#27384E] text-[#ADB5BD] hover:text-[#00E5FF] border border-[#27384E] transition-all cursor-pointer text-[11px] font-medium"
            >
              {tag.label}
            </button>
          ))}
        </div>
      </div>

      {/* Results List */}
      <div className="space-y-3.5">
        <div className="flex items-center justify-between text-xs text-[#778DA9]">
          <span className="flex items-center gap-1.5">
            <Globe2 size={14} className="text-[#00E5FF]" />
            <span>Kết quả tra cứu ({displayResults.length} mục)</span>
          </span>
          {query.trim().length > 0 && (
            <span className="flex items-center gap-1 text-[#00E5FF]">
              <Sparkles size={13} />
              <span>Dịch thuật & Phân tích ngữ nghĩa tự động</span>
            </span>
          )}
        </div>

        {displayResults.map((entry, index) => (
          <div
            key={`${entry.wordEn}_${index}`}
            className="p-5 sm:p-6 rounded-3xl bg-[#1B263B] border border-[#27384E] hover:border-[#00E5FF]/50 transition-all shadow-md space-y-3.5"
          >
            <div className="flex items-start justify-between gap-3">
              <div>
                <div className="flex items-center gap-2 flex-wrap">
                  <h3 className="text-xl sm:text-2xl font-black text-white tracking-wide">
                    {entry.wordEn}
                  </h3>
                  <span className="px-2 py-0.5 rounded-md text-[10px] font-bold bg-[#9D4EDD]/20 text-[#9D4EDD] uppercase">
                    {entry.partOfSpeech}
                  </span>
                  {entry.phonetic && (
                    <span className="text-xs font-mono text-[#FFD166]">{entry.phonetic}</span>
                  )}
                </div>

                <p className="text-sm sm:text-base font-bold text-[#00E5FF] mt-1 capitalize">
                  {entry.meaningVi}
                </p>

                {entry.definitionEn && (
                  <p className="text-xs text-[#ADB5BD] mt-1 italic">
                    EN: {entry.definitionEn}
                  </p>
                )}
              </div>

              {/* Pronunciation Audio Button */}
              <button
                onClick={() => audioService.speakEnglish(entry.wordEn)}
                className="p-2.5 rounded-xl bg-[#131F2E] hover:bg-[#00E5FF] hover:text-[#0D1B2A] text-[#00E5FF] border border-[#00E5FF]/30 transition-all cursor-pointer shrink-0"
                title="Nghe phát âm chuẩn tiếng Anh"
              >
                <Volume2 size={18} />
              </button>
            </div>

            {/* Bilingual Example Box */}
            {(entry.exampleEn || entry.exampleVi) && (
              <div className="p-3.5 rounded-2xl bg-[#131F2E] border border-[#27384E]/80 space-y-1 text-xs">
                {entry.exampleEn && (
                  <p className="text-white font-medium">
                    🇬🇧 {entry.exampleEn}
                  </p>
                )}
                {entry.exampleVi && (
                  <p className="text-[#ADB5BD]">
                    🇻🇳 {entry.exampleVi}
                  </p>
                )}
              </div>
            )}

            {/* Synonyms */}
            {entry.synonyms && entry.synonyms.length > 0 && (
              <div className="flex items-center gap-1.5 flex-wrap pt-1 text-xs">
                <span className="text-[#778DA9] font-medium">Từ liên quan / đồng nghĩa:</span>
                {entry.synonyms.map((s, si) => (
                  <button
                    key={si}
                    onClick={() => {
                      setQuery(s);
                      setIsEnToVi(true);
                    }}
                    className="px-2 py-0.5 rounded-lg bg-[#27384E] text-[#ADB5BD] hover:text-white hover:bg-[#344966] transition-colors cursor-pointer text-[11px]"
                  >
                    {s}
                  </button>
                ))}
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};
