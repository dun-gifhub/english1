import React, { useState, useEffect, useRef, useMemo, useCallback } from 'react';
import { UnitTopic, WordItem, GameDifficulty } from '../types';
import { audioService } from '../services/audioService';
import { removeDiacritics } from '../services/dictionaryDatabase';
import confetti from 'canvas-confetti';
import {
  Heart,
  Volume2,
  Flame,
  ArrowLeft,
  Sparkles,
  Zap,
  RotateCcw,
  Trophy,
  CheckCircle,
  XCircle,
  AlertTriangle,
  Send,
  Timer
} from 'lucide-react';

interface TapGameScreenProps {
  unit: UnitTopic;
  onGameOver: (score: number, xpEarned: number) => void;
  onBack: () => void;
}

export const TapGameScreen: React.FC<TapGameScreenProps> = ({
  unit,
  onGameOver,
  onBack
}) => {
  // Randomize words for this round
  const words = useMemo(() => {
    return [...unit.words].sort(() => Math.random() - 0.5);
  }, [unit.words]);

  const [isEnglishFalling, setIsEnglishFalling] = useState<boolean>(true); // EN falling -> choose VI, or VI falling -> choose EN
  const [currentWordIndex, setCurrentWordIndex] = useState<number>(0);
  const [score, setScore] = useState<number>(0);
  const [combo, setCombo] = useState<number>(1);
  const [maxCombo, setMaxCombo] = useState<number>(1);

  // EXACTLY 3 LIVES (3 MẠNG)
  const [lives, setLives] = useState<number>(3);
  const [destroyedCount, setDestroyedCount] = useState<number>(0);
  const [missedCount, setMissedCount] = useState<number>(0);

  // Falling animation progress 0.0 (top) -> 1.0 (bottom danger line)
  const [fallingProgress, setFallingProgress] = useState<number>(0);
  const [isWordDestroyed, setIsWordDestroyed] = useState<boolean>(false);
  const [isWrongAnswerActive, setIsWrongAnswerActive] = useState<boolean>(false);
  const [clickedWrongOption, setClickedWrongOption] = useState<string | null>(null);
  const [feedbackMessage, setFeedbackMessage] = useState<string | null>(null);
  const [isGameOver, setIsGameOver] = useState<boolean>(false);
  const [typedInput, setTypedInput] = useState<string>('');
  const [missedWordsList, setMissedWordsList] = useState<WordItem[]>([]);

  const currentWord: WordItem | undefined = words[currentWordIndex];
  const progressIntervalRef = useRef<number | null>(null);

  // DYNAMIC DIFFICULTY: Ban đầu chậm, càng về sau càng rơi nhanh dần
  // Base duration starts at 11,500ms (11.5s). With each word and combo, speedMultiplier scales up!
  const baseDurationMs = 11500;
  const speedMultiplier = useMemo(() => {
    // Starts at 1.0x, increases by +0.12 per question index, +0.08 per successful hit
    const calculated = 1.0 + currentWordIndex * 0.12 + destroyedCount * 0.08;
    return Math.min(3.2, Math.max(1.0, calculated));
  }, [currentWordIndex, destroyedCount]);

  const currentFallDuration = baseDurationMs / speedMultiplier;

  // Speak word when it appears
  useEffect(() => {
    if (currentWord && !isGameOver && !isWordDestroyed) {
      if (isEnglishFalling) {
        audioService.speakEnglish(currentWord.word);
      }
    }
  }, [currentWordIndex, isEnglishFalling, isGameOver]);

  // Generate 4 multiple-choice suggestions
  const currentSuggestions = useMemo(() => {
    if (!currentWord) return [];
    const targetCorrect = isEnglishFalling ? currentWord.meaningVi : currentWord.word;
    const pool = words
      .filter((w) => w.id !== currentWord.id)
      .map((w) => (isEnglishFalling ? w.meaningVi : w.word));
    const distractors = currentWord.distractorsVi.filter((d) => d && d !== targetCorrect);

    const candidates = [targetCorrect, ...distractors, ...pool];
    const unique = Array.from(new Set(candidates));
    return unique.slice(0, 4).sort(() => Math.random() - 0.5);
  }, [currentWord, words, isEnglishFalling]);

  // Execute Destroy / Correct Answer
  const executeDestroyWord = useCallback((bonusFactor: number = 1.0) => {
    if (isWordDestroyed || isGameOver || !currentWord) return;

    setIsWordDestroyed(true);
    audioService.playHitSound(combo);

    const basePoints = 100;
    const comboBonus = combo * 30;
    const speedBonus = Math.floor((1 - fallingProgress) * 60 * speedMultiplier);
    const earned = Math.floor((basePoints + comboBonus + speedBonus) * bonusFactor);

    setScore((prev) => prev + earned);
    setDestroyedCount((prev) => prev + 1);
    setCombo((prev) => {
      const nextCombo = prev + 1;
      if (nextCombo > maxCombo) setMaxCombo(nextCombo);
      return nextCombo;
    });

    setFeedbackMessage(`💥 CHÍNH XÁC! +${earned} PTS (Combo x${combo + 1})`);
    setTypedInput('');
    setClickedWrongOption(null);

    // Advance to next word
    setTimeout(() => {
      if (currentWordIndex + 1 < words.length) {
        setCurrentWordIndex((prev) => prev + 1);
        setFallingProgress(0);
        setIsWordDestroyed(false);
        setFeedbackMessage(null);
      } else {
        // Victory! Completed all words in this unit
        setIsGameOver(true);
        audioService.playVictoryFanfare();
        confetti({ particleCount: 130, spread: 85, origin: { y: 0.6 } });
        onGameOver(score + earned, score + earned);
      }
    }, 450);
  }, [isWordDestroyed, isGameOver, currentWord, combo, fallingProgress, speedMultiplier, maxCombo, currentWordIndex, words.length, score, onGameOver]);

  // Handle Miss / Out of Time (Không trả lời được, chạm vạch nguy hiểm) -> MẤT 1 MẠNG
  const handleMissWord = useCallback(() => {
    if (isWordDestroyed || isGameOver || !currentWord) return;

    audioService.playMissSound();
    setMissedCount((prev) => prev + 1);
    setCombo(1);
    setTypedInput('');
    setClickedWrongOption(null);
    setMissedWordsList((prev) => [...prev, currentWord]);

    const newLives = lives - 1;
    setLives(newLives);

    if (newLives <= 0) {
      setIsGameOver(true);
      setFeedbackMessage('☠️ HẾT GIỜ & HẾT MẠNG! BẠN ĐÃ THUA CUỘC!');
      onGameOver(score, score);
    } else {
      setFeedbackMessage(`⏱️ Hết giờ! Rơi mất từ (-1 ❤️, còn ${newLives}/3 mạng)!`);

      setTimeout(() => {
        if (currentWordIndex + 1 < words.length) {
          setCurrentWordIndex((prev) => prev + 1);
          setFallingProgress(0);
          setFeedbackMessage(null);
        } else {
          setIsGameOver(true);
          onGameOver(score, score);
        }
      }, 650);
    }
  }, [isWordDestroyed, isGameOver, currentWord, lives, onGameOver, score, currentWordIndex, words.length]);

  // Handle Wrong Answer (Trả lời sai) -> MẤT 1 MẠNG, SAI 3 LẦN THUA
  const handleWrongAnswer = useCallback((wrongValue: string) => {
    if (isWordDestroyed || isGameOver || !currentWord) return;

    audioService.playMissSound();
    setCombo(1);
    setClickedWrongOption(wrongValue);
    setIsWrongAnswerActive(true);
    setMissedWordsList((prev) => [...prev, currentWord]);

    const newLives = lives - 1;
    setLives(newLives);

    if (newLives <= 0) {
      setIsGameOver(true);
      setFeedbackMessage('☠️ SAI 3 LẦN! BẠN ĐÃ THUA CUỘC!');
      onGameOver(score, score);
    } else {
      setFeedbackMessage(`❌ Sai đáp án! Mất 1 ❤️ (Còn ${newLives}/3 mạng)!`);

      // After revealing mistake briefly, advance to next word
      setTimeout(() => {
        setIsWrongAnswerActive(false);
        setClickedWrongOption(null);
        if (currentWordIndex + 1 < words.length) {
          setCurrentWordIndex((prev) => prev + 1);
          setFallingProgress(0);
          setFeedbackMessage(null);
        } else {
          setIsGameOver(true);
          onGameOver(score, score);
        }
      }, 700);
    }
  }, [isWordDestroyed, isGameOver, currentWord, lives, onGameOver, score, currentWordIndex, words.length]);

  // Falling animation loop: Smooth 40ms updates
  useEffect(() => {
    if (isGameOver || isWordDestroyed || isWrongAnswerActive || !currentWord) return;

    const intervalTime = 40;
    const step = intervalTime / currentFallDuration;

    progressIntervalRef.current = window.setInterval(() => {
      setFallingProgress((prev) => {
        const next = prev + step;
        if (next >= 1.0) {
          clearInterval(progressIntervalRef.current!);
          handleMissWord();
          return 1.0;
        }
        return next;
      });
    }, intervalTime);

    return () => {
      if (progressIntervalRef.current) clearInterval(progressIntervalRef.current);
    };
  }, [currentFallDuration, isGameOver, isWordDestroyed, isWrongAnswerActive, currentWord, handleMissWord]);

  // Verify answer clicked or typed
  const checkAnswer = (input: string, isFromCardClick: boolean = false) => {
    if (!currentWord || isWordDestroyed || isGameOver || isWrongAnswerActive) return;
    const cleanIn = removeDiacritics(input).trim();
    const target = isEnglishFalling ? currentWord.meaningVi : currentWord.word;
    const cleanTarget = removeDiacritics(target).trim();
    const cleanEn = removeDiacritics(currentWord.word).trim();
    const cleanVi = removeDiacritics(currentWord.meaningVi).trim();

    const isMatch =
      cleanIn === cleanTarget ||
      cleanIn === cleanEn ||
      cleanIn === cleanVi ||
      (!isFromCardClick && cleanIn.length >= 3 && cleanTarget.includes(cleanIn));

    if (isMatch) {
      executeDestroyWord(1.2);
    } else {
      // If clicked from a card OR explicitly submitted an incorrect answer -> LOSE 1 LIFE!
      handleWrongAnswer(input);
    }
  };

  const handleRestart = () => {
    setCurrentWordIndex(0);
    setScore(0);
    setCombo(1);
    setMaxCombo(1);
    setLives(3);
    setDestroyedCount(0);
    setMissedCount(0);
    setFallingProgress(0);
    setIsWordDestroyed(false);
    setIsWrongAnswerActive(false);
    setClickedWrongOption(null);
    setIsGameOver(false);
    setFeedbackMessage(null);
    setTypedInput('');
    setMissedWordsList([]);
  };

  return (
    <div className="relative min-h-[85vh] flex flex-col justify-between max-w-4xl mx-auto p-4 select-none">
      {/* Top HUD: Title, 3 Lives (❤️❤️❤️), Combo, Speed Multiplier, Score */}
      <div className="rounded-3xl bg-[#1B263B] border border-[#27384E] p-4 sm:p-5 shadow-xl">
        <div className="flex items-center justify-between gap-3 flex-wrap">
          <div className="flex items-center gap-3">
            <button
              onClick={onBack}
              className="p-2.5 rounded-xl bg-[#131F2E] hover:bg-[#27384E] text-[#778DA9] hover:text-white transition-colors cursor-pointer"
              title="Quay lại danh sách"
            >
              <ArrowLeft size={18} />
            </button>
            <div>
              <div className="flex items-center gap-2">
                <span className="text-[10px] font-extrabold uppercase px-2 py-0.5 rounded-md bg-[#00E5FF]/20 text-[#00E5FF]">
                  Săn Từ Phản Xạ
                </span>
                <span className="text-[11px] text-[#778DA9]">
                  Lớp {unit.grade} • Từ {currentWordIndex + 1}/{words.length}
                </span>
              </div>
              <h3 className="text-sm sm:text-base font-black text-white truncate max-w-[200px] sm:max-w-xs mt-0.5">
                {unit.title}
              </h3>
            </div>
          </div>

          {/* 3 LIVES (MẠNG) DISPLAY */}
          <div className="flex items-center gap-4">
            <div className="flex flex-col items-center">
              <span className="text-[10px] font-bold text-[#778DA9] uppercase mb-0.5">Mạng Sống</span>
              <div className="flex items-center gap-1.5 p-1 px-2.5 rounded-xl bg-[#131F2E] border border-[#EF476F]/30">
                {[1, 2, 3].map((heartIndex) => (
                  <Heart
                    key={heartIndex}
                    size={22}
                    className={`transition-all duration-300 ${
                      heartIndex <= lives
                        ? 'text-[#EF476F] fill-[#EF476F] scale-110 drop-shadow-[0_0_8px_rgba(239,71,111,0.6)] animate-pulse'
                        : 'text-[#2D3F56] fill-none scale-90 opacity-40'
                    }`}
                  />
                ))}
              </div>
            </div>

            {/* Combo Badge */}
            <div className="flex flex-col items-center">
              <span className="text-[10px] font-bold text-[#778DA9] uppercase mb-0.5">Combo</span>
              <div className="flex items-center gap-1 px-2.5 py-1.5 rounded-xl bg-[#27384E] text-[#FFD166] text-xs font-black border border-[#FFD166]/30">
                <Flame size={14} className="fill-[#FFD166]" />
                <span>x{combo}</span>
              </div>
            </div>

            {/* Score */}
            <div className="text-right pl-2 border-l border-[#27384E]">
              <span className="text-[10px] text-[#778DA9] block font-bold">ĐIỂM SỐ</span>
              <span className="text-lg sm:text-xl font-black text-[#00E5FF] tracking-tight">
                {score.toLocaleString()}
              </span>
            </div>
          </div>
        </div>

        {/* Dynamic Difficulty Gauge: Ban đầu chậm, sau nhanh dần */}
        <div className="mt-3.5 pt-3 border-t border-[#27384E]/80 flex items-center justify-between gap-2 flex-wrap text-xs">
          <div className="flex items-center gap-2">
            <span className="text-[#778DA9] font-bold flex items-center gap-1">
              <Timer size={14} className="text-[#FFD166]" />
              <span>Độ Khó Tăng Dần:</span>
            </span>
            <div className="flex items-center gap-1.5 px-2.5 py-1 rounded-xl bg-[#131F2E] border border-[#27384E]">
              <span className="font-mono font-black text-[#FFD166]">
                {speedMultiplier.toFixed(1)}x
              </span>
              <span className="text-[11px] text-[#778DA9]">
                {speedMultiplier < 1.3
                  ? '🐢 Tốc độ ban đầu (Chậm)'
                  : speedMultiplier < 1.9
                  ? '⚡ Đang tăng tốc (Vừa)'
                  : speedMultiplier < 2.5
                  ? '🔥 Phản xạ siêu tốc (Nhanh)'
                  : '🚀 Thần tốc cực hạn!'}
              </span>
            </div>
          </div>

          <button
            onClick={() => setIsEnglishFalling(!isEnglishFalling)}
            className="px-3 py-1 rounded-xl text-[11px] font-bold bg-[#131F2E] text-[#9D4EDD] border border-[#9D4EDD]/40 hover:bg-[#9D4EDD]/15 cursor-pointer transition-all"
          >
            Chế độ: {isEnglishFalling ? '🇬🇧 Tiếng Anh rơi' : '🇻🇳 Tiếng Việt rơi'}
          </button>
        </div>
      </div>

      {/* Main Falling Battlefield Canvas */}
      <div className="relative flex-1 my-4 min-h-[320px] sm:min-h-[380px] rounded-3xl bg-linear-to-b from-[#0D1B2A] via-[#131F2E] to-[#0D1B2A] border border-[#27384E] overflow-hidden flex flex-col justify-between p-4 shadow-inner">
        {/* Falling Target Word Card */}
        {currentWord && !isGameOver && (
          <div
            className="absolute left-1/2 -translate-x-1/2 w-11/12 max-w-md transition-all duration-75"
            style={{
              top: `${Math.min(78, fallingProgress * 80)}%`
            }}
          >
            <div
              className={`p-4 sm:p-5 rounded-2xl border text-center shadow-2xl transition-all duration-150 ${
                isWordDestroyed
                  ? 'scale-90 opacity-0 bg-[#06D6A0]/20 border-[#06D6A0]'
                  : isWrongAnswerActive
                  ? 'scale-105 bg-[#EF476F]/25 border-[#EF476F] shadow-[#EF476F]/30 animate-shake'
                  : 'bg-[#1E2D40] border-[#00E5FF]/60 shadow-[#00E5FF]/10'
              }`}
            >
              <div className="flex items-center justify-between mb-1.5">
                <span className="text-[10px] font-extrabold uppercase px-2 py-0.5 rounded-md bg-[#00E5FF]/20 text-[#00E5FF]">
                  {isEnglishFalling ? 'Từ Mục Tiêu (English)' : 'Nghĩa Mục Tiêu (Tiếng Việt)'}
                </span>

                <button
                  type="button"
                  onClick={() => audioService.speakEnglish(currentWord.word)}
                  className="p-1 rounded-md text-[#778DA9] hover:text-[#00E5FF] transition-colors"
                  title="Nghe phát âm chuẩn"
                >
                  <Volume2 size={16} />
                </button>
              </div>

              {/* Word Display */}
              <h2 className="text-2xl sm:text-4xl font-black text-white tracking-wide">
                {isEnglishFalling ? currentWord.word : currentWord.meaningVi}
              </h2>

              {isEnglishFalling && currentWord.phonetic && (
                <p className="text-xs text-[#FFD166] font-mono mt-1">{currentWord.phonetic}</p>
              )}

              {/* Falling Progress / Time Remaining Bar */}
              <div className="mt-3.5 w-full h-2 rounded-full bg-[#131F2E] overflow-hidden p-0.5 border border-[#27384E]">
                <div
                  className={`h-full rounded-full transition-all duration-75 ${
                    fallingProgress > 0.7
                      ? 'bg-[#EF476F] animate-pulse'
                      : fallingProgress > 0.4
                      ? 'bg-[#FFD166]'
                      : 'bg-[#00E5FF]'
                  }`}
                  style={{ width: `${(1 - fallingProgress) * 100}%` }}
                />
              </div>
            </div>
          </div>
        )}

        {/* Feedback / Warning Popup Banner */}
        {feedbackMessage && (
          <div className="absolute top-4 left-1/2 -translate-x-1/2 z-20 px-4 py-2 rounded-xl bg-[#1E2D40]/95 border border-[#00E5FF] text-white text-xs sm:text-sm font-extrabold shadow-2xl animate-bounce">
            {feedbackMessage}
          </div>
        )}

        {/* Danger baseline at bottom */}
        <div className="mt-auto pt-2 border-t-2 border-dashed border-[#EF476F]/50 flex items-center justify-between text-[11px] font-bold text-[#EF476F]">
          <span className="flex items-center gap-1">
            <AlertTriangle size={13} />
            <span>Vạch Nguy Hiểm</span>
          </span>
          <span>Không trả lời kịp = Mất 1 ❤️ (Sai 3 mạng = THUA)</span>
        </div>
      </div>

      {/* Input Options Bottom Deck: 4 Multiple-choice Cards OR Text Input */}
      <div className="space-y-3">
        {/* 4 Fast-Tap Suggestion Cards (Wrong click deducts life!) */}
        <div className="grid grid-cols-2 gap-2.5">
          {currentSuggestions.map((opt, i) => {
            const isTarget = currentWord && (isEnglishFalling ? currentWord.meaningVi : currentWord.word) === opt;
            const isThisClickedWrong = clickedWrongOption === opt;

            return (
              <button
                key={i}
                onClick={() => checkAnswer(opt, true)}
                disabled={isGameOver || isWordDestroyed || isWrongAnswerActive}
                className={`py-3.5 px-4 rounded-2xl border text-white text-xs sm:text-sm font-bold text-left transition-all flex items-center justify-between gap-2 shadow-md cursor-pointer disabled:opacity-60 ${
                  isThisClickedWrong
                    ? 'bg-[#EF476F] border-[#EF476F] scale-95 shadow-[#EF476F]/40'
                    : isWrongAnswerActive && isTarget
                    ? 'bg-[#06D6A0] text-[#0D1B2A] border-[#06D6A0] font-black'
                    : 'bg-[#1E2D40] hover:bg-[#27384E] active:scale-98 border-[#2D3F56] hover:border-[#00E5FF]'
                }`}
              >
                <span className="truncate">{opt}</span>
                <span className="w-5 h-5 rounded-full bg-[#131F2E] text-[10px] font-mono text-[#778DA9] flex items-center justify-center shrink-0">
                  {i + 1}
                </span>
              </button>
            );
          })}
        </div>

        {/* Quick Type Input Bar */}
        <form
          onSubmit={(e) => {
            e.preventDefault();
            if (typedInput.trim()) {
              checkAnswer(typedInput.trim(), false);
            }
          }}
          className="flex gap-2"
        >
          <input
            type="text"
            value={typedInput}
            onChange={(e) => setTypedInput(e.target.value)}
            placeholder="Hoặc gõ từ trực tiếp rồi nhấn Enter / Phá Hủy..."
            disabled={isGameOver || isWordDestroyed || isWrongAnswerActive}
            className="flex-1 px-4 py-2.5 rounded-xl bg-[#131F2E] border border-[#27384E] focus:outline-hidden focus:border-[#00E5FF] text-white text-xs sm:text-sm placeholder-[#778DA9]"
          />
          <button
            type="submit"
            disabled={isGameOver || !typedInput.trim() || isWrongAnswerActive}
            className="px-4 py-2.5 rounded-xl bg-[#00E5FF] text-[#0D1B2A] font-bold text-xs sm:text-sm hover:bg-[#38bdf8] transition-all disabled:opacity-50 cursor-pointer flex items-center gap-1"
          >
            <Send size={15} />
            <span className="hidden sm:inline">Phá Hủy</span>
          </button>
        </form>
      </div>

      {/* Game Over / Victory Modal */}
      {isGameOver && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/85 backdrop-blur-md">
          <div className="relative w-full max-w-md p-6 rounded-3xl bg-[#1E2D40] border border-[#00E5FF]/40 shadow-2xl text-center space-y-4">
            <div className="w-16 h-16 mx-auto rounded-2xl bg-[#00E5FF]/20 flex items-center justify-center text-[#00E5FF]">
              {lives > 0 ? (
                <Trophy size={36} className="text-[#FFD166]" />
              ) : (
                <Flame size={36} className="text-[#EF476F]" />
              )}
            </div>

            <div>
              <h3 className="text-xl font-black text-white">
                {lives > 0 ? '🎉 CHIẾN THẮNG BÀI ĐẤU TỪ!' : '☠️ BẠN ĐÃ THUA CUỘC!'}
              </h3>
              <p className="text-xs text-[#778DA9] mt-1">
                {lives <= 0
                  ? 'Đã hết cả 3 mạng (do trả lời sai hoặc không kịp phản xạ).'
                  : `Xuất sắc sống sót và hoàn thành toàn bộ từ vựng Lớp ${unit.grade}!`}
              </p>
            </div>

            {/* Score Grid */}
            <div className="grid grid-cols-2 gap-2.5 p-3 rounded-2xl bg-[#131F2E] border border-[#27384E] text-xs">
              <div className="p-2.5 rounded-xl bg-[#1B263B]">
                <span className="text-[#778DA9] block">Tổng Điểm</span>
                <span className="text-lg font-black text-[#00E5FF]">{score.toLocaleString()}</span>
              </div>
              <div className="p-2.5 rounded-xl bg-[#1B263B]">
                <span className="text-[#778DA9] block">Kỷ Lục Combo</span>
                <span className="text-lg font-black text-[#FFD166]">x{maxCombo}</span>
              </div>
              <div className="p-2.5 rounded-xl bg-[#1B263B]">
                <span className="text-[#778DA9] block">Từ Bắn Trúng</span>
                <span className="text-base font-bold text-[#06D6A0]">
                  {destroyedCount} / {words.length}
                </span>
              </div>
              <div className="p-2.5 rounded-xl bg-[#1B263B]">
                <span className="text-[#778DA9] block">Mạng Còn Lại</span>
                <span className="text-base font-bold text-[#EF476F]">
                  {lives} / 3 ❤️
                </span>
              </div>
            </div>

            {/* Missed words review list if any */}
            {missedWordsList.length > 0 && (
              <div className="text-left p-3 rounded-2xl bg-[#131F2E] border border-[#EF476F]/30 max-h-32 overflow-y-auto">
                <span className="text-[10px] font-bold text-[#EF476F] uppercase block mb-1">
                  Từ vựng cần ôn lại ({missedWordsList.length}):
                </span>
                <div className="space-y-1 text-xs">
                  {missedWordsList.slice(0, 5).map((mw, i) => (
                    <div key={i} className="flex justify-between text-[#ADB5BD]">
                      <span className="font-bold text-white">{mw.word}</span>
                      <span>{mw.meaningVi}</span>
                    </div>
                  ))}
                </div>
              </div>
            )}

            <div className="flex gap-3 pt-2">
              <button
                onClick={onBack}
                className="flex-1 py-3 px-4 rounded-xl text-xs font-bold text-[#778DA9] bg-[#131F2E] hover:text-white hover:bg-[#27384E] transition-all cursor-pointer"
              >
                Về Trang Chủ
              </button>
              <button
                onClick={handleRestart}
                className="flex-1 py-3 px-4 rounded-xl text-xs font-extrabold text-[#0D1B2A] bg-[#00E5FF] hover:bg-[#38bdf8] transition-all flex items-center justify-center gap-1.5 shadow-lg shadow-[#00E5FF]/20 cursor-pointer"
              >
                <RotateCcw size={15} />
                <span>Chơi Lại (3 Mạng)</span>
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
