import React, { useState } from 'react';
import { TeacherAssignment } from '../types';
import { audioService } from '../services/audioService';
import confetti from 'canvas-confetti';
import {
  ArrowLeft,
  CheckCircle,
  XCircle,
  HelpCircle,
  Trophy,
  Flame,
  Award,
  ChevronRight,
  ChevronLeft
} from 'lucide-react';

interface AssignmentQuizScreenProps {
  assignment: TeacherAssignment;
  onFinish: (xpEarned: number) => void;
  onBack: () => void;
}

export const AssignmentQuizScreen: React.FC<AssignmentQuizScreenProps> = ({
  assignment,
  onFinish,
  onBack
}) => {
  const [currentQIndex, setCurrentQIndex] = useState<number>(0);
  const [userAnswers, setUserAnswers] = useState<Record<number, number>>({});
  const [isSubmitted, setIsSubmitted] = useState<boolean>(false);

  const questions = assignment.questions;
  const currentQ = questions[currentQIndex];

  const handleSelectOption = (optIndex: number) => {
    if (isSubmitted) return;
    setUserAnswers((prev) => ({
      ...prev,
      [currentQIndex]: optIndex
    }));
  };

  const handleSubmitQuiz = () => {
    const correctCount = questions.reduce((acc, q, idx) => {
      return acc + (userAnswers[idx] === q.correctIndex ? 1 : 0);
    }, 0);

    const xpReward = correctCount * 50;
    setIsSubmitted(true);
    onFinish(xpReward);

    if (correctCount >= Math.ceil(questions.length / 2)) {
      audioService.playVictoryFanfare();
      confetti({ particleCount: 100, spread: 70, origin: { y: 0.6 } });
    }
  };

  // If results submitted
  if (isSubmitted) {
    const correctCount = questions.reduce((acc, q, idx) => {
      return acc + (userAnswers[idx] === q.correctIndex ? 1 : 0);
    }, 0);
    const scorePercent = questions.length > 0 ? Math.round((correctCount / questions.length) * 100) : 0;
    const xpReward = correctCount * 50;

    return (
      <div className="max-w-3xl mx-auto space-y-6 pb-20">
        {/* Result Card */}
        <div className="p-6 sm:p-8 rounded-3xl bg-[#1B263B] border border-[#FFD166]/40 shadow-2xl text-center space-y-4">
          <div className="w-16 h-16 mx-auto rounded-2xl bg-[#FFD166]/20 flex items-center justify-center text-[#FFD166]">
            <Trophy size={36} />
          </div>

          <div>
            <span className="text-[10px] font-bold uppercase tracking-widest text-[#FFD166] block">
              KẾT QUẢ BÀI KIỂM TRA
            </span>
            <h2 className="text-xl sm:text-2xl font-black text-white mt-1">
              {assignment.title}
            </h2>
            <p className="text-xs text-[#778DA9]">Lớp {assignment.grade} • {assignment.teacherName}</p>
          </div>

          <div className="flex items-center justify-center gap-4 py-2">
            <div className="px-5 py-3 rounded-2xl bg-[#131F2E] border border-[#27384E]">
              <span className="text-xs text-[#778DA9] block">Số Câu Đúng</span>
              <span className={`text-2xl font-black ${scorePercent >= 50 ? 'text-[#06D6A0]' : 'text-[#EF476F]'}`}>
                {correctCount} / {questions.length} ({scorePercent}%)
              </span>
            </div>

            <div className="px-5 py-3 rounded-2xl bg-[#131F2E] border border-[#27384E]">
              <span className="text-xs text-[#778DA9] block">Điểm XP Nhận Được</span>
              <span className="text-2xl font-black text-[#9D4EDD]">
                +{xpReward} XP
              </span>
            </div>
          </div>

          <button
            onClick={onBack}
            className="w-full py-3 px-6 rounded-2xl bg-[#00E5FF] text-[#0D1B2A] font-extrabold text-sm hover:bg-[#38bdf8] transition-all shadow-lg shadow-[#00E5FF]/20 cursor-pointer"
          >
            Quay Lại Trang Chủ
          </button>
        </div>

        {/* Detailed Question Review */}
        <div className="space-y-4">
          <h3 className="text-sm font-bold text-white uppercase tracking-wider">
            Chi Tiết Lời Giải Từng Câu Hỏi
          </h3>

          {questions.map((q, idx) => {
            const userPick = userAnswers[idx];
            const isCorrect = userPick === q.correctIndex;
            return (
              <div
                key={q.id || idx}
                className={`p-5 rounded-2xl border bg-[#1B263B] space-y-3 ${
                  isCorrect ? 'border-[#06D6A0]/40' : 'border-[#EF476F]/40'
                }`}
              >
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold text-[#778DA9]">Câu #{idx + 1}</span>
                  <span className={`text-xs font-bold flex items-center gap-1 ${isCorrect ? 'text-[#06D6A0]' : 'text-[#EF476F]'}`}>
                    {isCorrect ? <CheckCircle size={15} /> : <XCircle size={15} />}
                    <span>{isCorrect ? 'Chính xác' : 'Chưa đúng'}</span>
                  </span>
                </div>

                <p className="text-sm font-semibold text-white">{q.question}</p>

                {/* Options List */}
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 text-xs">
                  {q.options.map((opt, optIdx) => {
                    const isTargetCorrect = optIdx === q.correctIndex;
                    const isUserPick = optIdx === userPick;
                    let style = 'bg-[#131F2E] border-[#27384E] text-[#ADB5BD]';
                    if (isTargetCorrect) {
                      style = 'bg-[#06D6A0]/20 border-[#06D6A0] text-[#06D6A0] font-bold';
                    } else if (isUserPick) {
                      style = 'bg-[#EF476F]/20 border-[#EF476F] text-[#EF476F] font-bold';
                    }
                    return (
                      <div key={optIdx} className={`p-2.5 rounded-xl border flex items-center justify-between ${style}`}>
                        <span>{String.fromCharCode(65 + optIdx)}. {opt}</span>
                        {isTargetCorrect && <span className="text-[10px]">Đáp án đúng</span>}
                        {!isTargetCorrect && isUserPick && <span className="text-[10px]">Bạn chọn</span>}
                      </div>
                    );
                  })}
                </div>

                {q.explanation && (
                  <div className="p-3 rounded-xl bg-[#131F2E] border border-[#27384E] text-xs text-[#ADB5BD] flex items-start gap-2">
                    <HelpCircle size={16} className="text-[#00E5FF] shrink-0 mt-0.5" />
                    <span><strong className="text-[#00E5FF]">Giải thích: </strong>{q.explanation}</span>
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </div>
    );
  }

  // Quiz in Progress view
  const answeredCount = Object.keys(userAnswers).length;

  return (
    <div className="max-w-2xl mx-auto space-y-5 pb-20">
      {/* Top Bar */}
      <div className="flex items-center justify-between gap-3">
        <button
          onClick={onBack}
          className="p-2 rounded-xl bg-[#1B263B] hover:bg-[#27384E] text-[#778DA9] hover:text-white transition-colors cursor-pointer"
        >
          <ArrowLeft size={18} />
        </button>

        <div className="text-center">
          <h3 className="text-sm font-bold text-white truncate max-w-xs sm:max-w-md">
            {assignment.title}
          </h3>
          <p className="text-[11px] text-[#778DA9]">
            Câu {currentQIndex + 1} / {questions.length} • Lớp {assignment.grade}
          </p>
        </div>

        <span className="text-xs font-bold text-[#00E5FF] px-2.5 py-1 rounded-xl bg-[#1B263B] border border-[#27384E]">
          {answeredCount} / {questions.length} đã làm
        </span>
      </div>

      {/* Progress Line */}
      <div className="w-full h-1.5 rounded-full bg-[#131F2E] overflow-hidden">
        <div
          className="h-full bg-linear-to-r from-[#00E5FF] to-[#9D4EDD] transition-all"
          style={{ width: `${((currentQIndex + 1) / questions.length) * 100}%` }}
        />
      </div>

      {/* Question Card */}
      {currentQ && (
        <div className="p-6 rounded-3xl bg-[#1B263B] border border-[#27384E] shadow-xl space-y-6">
          <div className="flex items-center justify-between">
            <span className="px-2.5 py-1 rounded-lg text-xs font-bold bg-[#00E5FF]/20 text-[#00E5FF]">
              Câu hỏi #{currentQIndex + 1}
            </span>
          </div>

          <h3 className="text-base sm:text-lg font-bold text-white leading-relaxed">
            {currentQ.question}
          </h3>

          {/* 4 Choices */}
          <div className="space-y-2.5">
            {currentQ.options.map((opt, optIdx) => {
              const isSelected = userAnswers[currentQIndex] === optIdx;
              return (
                <button
                  key={optIdx}
                  onClick={() => handleSelectOption(optIdx)}
                  className={`w-full p-4 rounded-2xl border text-left text-sm font-semibold transition-all flex items-center justify-between gap-3 cursor-pointer ${
                    isSelected
                      ? 'bg-[#00E5FF]/20 border-[#00E5FF] text-white shadow-md shadow-[#00E5FF]/10'
                      : 'bg-[#131F2E] border-[#27384E] text-[#ADB5BD] hover:border-[#778DA9]'
                  }`}
                >
                  <div className="flex items-center gap-3">
                    <span
                      className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold shrink-0 ${
                        isSelected
                          ? 'bg-[#00E5FF] text-[#0D1B2A]'
                          : 'bg-[#1E2D40] text-[#778DA9]'
                      }`}
                    >
                      {String.fromCharCode(65 + optIdx)}
                    </span>
                    <span>{opt}</span>
                  </div>

                  {isSelected && <CheckCircle size={18} className="text-[#00E5FF]" />}
                </button>
              );
            })}
          </div>

          {/* Stepper Navigation */}
          <div className="flex items-center justify-between pt-4 border-t border-[#27384E]/70">
            <button
              onClick={() => setCurrentQIndex((prev) => Math.max(0, prev - 1))}
              disabled={currentQIndex === 0}
              className="px-4 py-2 rounded-xl text-xs font-bold bg-[#131F2E] text-[#778DA9] hover:text-white disabled:opacity-40 flex items-center gap-1 cursor-pointer"
            >
              <ChevronLeft size={16} />
              <span>Câu Trước</span>
            </button>

            {currentQIndex < questions.length - 1 ? (
              <button
                onClick={() => setCurrentQIndex((prev) => Math.min(questions.length - 1, prev + 1))}
                className="px-4 py-2 rounded-xl text-xs font-bold bg-[#27384E] text-white hover:bg-[#344966] flex items-center gap-1 cursor-pointer"
              >
                <span>Câu Tiếp</span>
                <ChevronRight size={16} />
              </button>
            ) : (
              <button
                onClick={handleSubmitQuiz}
                disabled={answeredCount === 0}
                className="px-5 py-2.5 rounded-xl text-xs font-extrabold bg-[#06D6A0] text-[#0D1B2A] hover:bg-[#05b88a] shadow-lg shadow-[#06D6A0]/20 flex items-center gap-1 cursor-pointer disabled:opacity-50"
              >
                <span>NỘP BÀI THI</span>
              </button>
            )}
          </div>
        </div>
      )}
    </div>
  );
};
