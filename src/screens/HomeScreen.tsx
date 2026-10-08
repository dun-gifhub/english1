import React, { useState } from 'react';
import { useApp, getDisplayClassName, getCurrentSchoolYearString } from '../context/AppContext';
import { GradeSelector } from '../components/GradeSelector';
import { UnitTopic, TeacherAssignment, GrammarLesson } from '../types';
import {
  Zap,
  Play,
  Copy,
  Check,
  BookOpen,
  GraduationCap,
  Sparkles,
  Trophy,
  ChevronRight,
  ShieldCheck,
  Flame,
  Award
} from 'lucide-react';

interface HomeScreenProps {
  onPlayUnit: (unit: UnitTopic) => void;
  onTakeAssignment: (assignment: TeacherAssignment) => void;
  onOpenTeacherPortal: () => void;
}

export const HomeScreen: React.FC<HomeScreenProps> = ({
  onPlayUnit,
  onTakeAssignment,
  onOpenTeacherPortal
}) => {
  const {
    currentUser,
    selectedGrade,
    subjects,
    grammarLessons,
    assignments
  } = useApp();

  const [activeSection, setActiveSection] = useState<'words' | 'grammar' | 'assignments'>('words');
  const [copiedCode, setCopiedCode] = useState(false);
  const [selectedGrammar, setSelectedGrammar] = useState<GrammarLesson | null>(null);

  const currentSubject = subjects[0];
  const schoolYear = getCurrentSchoolYearString();

  const handleCopyFriendCode = () => {
    if (!currentUser?.friendCode) return;
    navigator.clipboard.writeText(currentUser.friendCode);
    setCopiedCode(true);
    setTimeout(() => setCopiedCode(false), 2000);
  };

  const xpInCurrentLevel = (currentUser?.xp || 0) % 500;
  const xpPercent = Math.min(100, Math.floor((xpInCurrentLevel / 500) * 100));

  return (
    <div className="space-y-6 pb-20">
      {/* Hunter Profile Banner */}
      <div className="relative overflow-hidden rounded-3xl bg-linear-to-br from-[#1B263B] via-[#131F2E] to-[#1B263B] border border-[#2D3F56] p-5 sm:p-6 shadow-xl shadow-black/30">
        <div className="absolute -top-12 -right-12 w-44 h-44 rounded-full bg-[#00E5FF]/10 blur-2xl pointer-events-none" />
        <div className="absolute -bottom-12 -left-12 w-44 h-44 rounded-full bg-[#9D4EDD]/10 blur-2xl pointer-events-none" />

        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-5">
          {/* User info & avatar */}
          <div className="flex items-start sm:items-center gap-4">
            <div
              className="w-14 h-14 sm:w-16 sm:h-16 rounded-2xl flex items-center justify-center shrink-0 shadow-lg shadow-[#00E5FF]/20"
              style={{ backgroundColor: currentUser?.avatarColor || '#00E5FF' }}
            >
              <Zap size={32} className="text-[#0D1B2A] fill-[#0D1B2A]" />
            </div>

            <div>
              <div className="flex items-center gap-2 flex-wrap">
                <h2 className="text-lg sm:text-xl font-extrabold text-white tracking-tight">
                  {currentUser?.displayName || 'Học Sinh Lương Phú'}
                </h2>
                <span className="px-2 py-0.5 rounded-lg text-xs font-bold bg-[#FFD166]/20 text-[#FFD166] flex items-center gap-1 border border-[#FFD166]/30">
                  <Award size={13} />
                  <span>Cấp {currentUser?.level || 1}</span>
                </span>
                <span className="px-2 py-0.5 rounded-lg text-xs font-semibold bg-[#27384E] text-[#00E5FF]">
                  {currentUser ? getDisplayClassName(currentUser) : `Lớp ${selectedGrade}`}
                </span>
              </div>

              <p className="text-xs text-[#778DA9] mt-1 flex items-center gap-1.5 flex-wrap">
                <span>Niên khóa: <strong className="text-white font-semibold">{schoolYear}</strong></span>
                <span>•</span>
                <span>{currentUser?.status || 'Sẵn sàng săn từ vựng!'}</span>
              </p>

              {/* XP Progress Bar */}
              <div className="mt-2.5 w-full max-w-xs sm:max-w-sm">
                <div className="flex justify-between text-[11px] font-semibold text-[#778DA9] mb-1">
                  <span>Tiến độ kinh nghiệm</span>
                  <span className="text-[#00E5FF]">{xpInCurrentLevel} / 500 XP</span>
                </div>
                <div className="w-full h-2 rounded-full bg-[#27384E] overflow-hidden">
                  <div
                    className="h-full rounded-full bg-linear-to-r from-[#00E5FF] to-[#9D4EDD] transition-all duration-500"
                    style={{ width: `${xpPercent}%` }}
                  />
                </div>
              </div>
            </div>
          </div>

          {/* Score & Friend code badges */}
          <div className="flex items-center gap-2 sm:gap-3 shrink-0 flex-wrap">
            <div className="px-3.5 py-2.5 rounded-2xl bg-[#0D1B2A]/80 border border-[#27384E] text-center min-w-[95px]">
              <span className="text-[10px] font-bold text-[#778DA9] uppercase tracking-wider block">Điểm Tháng</span>
              <span className="text-base sm:text-lg font-black text-[#00E5FF]">
                {(currentUser?.monthlyScore || 0).toLocaleString()}
              </span>
            </div>

            <div className="px-3.5 py-2.5 rounded-2xl bg-[#0D1B2A]/80 border border-[#27384E] text-center min-w-[95px]">
              <span className="text-[10px] font-bold text-[#778DA9] uppercase tracking-wider block">Kỷ Lục Điểm</span>
              <span className="text-base sm:text-lg font-black text-[#FFD166]">
                {(currentUser?.highestScore || 0).toLocaleString()}
              </span>
            </div>

            {/* Friend Code Button */}
            <button
              onClick={handleCopyFriendCode}
              className="px-3 py-2.5 rounded-2xl bg-[#27384E] hover:bg-[#344966] text-white border border-[#415A77]/50 flex items-center gap-1.5 transition-all text-xs font-bold cursor-pointer"
              title="Sao chép mã kết bạn"
            >
              {copiedCode ? <Check size={14} className="text-[#06D6A0]" /> : <Copy size={14} />}
              <span>{currentUser?.friendCode || 'LP-2026'}</span>
            </button>
          </div>
        </div>

        {/* Auto advancement education notice */}
        <div className="mt-4 pt-3 border-t border-[#27384E]/60 flex items-center gap-2 text-xs text-[#778DA9]">
          <ShieldCheck size={15} className="text-[#06D6A0] shrink-0" />
          <span>
            Hệ thống phân cấp chuẩn GDPT từ Lớp 6 đến Lớp 12. Khối lớp tự động nâng hạng vào tháng 9 hàng năm.
          </span>
        </div>
      </div>

      {/* Grade Selector Strip */}
      <div className="flex items-center justify-between gap-4 flex-wrap">
        <div>
          <h3 className="text-lg font-black text-white tracking-tight flex items-center gap-2">
            <span>{currentSubject?.title || `Tiếng Anh Lớp ${selectedGrade}`}</span>
            <span className="text-xs px-2 py-0.5 rounded-full bg-[#00E5FF]/10 text-[#00E5FF] font-bold border border-[#00E5FF]/20">
              {currentSubject?.units.length || 0} Chuyên đề
            </span>
          </h3>
          <p className="text-xs text-[#778DA9]">
            {currentSubject?.subtitle || 'Chương trình chuẩn Bộ GD&ĐT'}
          </p>
        </div>

        <GradeSelector />
      </div>

      {/* Section Tabs: Từ Vựng & Game / Ngữ Pháp / Bài Tập Giáo Viên */}
      <div className="flex p-1 rounded-2xl bg-[#131F2E] border border-[#27384E]/70 max-w-md">
        <button
          onClick={() => setActiveSection('words')}
          className={`flex-1 py-2 px-3 rounded-xl text-xs font-bold transition-all flex items-center justify-center gap-1.5 cursor-pointer ${
            activeSection === 'words'
              ? 'bg-[#00E5FF] text-[#0D1B2A] shadow-md shadow-[#00E5FF]/20'
              : 'text-[#ADB5BD] hover:text-white'
          }`}
        >
          <Play size={14} />
          <span>Đấu Từ (Game)</span>
        </button>
        <button
          onClick={() => setActiveSection('grammar')}
          className={`flex-1 py-2 px-3 rounded-xl text-xs font-bold transition-all flex items-center justify-center gap-1.5 cursor-pointer ${
            activeSection === 'grammar'
              ? 'bg-[#00E5FF] text-[#0D1B2A] shadow-md shadow-[#00E5FF]/20'
              : 'text-[#ADB5BD] hover:text-white'
          }`}
        >
          <BookOpen size={14} />
          <span>Ngữ Pháp</span>
        </button>
        <button
          onClick={() => setActiveSection('assignments')}
          className={`flex-1 py-2 px-3 rounded-xl text-xs font-bold transition-all flex items-center justify-center gap-1.5 cursor-pointer ${
            activeSection === 'assignments'
              ? 'bg-[#9D4EDD] text-white shadow-md shadow-[#9D4EDD]/20'
              : 'text-[#ADB5BD] hover:text-white'
          }`}
        >
          <GraduationCap size={14} />
          <span>Đề Thi ({assignments.length})</span>
        </button>
      </div>

      {/* SECTION 1: VOCABULARY & TAP GAME UNITS */}
      {activeSection === 'words' && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {currentSubject?.units.map((unit) => {
            const isTeacherAdded = unit.unitNumber === 0;
            return (
              <div
                key={unit.id}
                className={`group rounded-3xl p-5 border transition-all duration-300 flex flex-col justify-between ${
                  isTeacherAdded
                    ? 'bg-linear-to-br from-[#1E2D40] to-[#251838] border-[#9D4EDD]/60 hover:border-[#9D4EDD]'
                    : 'bg-[#1B263B] border-[#27384E] hover:border-[#00E5FF]/50 hover:shadow-xl hover:shadow-[#00E5FF]/5'
                }`}
              >
                <div>
                  <div className="flex items-center justify-between gap-2 mb-2">
                    <span
                      className={`px-2.5 py-0.5 rounded-lg text-[11px] font-extrabold uppercase tracking-wider ${
                        isTeacherAdded
                          ? 'bg-[#9D4EDD]/20 text-[#9D4EDD] border border-[#9D4EDD]/40'
                          : 'bg-[#00E5FF]/10 text-[#00E5FF] border border-[#00E5FF]/20'
                      }`}
                    >
                      {unit.difficulty}
                    </span>

                    <div className="flex items-center gap-1.5 text-xs text-[#FFD166] font-bold">
                      <Trophy size={13} />
                      <span>{unit.bestScore > 0 ? `${unit.bestScore} pts` : 'Chưa đấu'}</span>
                    </div>
                  </div>

                  <h4 className="text-base font-extrabold text-white group-hover:text-[#00E5FF] transition-colors">
                    {unit.title}
                  </h4>
                  <p className="text-xs text-[#778DA9] mt-1 leading-relaxed">
                    {unit.description}
                  </p>

                  {/* Vocabulary preview pills */}
                  <div className="mt-3.5 flex flex-wrap gap-1.5">
                    {unit.words.slice(0, 4).map((w) => (
                      <span
                        key={w.id}
                        className="px-2 py-1 rounded-lg text-[11px] font-medium bg-[#131F2E] text-[#ADB5BD] border border-[#27384E]/70"
                      >
                        <strong className="text-white font-bold">{w.word}</strong>: {w.meaningVi}
                      </span>
                    ))}
                    {unit.words.length > 4 && (
                      <span className="px-2 py-1 rounded-lg text-[11px] font-semibold bg-[#131F2E] text-[#00E5FF]">
                        +{unit.words.length - 4} từ nữa
                      </span>
                    )}
                  </div>
                </div>

                {/* Play Button */}
                <div className="mt-5 pt-3 border-t border-[#27384E]/60 flex items-center justify-between gap-3">
                  <span className="text-xs font-semibold text-[#778DA9]">
                    {unit.words.length} từ vựng phản xạ
                  </span>

                  <button
                    onClick={() => onPlayUnit(unit)}
                    className={`px-4 py-2 rounded-xl text-xs font-extrabold flex items-center gap-1.5 transition-all shadow-md cursor-pointer ${
                      isTeacherAdded
                        ? 'bg-linear-to-r from-[#9D4EDD] to-[#7B2CBF] text-white hover:from-[#A855F7] hover:to-[#9333EA] shadow-[#9D4EDD]/25'
                        : 'bg-[#00E5FF] text-[#0D1B2A] hover:bg-[#38bdf8] shadow-[#00E5FF]/20 hover:scale-102'
                    }`}
                  >
                    <Play size={14} className="fill-current" />
                    <span>VÀO SĂN TỪ (TAP GAME)</span>
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* SECTION 2: GRAMMAR LESSONS */}
      {activeSection === 'grammar' && (
        <div className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {grammarLessons.map((g) => (
              <div
                key={g.id}
                onClick={() => setSelectedGrammar(g)}
                className="p-5 rounded-3xl bg-[#1B263B] border border-[#27384E] hover:border-[#00E5FF]/60 cursor-pointer transition-all hover:shadow-xl hover:shadow-[#00E5FF]/5"
              >
                <div className="flex items-center justify-between gap-2 mb-2">
                  <span className="px-2 py-0.5 rounded-lg text-[10px] font-bold bg-[#9D4EDD]/20 text-[#9D4EDD]">
                    Ngữ Pháp Lớp {g.grade}
                  </span>
                  <ChevronRight size={16} className="text-[#778DA9]" />
                </div>

                <h4 className="text-base font-bold text-white mb-2">{g.title}</h4>

                {/* Formula box */}
                <div className="p-2.5 rounded-xl bg-[#131F2E] border border-[#2D3F56] text-xs font-mono text-[#00E5FF] mb-2.5 truncate">
                  {g.formula}
                </div>

                <p className="text-xs text-[#ADB5BD] line-clamp-2 leading-relaxed">
                  {g.explanationVi}
                </p>

                <div className="mt-3 text-[11px] text-[#778DA9] italic">
                  Ví dụ: <span className="text-[#FFD166]">{g.exampleEn}</span>
                </div>
              </div>
            ))}
          </div>

          {/* Grammar Modal Details */}
          {selectedGrammar && (
            <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-xs">
              <div className="relative w-full max-w-lg p-6 rounded-3xl bg-[#1E2D40] border border-[#00E5FF]/40 shadow-2xl space-y-4 max-h-[85vh] overflow-y-auto">
                <div className="flex items-center justify-between">
                  <span className="px-2.5 py-1 rounded-lg text-xs font-bold bg-[#9D4EDD]/20 text-[#9D4EDD]">
                    Chuyên Đề Lớp {selectedGrammar.grade}
                  </span>
                  <button
                    onClick={() => setSelectedGrammar(null)}
                    className="p-1 rounded-lg text-[#778DA9] hover:text-white"
                  >
                    ✕
                  </button>
                </div>

                <h3 className="text-lg font-extrabold text-white">{selectedGrammar.title}</h3>

                <div className="p-3.5 rounded-2xl bg-[#131F2E] border border-[#00E5FF]/30">
                  <span className="text-[10px] font-bold text-[#778DA9] uppercase tracking-wider block mb-1">
                    Công thức & Cấu trúc
                  </span>
                  <p className="font-mono text-sm font-semibold text-[#00E5FF]">{selectedGrammar.formula}</p>
                </div>

                <div>
                  <h5 className="text-xs font-bold text-[#778DA9] uppercase tracking-wider mb-1">Giải Thích Ngữ Nghĩa</h5>
                  <p className="text-sm text-white leading-relaxed">{selectedGrammar.explanationVi}</p>
                </div>

                <div className="p-3.5 rounded-2xl bg-[#131F2E] space-y-1.5 border border-[#27384E]">
                  <h5 className="text-xs font-bold text-[#FFD166] uppercase tracking-wider">Ví Dụ Minh Họa</h5>
                  <p className="text-sm font-medium text-white">🇬🇧 {selectedGrammar.exampleEn}</p>
                  <p className="text-xs text-[#ADB5BD]">🇻🇳 {selectedGrammar.exampleVi}</p>
                </div>

                {selectedGrammar.usageNotes && (
                  <div className="p-3 rounded-xl bg-[#27384E]/40 border border-[#27384E] text-xs text-[#ADB5BD]">
                    <strong className="text-[#00E5FF]">Lưu ý làm bài: </strong>
                    {selectedGrammar.usageNotes}
                  </div>
                )}

                <button
                  onClick={() => setSelectedGrammar(null)}
                  className="w-full py-2.5 rounded-xl text-sm font-bold bg-[#00E5FF] text-[#0D1B2A] hover:bg-[#38bdf8] transition-all"
                >
                  Đã Hiểu Kiến Thức
                </button>
              </div>
            </div>
          )}
        </div>
      )}

      {/* SECTION 3: TEACHER ASSIGNMENTS & REPUTABLE EXAMS */}
      {activeSection === 'assignments' && (
        <div className="space-y-4">
          <div className="flex items-center justify-between flex-wrap gap-2">
            <p className="text-xs text-[#778DA9]">
              Bài kiểm tra trắc nghiệm từ vựng & ngữ pháp Lớp {selectedGrade} do giáo viên biên soạn và trích xuất từ ngân hàng đề thi.
            </p>

            {currentUser?.role === 'TEACHER' && (
              <button
                onClick={onOpenTeacherPortal}
                className="px-3 py-1.5 rounded-xl text-xs font-bold bg-[#9D4EDD] text-white hover:bg-[#a855f7] transition-all flex items-center gap-1.5"
              >
                <Sparkles size={14} />
                <span>Mở Cổng Quản Lý Đề Thi</span>
              </button>
            )}
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {assignments.map((assign) => (
              <div
                key={assign.id}
                className="p-5 rounded-3xl bg-[#1B263B] border border-[#27384E] hover:border-[#9D4EDD]/60 transition-all flex flex-col justify-between hover:shadow-xl hover:shadow-[#9D4EDD]/5"
              >
                <div>
                  <div className="flex items-center justify-between gap-2 mb-2">
                    <span className="px-2.5 py-0.5 rounded-lg text-[10px] font-bold bg-[#9D4EDD]/20 text-[#9D4EDD] border border-[#9D4EDD]/30">
                      Lớp {assign.grade} • {assign.questions.length} Câu Hỏi
                    </span>
                    <span className="text-[11px] text-[#778DA9]">
                      {new Date(assign.createdAt).toLocaleDateString('vi-VN')}
                    </span>
                  </div>

                  <h4 className="text-base font-extrabold text-white">{assign.title}</h4>
                  <p className="text-xs text-[#ADB5BD] mt-1 line-clamp-2 leading-relaxed">
                    {assign.description}
                  </p>

                  <div className="mt-3 flex items-center gap-2 text-xs text-[#778DA9]">
                    <GraduationCap size={15} className="text-[#9D4EDD]" />
                    <span>{assign.teacherName}</span>
                  </div>
                </div>

                <div className="mt-5 pt-3 border-t border-[#27384E]/60 flex items-center justify-between">
                  <span className="text-xs font-bold text-[#FFD166] flex items-center gap-1">
                    <Flame size={14} />
                    <span>+{assign.questions.length * 50} XP</span>
                  </span>

                  <button
                    onClick={() => onTakeAssignment(assign)}
                    className="px-4 py-2 rounded-xl text-xs font-bold bg-linear-to-r from-[#9D4EDD] to-[#7B2CBF] hover:from-[#A855F7] hover:to-[#9333EA] text-white transition-all shadow-md shadow-[#9D4EDD]/20 cursor-pointer"
                  >
                    LÀM BÀI KIỂM TRA
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};
