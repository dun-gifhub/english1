import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import { TeacherAssignment, CustomQuestion, ExamSourceType, WordItem, GrammarLesson } from '../types';
import { EXAM_SOURCES, generateReputableExam, getReputableQuestionsPool } from '../services/examBank';
import {
  GraduationCap,
  Plus,
  Sparkles,
  BookOpen,
  Languages,
  Trash2,
  Edit3,
  CheckCircle,
  ArrowRight,
  HelpCircle,
  FileText,
  AlertTriangle,
  Eye,
  X,
  Search,
  Save,
  Check
} from 'lucide-react';

interface TeacherPortalScreenProps {
  onSwitchToStudentRole: () => void;
}

export const TeacherPortalScreen: React.FC<TeacherPortalScreenProps> = ({
  onSwitchToStudentRole
}) => {
  const {
    currentUser,
    assignments,
    customWords,
    customGrammar,
    addTeacherAssignment,
    updateTeacherAssignment,
    deleteTeacherAssignment,
    addCustomWord,
    updateCustomWord,
    deleteCustomWord,
    addCustomGrammar,
    updateCustomGrammar,
    deleteCustomGrammar
  } = useApp();

  const [activeTab, setActiveTab] = useState<'quizzes' | 'words' | 'grammar'>('quizzes');

  // Dialog & Modal states
  const [showManualQuizModal, setShowManualQuizModal] = useState(false);
  const [showReputableExamModal, setShowReputableExamModal] = useState(false);
  const [editingAssignment, setEditingAssignment] = useState<TeacherAssignment | null>(null);
  const [previewAssignment, setPreviewAssignment] = useState<TeacherAssignment | null>(null);

  // Manual Quiz Form state
  const [quizTitle, setQuizTitle] = useState('');
  const [quizDesc, setQuizDesc] = useState('');
  const [quizGrade, setQuizGrade] = useState(10);
  const [quizQuestions, setQuizQuestions] = useState<CustomQuestion[]>([
    {
      id: 'q1',
      question: '',
      options: ['', '', '', ''],
      correctIndex: 0,
      explanation: '',
      topic: 'Ngữ pháp chung'
    }
  ]);

  // Reputable Exam Generation Form state
  const [repSource, setRepSource] = useState<ExamSourceType>('THPT_QUOC_GIA');
  const [repGrade, setRepGrade] = useState(10);
  const [repCount, setRepCount] = useState(5);
  const [repTeacherName, setRepTeacherName] = useState(currentUser?.displayName || 'Tổ Ngoại Ngữ THPT Lương Phú');
  const [repCustomTitle, setRepCustomTitle] = useState('');
  const [repGeneratedPreview, setRepGeneratedPreview] = useState<TeacherAssignment | null>(null);

  // Custom Word Form state
  const [editingWord, setEditingWord] = useState<WordItem | null>(null);
  const [wordGrade, setWordGrade] = useState(10);
  const [wordUnit, setWordUnit] = useState(1);
  const [wordEn, setWordEn] = useState('');
  const [wordIpa, setWordIpa] = useState('');
  const [wordVi, setWordVi] = useState('');
  const [wordExEn, setWordExEn] = useState('');
  const [wordExVi, setWordExVi] = useState('');
  const [distractor1, setDistractor1] = useState('');
  const [distractor2, setDistractor2] = useState('');
  const [distractor3, setDistractor3] = useState('');
  const [wordSuccess, setWordSuccess] = useState(false);

  // Custom Grammar Form state
  const [editingGrammar, setEditingGrammar] = useState<GrammarLesson | null>(null);
  const [grammarGrade, setGrammarGrade] = useState(10);
  const [grammarTitle, setGrammarTitle] = useState('');
  const [grammarFormula, setGrammarFormula] = useState('');
  const [grammarExpl, setGrammarExpl] = useState('');
  const [grammarExEn, setGrammarExEn] = useState('');
  const [grammarExVi, setGrammarExVi] = useState('');
  const [grammarNotes, setGrammarNotes] = useState('');
  const [grammarSuccess, setGrammarSuccess] = useState(false);

  // Filter grade for tabs
  const [filterGrade, setFilterGrade] = useState<number | 'ALL'>('ALL');

  // Handle Reputable Exam Pre-generation Preview
  const handlePreGenerateExam = (e: React.FormEvent) => {
    e.preventDefault();
    const exam = generateReputableExam(
      repGrade,
      repSource,
      repCount,
      repTeacherName,
      repCustomTitle
    );
    setRepGeneratedPreview(exam);
  };

  // Confirm saving the reputable exam (with any teacher edits!)
  const handleConfirmSaveReputableExam = () => {
    if (!repGeneratedPreview) return;
    addTeacherAssignment(repGeneratedPreview);
    setRepGeneratedPreview(null);
    setShowReputableExamModal(false);
  };

  // Open Edit Modal for any assignment
  const handleOpenEditAssignment = (assign: TeacherAssignment) => {
    setEditingAssignment(assign);
    setQuizTitle(assign.title);
    setQuizDesc(assign.description);
    setQuizGrade(assign.grade);
    setQuizQuestions(JSON.parse(JSON.stringify(assign.questions)));
    setShowManualQuizModal(true);
  };

  // Handle Manual/Edited Quiz Submit
  const handleSaveQuiz = (e: React.FormEvent) => {
    e.preventDefault();
    if (!quizTitle.trim()) return;

    // Filter valid questions
    const validQuestions = quizQuestions.filter(q => q.question.trim().length > 0);
    if (validQuestions.length === 0) {
      alert("Đề thi cần có ít nhất 1 câu hỏi có nội dung hợp lệ!");
      return;
    }

    const assignmentToSave: TeacherAssignment = {
      id: editingAssignment ? editingAssignment.id : `assign_${Date.now()}`,
      teacherUid: currentUser?.uid || 'teacher_local',
      teacherName: currentUser?.displayName || 'Thầy Cô Giáo THPT Lương Phú',
      grade: quizGrade,
      title: quizTitle.trim(),
      description: quizDesc.trim() || `Đề kiểm tra Lớp ${quizGrade} do giáo viên biên soạn`,
      questions: validQuestions,
      sourceType: editingAssignment?.sourceType || 'SGK_GLOBAL_SUCCESS',
      createdAt: editingAssignment ? editingAssignment.createdAt : Date.now()
    };

    if (editingAssignment) {
      updateTeacherAssignment(assignmentToSave);
    } else {
      addTeacherAssignment(assignmentToSave);
    }

    setShowManualQuizModal(false);
    setEditingAssignment(null);
    setQuizTitle('');
    setQuizDesc('');
    setQuizQuestions([
      { id: 'q1', question: '', options: ['', '', '', ''], correctIndex: 0, explanation: '' }
    ]);
  };

  // Handle Custom Word Submit (Add or Update)
  const handleSaveWord = (e: React.FormEvent) => {
    e.preventDefault();
    if (!wordEn.trim() || !wordVi.trim()) return;

    if (editingWord) {
      updateCustomWord({
        ...editingWord,
        word: wordEn.trim(),
        phonetic: wordIpa.trim(),
        meaningVi: wordVi.trim(),
        exampleEn: wordExEn.trim(),
        exampleVi: wordExVi.trim(),
        grade: wordGrade,
        unitNumber: wordUnit,
        distractorsVi: [distractor1, distractor2, distractor3].filter(Boolean)
      });
      setEditingWord(null);
    } else {
      addCustomWord({
        word: wordEn.trim(),
        phonetic: wordIpa.trim(),
        meaningVi: wordVi.trim(),
        exampleEn: wordExEn.trim(),
        exampleVi: wordExVi.trim(),
        grade: wordGrade,
        unitNumber: wordUnit,
        distractorsVi: [distractor1, distractor2, distractor3].filter(Boolean)
      });
    }

    setWordEn('');
    setWordIpa('');
    setWordVi('');
    setWordExEn('');
    setWordExVi('');
    setDistractor1('');
    setDistractor2('');
    setDistractor3('');
    setWordSuccess(true);
    setTimeout(() => setWordSuccess(false), 2500);
  };

  const handleStartEditWord = (w: WordItem) => {
    setEditingWord(w);
    setWordGrade(w.grade);
    setWordUnit(w.unitNumber);
    setWordEn(w.word);
    setWordIpa(w.phonetic || '');
    setWordVi(w.meaningVi);
    setWordExEn(w.exampleEn || '');
    setWordExVi(w.exampleVi || '');
    setDistractor1(w.distractorsVi[0] || '');
    setDistractor2(w.distractorsVi[1] || '');
    setDistractor3(w.distractorsVi[2] || '');
    window.scrollTo({ top: 300, behavior: 'smooth' });
  };

  const handleCancelEditWord = () => {
    setEditingWord(null);
    setWordEn('');
    setWordIpa('');
    setWordVi('');
    setWordExEn('');
    setWordExVi('');
    setDistractor1('');
    setDistractor2('');
    setDistractor3('');
  };

  // Handle Custom Grammar Submit (Add or Update)
  const handleSaveGrammar = (e: React.FormEvent) => {
    e.preventDefault();
    if (!grammarTitle.trim() || !grammarFormula.trim()) return;

    if (editingGrammar) {
      updateCustomGrammar({
        ...editingGrammar,
        grade: grammarGrade,
        title: grammarTitle.trim(),
        formula: grammarFormula.trim(),
        explanationVi: grammarExpl.trim(),
        exampleEn: grammarExEn.trim(),
        exampleVi: grammarExVi.trim(),
        usageNotes: grammarNotes.trim()
      });
      setEditingGrammar(null);
    } else {
      addCustomGrammar({
        grade: grammarGrade,
        title: grammarTitle.trim(),
        formula: grammarFormula.trim(),
        explanationVi: grammarExpl.trim(),
        exampleEn: grammarExEn.trim(),
        exampleVi: grammarExVi.trim(),
        usageNotes: grammarNotes.trim()
      });
    }

    setGrammarTitle('');
    setGrammarFormula('');
    setGrammarExpl('');
    setGrammarExEn('');
    setGrammarExVi('');
    setGrammarNotes('');
    setGrammarSuccess(true);
    setTimeout(() => setGrammarSuccess(false), 2500);
  };

  const handleStartEditGrammar = (g: GrammarLesson) => {
    setEditingGrammar(g);
    setGrammarGrade(g.grade);
    setGrammarTitle(g.title);
    setGrammarFormula(g.formula);
    setGrammarExpl(g.explanationVi);
    setGrammarExEn(g.exampleEn);
    setGrammarExVi(g.exampleVi);
    setGrammarNotes(g.usageNotes);
    window.scrollTo({ top: 300, behavior: 'smooth' });
  };

  const handleCancelEditGrammar = () => {
    setEditingGrammar(null);
    setGrammarTitle('');
    setGrammarFormula('');
    setGrammarExpl('');
    setGrammarExEn('');
    setGrammarExVi('');
    setGrammarNotes('');
  };

  // Filtered lists
  const filteredAssignments = assignments.filter(
    (a) => filterGrade === 'ALL' || a.grade === filterGrade
  );
  const filteredWords = customWords.filter(
    (w) => filterGrade === 'ALL' || w.grade === filterGrade
  );
  const filteredGrammar = customGrammar.filter(
    (g) => filterGrade === 'ALL' || g.grade === filterGrade
  );

  return (
    <div className="space-y-6 pb-20">
      {/* Teacher Top Banner */}
      <div className="rounded-3xl p-5 sm:p-6 bg-linear-to-r from-[#1B263B] via-[#241738] to-[#1B263B] border border-[#9D4EDD]/40 shadow-xl flex items-center justify-between gap-4 flex-wrap">
        <div className="flex items-center gap-3.5">
          <div className="w-12 h-12 rounded-2xl bg-[#9D4EDD]/20 text-[#9D4EDD] flex items-center justify-center shrink-0 border border-[#9D4EDD]/30">
            <GraduationCap size={28} />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="text-[10px] font-extrabold uppercase tracking-widest text-[#9D4EDD]">
                Nhánh Quản Trị Giáo Viên
              </span>
              <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-[#06D6A0]/20 text-[#06D6A0]">
                Quyền Ra Đề & Chỉnh Sửa Toàn Quyền
              </span>
            </div>
            <h2 className="text-lg font-black text-white">
              {currentUser?.displayName || 'Thầy Cô Giáo THPT Lương Phú'}
            </h2>
            <p className="text-xs text-[#778DA9]">
              Quản lý đề thi, câu hỏi chuẩn hóa, từ vựng & ngữ pháp Lớp 6 - 12 (Đồng bộ mọi thiết bị)
            </p>
          </div>
        </div>

        <button
          onClick={onSwitchToStudentRole}
          className="px-4 py-2.5 rounded-xl text-xs font-bold text-[#00E5FF] bg-[#1E2D40] hover:bg-[#27384E] border border-[#00E5FF]/40 transition-all flex items-center gap-1.5 cursor-pointer shadow-md"
        >
          <span>Góc Học Sinh</span>
          <ArrowRight size={14} />
        </button>
      </div>

      {/* Tabs & Grade Filters */}
      <div className="flex flex-col sm:flex-row items-stretch sm:items-center justify-between gap-3 flex-wrap">
        {/* Navigation Tabs */}
        <div className="flex p-1 rounded-2xl bg-[#131F2E] border border-[#27384E] max-w-lg">
          <button
            onClick={() => setActiveTab('quizzes')}
            className={`flex-1 py-2 px-3.5 rounded-xl text-xs font-bold transition-all flex items-center justify-center gap-1.5 cursor-pointer ${
              activeTab === 'quizzes'
                ? 'bg-[#9D4EDD] text-white shadow-md shadow-[#9D4EDD]/20'
                : 'text-[#ADB5BD] hover:text-white'
            }`}
          >
            <FileText size={14} />
            <span>Đề Thi & Câu Hỏi ({assignments.length})</span>
          </button>
          <button
            onClick={() => setActiveTab('words')}
            className={`flex-1 py-2 px-3.5 rounded-xl text-xs font-bold transition-all flex items-center justify-center gap-1.5 cursor-pointer ${
              activeTab === 'words'
                ? 'bg-[#9D4EDD] text-white shadow-md shadow-[#9D4EDD]/20'
                : 'text-[#ADB5BD] hover:text-white'
            }`}
          >
            <Languages size={14} />
            <span>Từ Mới ({customWords.length})</span>
          </button>
          <button
            onClick={() => setActiveTab('grammar')}
            className={`flex-1 py-2 px-3.5 rounded-xl text-xs font-bold transition-all flex items-center justify-center gap-1.5 cursor-pointer ${
              activeTab === 'grammar'
                ? 'bg-[#9D4EDD] text-white shadow-md shadow-[#9D4EDD]/20'
                : 'text-[#ADB5BD] hover:text-white'
            }`}
          >
            <BookOpen size={14} />
            <span>Ngữ Pháp ({customGrammar.length})</span>
          </button>
        </div>

        {/* Grade Filter Pill Select */}
        <div className="flex items-center gap-1.5 overflow-x-auto pb-1 text-xs">
          <span className="text-[#778DA9] font-semibold text-[11px] whitespace-nowrap">Lọc Khối:</span>
          <button
            onClick={() => setFilterGrade('ALL')}
            className={`px-2.5 py-1 rounded-lg text-[11px] font-bold cursor-pointer transition-all ${
              filterGrade === 'ALL'
                ? 'bg-[#00E5FF] text-[#0D1B2A]'
                : 'bg-[#131F2E] text-[#778DA9] hover:text-white'
            }`}
          >
            Tất Cả
          </button>
          {[6, 7, 8, 9, 10, 11, 12].map((g) => (
            <button
              key={g}
              onClick={() => setFilterGrade(g)}
              className={`px-2.5 py-1 rounded-lg text-[11px] font-bold cursor-pointer transition-all ${
                filterGrade === g
                  ? 'bg-[#00E5FF] text-[#0D1B2A]'
                  : 'bg-[#131F2E] text-[#778DA9] hover:text-white'
              }`}
            >
              K{g}
            </button>
          ))}
        </div>
      </div>

      {/* ============================================================ */}
      {/* TAB 1: RA ĐỀ THI & QUẢN LÝ CÂU HỎI (EDIT / DELETE / GENERATE) */}
      {/* ============================================================ */}
      {activeTab === 'quizzes' && (
        <div className="space-y-4">
          <div className="flex items-center justify-between gap-3 flex-wrap">
            <div>
              <h3 className="text-base font-bold text-white flex items-center gap-2">
                <span>Quản Lý Đề Thi & Bài Tập Trắc Nghiệm</span>
                <span className="text-xs px-2 py-0.5 rounded-full bg-[#1E2D40] text-[#778DA9] border border-[#27384E]">
                  {filteredAssignments.length} đề
                </span>
              </h3>
              <p className="text-xs text-[#778DA9]">
                Giáo viên có thể chỉnh sửa nội dung, sửa sai sót từng câu hỏi hoặc xóa đề bất kỳ lúc nào.
              </p>
            </div>

            <div className="flex items-center gap-2">
              <button
                onClick={() => {
                  setRepGeneratedPreview(null);
                  setShowReputableExamModal(true);
                }}
                className="px-3.5 py-2 rounded-xl text-xs font-bold bg-[#FFD166] text-[#0D1B2A] hover:bg-[#ffe082] transition-all flex items-center gap-1.5 cursor-pointer shadow-md shadow-[#FFD166]/20"
              >
                <Sparkles size={14} />
                <span>Sinh Đề Nguồn Uy Tín</span>
              </button>

              <button
                onClick={() => {
                  setEditingAssignment(null);
                  setQuizTitle('');
                  setQuizDesc('');
                  setQuizGrade(10);
                  setQuizQuestions([
                    {
                      id: `q_${Date.now()}`,
                      question: '',
                      options: ['', '', '', ''],
                      correctIndex: 0,
                      explanation: '',
                      topic: 'Ngữ pháp chung'
                    }
                  ]);
                  setShowManualQuizModal(true);
                }}
                className="px-3.5 py-2 rounded-xl text-xs font-bold bg-[#9D4EDD] text-white hover:bg-[#a855f7] transition-all flex items-center gap-1.5 cursor-pointer shadow-md shadow-[#9D4EDD]/20"
              >
                <Plus size={14} />
                <span>Tạo Đề Mới Thủ Công</span>
              </button>
            </div>
          </div>

          {/* List of current assignments */}
          {filteredAssignments.length === 0 ? (
            <div className="text-center py-12 rounded-3xl bg-[#1B263B] border border-dashed border-[#27384E] space-y-3">
              <FileText size={36} className="mx-auto text-[#778DA9]" />
              <p className="text-sm font-semibold text-white">Chưa có đề thi nào ở khối này</p>
              <p className="text-xs text-[#778DA9]">Bấm "Sinh Đề Nguồn Uy Tín" để tự động tạo đề từ Bộ GD&ĐT hoặc "Tạo Đề Mới Thủ Công".</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {filteredAssignments.map((assign) => (
                <div
                  key={assign.id}
                  className="p-5 rounded-3xl bg-[#1B263B] border border-[#27384E] flex flex-col justify-between hover:border-[#9D4EDD]/60 transition-all shadow-md group"
                >
                  <div>
                    <div className="flex items-center justify-between gap-2 mb-2">
                      <div className="flex items-center gap-1.5 flex-wrap">
                        <span className="px-2.5 py-0.5 rounded-lg text-[10px] font-bold bg-[#9D4EDD]/20 text-[#9D4EDD]">
                          Khối Lớp {assign.grade}
                        </span>
                        <span className="px-2 py-0.5 rounded-lg text-[10px] font-bold bg-[#00E5FF]/20 text-[#00E5FF]">
                          {assign.questions.length} Câu Hỏi
                        </span>
                        {assign.sourceType && EXAM_SOURCES[assign.sourceType] && (
                          <span className="px-2 py-0.5 rounded-lg text-[10px] font-bold bg-[#FFD166]/20 text-[#FFD166]">
                            {EXAM_SOURCES[assign.sourceType].badge}
                          </span>
                        )}
                      </div>
                      <span className="text-[11px] text-[#778DA9]">
                        {new Date(assign.createdAt).toLocaleDateString('vi-VN')}
                      </span>
                    </div>

                    <h4 className="text-base font-bold text-white group-hover:text-[#00E5FF] transition-colors">
                      {assign.title}
                    </h4>
                    <p className="text-xs text-[#ADB5BD] mt-1.5 line-clamp-2 leading-relaxed">
                      {assign.description}
                    </p>
                    <p className="text-xs text-[#778DA9] mt-2">
                      Biên soạn: <strong className="text-white">{assign.teacherName}</strong>
                    </p>
                  </div>

                  <div className="mt-4 pt-3.5 border-t border-[#27384E]/70 flex items-center justify-between">
                    <button
                      onClick={() => setPreviewAssignment(assign)}
                      className="text-xs text-[#778DA9] hover:text-[#00E5FF] font-semibold flex items-center gap-1 cursor-pointer"
                    >
                      <Eye size={13} />
                      <span>Xem chi tiết câu hỏi</span>
                    </button>

                    <div className="flex items-center gap-2">
                      {/* EDIT ASSIGNMENT BUTTON */}
                      <button
                        onClick={() => handleOpenEditAssignment(assign)}
                        className="px-3 py-1.5 rounded-xl bg-[#131F2E] hover:bg-[#00E5FF]/20 text-[#00E5FF] border border-[#00E5FF]/30 transition-all text-xs font-bold flex items-center gap-1 cursor-pointer shadow-xs"
                        title="Chỉnh sửa hoặc sửa sai sót đề thi"
                      >
                        <Edit3 size={13} />
                        <span>Sửa Đề</span>
                      </button>

                      {/* DELETE ASSIGNMENT BUTTON */}
                      <button
                        onClick={() => {
                          if (window.confirm(`XÁC NHẬN XÓA:\nBạn có chắc chắn muốn xóa vĩnh viễn đề thi '${assign.title}'?`)) {
                            deleteTeacherAssignment(assign.id);
                          }
                        }}
                        className="p-1.5 rounded-xl bg-[#131F2E] hover:bg-[#EF476F]/20 text-[#EF476F] border border-[#EF476F]/30 transition-all cursor-pointer"
                        title="Xóa đề thi khi sai sót hoặc không dùng"
                      >
                        <Trash2 size={14} />
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* ============================================================ */}
      {/* TAB 2: SOẠN TỪ MỚI & DANH SÁCH TỪ ĐÃ SOẠN (EDIT / DELETE)   */}
      {/* ============================================================ */}
      {activeTab === 'words' && (
        <div className="space-y-6">
          {/* Form to Add or Edit Vocabulary */}
          <div className="p-6 rounded-3xl bg-[#1B263B] border border-[#27384E] space-y-4 shadow-xl">
            <div className="flex items-center justify-between">
              <h3 className="text-base font-bold text-white flex items-center gap-2">
                <Languages size={18} className="text-[#9D4EDD]" />
                <span>
                  {editingWord ? `Chỉnh Sửa Từ Vựng: "${editingWord.word}"` : 'Thêm Từ Vựng Mới Vào Giáo Trình Khối Lớp'}
                </span>
              </h3>
              {editingWord && (
                <button
                  onClick={handleCancelEditWord}
                  className="text-xs text-[#EF476F] hover:underline font-bold"
                >
                  Hủy Chỉnh Sửa
                </button>
              )}
            </div>

            <form onSubmit={handleSaveWord} className="space-y-4">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-[#778DA9] mb-1">Khối Lớp</label>
                  <select
                    value={wordGrade}
                    onChange={(e) => setWordGrade(Number(e.target.value))}
                    className="w-full px-3 py-2 text-sm rounded-xl bg-[#131F2E] border border-[#27384E] text-white"
                  >
                    {[6, 7, 8, 9, 10, 11, 12].map((g) => (
                      <option key={g} value={g}>Lớp {g}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-semibold text-[#778DA9] mb-1">Unit Số (1 - 10)</label>
                  <input
                    type="number"
                    min={0}
                    max={12}
                    value={wordUnit}
                    onChange={(e) => setWordUnit(Number(e.target.value))}
                    className="w-full px-3 py-2 text-sm rounded-xl bg-[#131F2E] border border-[#27384E] text-white"
                  />
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-[#778DA9] mb-1">Từ Tiếng Anh *</label>
                  <input
                    type="text"
                    required
                    value={wordEn}
                    onChange={(e) => setWordEn(e.target.value)}
                    placeholder="e.g. perseverance"
                    className="w-full px-3 py-2 text-sm rounded-xl bg-[#131F2E] border border-[#27384E] text-white"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-[#778DA9] mb-1">Phiên Âm IPA</label>
                  <input
                    type="text"
                    value={wordIpa}
                    onChange={(e) => setWordIpa(e.target.value)}
                    placeholder="/ˌpɜː.sɪˈvɪə.rəns/"
                    className="w-full px-3 py-2 text-sm rounded-xl bg-[#131F2E] border border-[#27384E] text-white"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-[#778DA9] mb-1">Nghĩa Tiếng Việt *</label>
                  <input
                    type="text"
                    required
                    value={wordVi}
                    onChange={(e) => setWordVi(e.target.value)}
                    placeholder="lòng kiên trì, sự bền chí"
                    className="w-full px-3 py-2 text-sm rounded-xl bg-[#131F2E] border border-[#27384E] text-white"
                  />
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-[#778DA9] mb-1">Ví Dụ Tiếng Anh</label>
                  <input
                    type="text"
                    value={wordExEn}
                    onChange={(e) => setWordExEn(e.target.value)}
                    placeholder="Success comes with perseverance."
                    className="w-full px-3 py-2 text-sm rounded-xl bg-[#131F2E] border border-[#27384E] text-white"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-[#778DA9] mb-1">Dịch Nghĩa Ví Dụ</label>
                  <input
                    type="text"
                    value={wordExVi}
                    onChange={(e) => setWordExVi(e.target.value)}
                    placeholder="Thành công đến cùng sự kiên trì."
                    className="w-full px-3 py-2 text-sm rounded-xl bg-[#131F2E] border border-[#27384E] text-white"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-[#778DA9] mb-1">
                  3 Đáp Án Nhiễu Tiếng Việt (Dùng cho Game Đấu Từ Tap Hunt)
                </label>
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-2">
                  <input
                    type="text"
                    value={distractor1}
                    onChange={(e) => setDistractor1(e.target.value)}
                    placeholder="Nhiễu 1 (vd: sự nóng vội)"
                    className="w-full px-3 py-2 text-sm rounded-xl bg-[#131F2E] border border-[#27384E] text-white"
                  />
                  <input
                    type="text"
                    value={distractor2}
                    onChange={(e) => setDistractor2(e.target.value)}
                    placeholder="Nhiễu 2 (vd: tính lười biếng)"
                    className="w-full px-3 py-2 text-sm rounded-xl bg-[#131F2E] border border-[#27384E] text-white"
                  />
                  <input
                    type="text"
                    value={distractor3}
                    onChange={(e) => setDistractor3(e.target.value)}
                    placeholder="Nhiễu 3 (vd: lòng kiêu ngạo)"
                    className="w-full px-3 py-2 text-sm rounded-xl bg-[#131F2E] border border-[#27384E] text-white"
                  />
                </div>
              </div>

              {wordSuccess && (
                <div className="flex items-center gap-2 text-xs font-bold text-[#06D6A0]">
                  <CheckCircle size={15} />
                  <span>Đã lưu từ vựng thành công vào hệ thống!</span>
                </div>
              )}

              <div className="flex gap-2">
                <button
                  type="submit"
                  className="px-5 py-2.5 rounded-xl text-xs font-bold text-white bg-linear-to-r from-[#9D4EDD] to-[#7B2CBF] hover:from-[#A855F7] transition-all cursor-pointer shadow-md shadow-[#9D4EDD]/20"
                >
                  {editingWord ? 'Cập Nhật Từ Vựng' : 'Lưu Từ Vựng Vào Giáo Trình'}
                </button>
                {editingWord && (
                  <button
                    type="button"
                    onClick={handleCancelEditWord}
                    className="px-4 py-2.5 rounded-xl text-xs font-bold text-[#778DA9] bg-[#131F2E] hover:text-white"
                  >
                    Hủy
                  </button>
                )}
              </div>
            </form>
          </div>

          {/* List of Custom Words with Edit & Delete */}
          <div className="space-y-3">
            <h4 className="text-sm font-bold text-white flex items-center justify-between">
              <span>Danh Sách Từ Vựng Giáo Viên Đã Soạn ({filteredWords.length})</span>
              <span className="text-xs text-[#778DA9]">Có thể sửa hoặc xóa khi sai</span>
            </h4>

            {filteredWords.length === 0 ? (
              <p className="text-xs text-[#778DA9] italic">Chưa có từ vựng tự tạo nào trong khối lớp này.</p>
            ) : (
              <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-3">
                {filteredWords.map((w) => (
                  <div
                    key={w.id}
                    className="p-4 rounded-2xl bg-[#1B263B] border border-[#27384E] flex flex-col justify-between hover:border-[#00E5FF]/40 transition-all"
                  >
                    <div>
                      <div className="flex items-center justify-between mb-1">
                        <span className="text-[10px] font-bold text-[#9D4EDD] bg-[#9D4EDD]/20 px-2 py-0.5 rounded-md">
                          Lớp {w.grade} • Unit {w.unitNumber}
                        </span>
                        {w.phonetic && (
                          <span className="text-[11px] font-mono text-[#FFD166]">{w.phonetic}</span>
                        )}
                      </div>
                      <h5 className="text-base font-black text-white">{w.word}</h5>
                      <p className="text-xs font-semibold text-[#00E5FF] mt-0.5">{w.meaningVi}</p>
                      {w.exampleEn && (
                        <p className="text-[11px] text-[#778DA9] mt-1.5 italic line-clamp-1">
                          "{w.exampleEn}"
                        </p>
                      )}
                    </div>

                    <div className="mt-3 pt-2.5 border-t border-[#27384E] flex items-center justify-end gap-1.5">
                      <button
                        onClick={() => handleStartEditWord(w)}
                        className="px-2.5 py-1 rounded-lg bg-[#131F2E] hover:bg-[#00E5FF]/20 text-[#00E5FF] text-xs font-bold transition-all flex items-center gap-1 cursor-pointer"
                        title="Sửa từ"
                      >
                        <Edit3 size={12} />
                        <span>Sửa</span>
                      </button>
                      <button
                        onClick={() => {
                          if (window.confirm(`Bạn có chắc muốn xóa từ '${w.word}'?`)) {
                            deleteCustomWord(w.id);
                          }
                        }}
                        className="p-1 rounded-lg bg-[#131F2E] hover:bg-[#EF476F]/20 text-[#EF476F] transition-all cursor-pointer"
                        title="Xóa từ"
                      >
                        <Trash2 size={14} />
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}

      {/* ============================================================ */}
      {/* TAB 3: SOẠN NGỮ PHÁP & DANH SÁCH CHUYÊN ĐỀ (EDIT / DELETE)   */}
      {/* ============================================================ */}
      {activeTab === 'grammar' && (
        <div className="space-y-6">
          {/* Form to Add or Edit Grammar */}
          <div className="p-6 rounded-3xl bg-[#1B263B] border border-[#27384E] space-y-4 shadow-xl">
            <div className="flex items-center justify-between">
              <h3 className="text-base font-bold text-white flex items-center gap-2">
                <BookOpen size={18} className="text-[#9D4EDD]" />
                <span>
                  {editingGrammar ? `Chỉnh Sửa Ngữ Pháp: "${editingGrammar.title}"` : 'Thêm Chuyên Đề Ngữ Pháp Mới'}
                </span>
              </h3>
              {editingGrammar && (
                <button
                  onClick={handleCancelEditGrammar}
                  className="text-xs text-[#EF476F] hover:underline font-bold"
                >
                  Hủy Chỉnh Sửa
                </button>
              )}
            </div>

            <form onSubmit={handleSaveGrammar} className="space-y-4">
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-[#778DA9] mb-1">Khối Lớp</label>
                  <select
                    value={grammarGrade}
                    onChange={(e) => setGrammarGrade(Number(e.target.value))}
                    className="w-full px-3 py-2 text-sm rounded-xl bg-[#131F2E] border border-[#27384E] text-white"
                  >
                    {[6, 7, 8, 9, 10, 11, 12].map((g) => (
                      <option key={g} value={g}>Lớp {g}</option>
                    ))}
                  </select>
                </div>
                <div className="sm:col-span-2">
                  <label className="block text-xs font-semibold text-[#778DA9] mb-1">Tên Chuyên Đề Ngữ Pháp *</label>
                  <input
                    type="text"
                    required
                    value={grammarTitle}
                    onChange={(e) => setGrammarTitle(e.target.value)}
                    placeholder="e.g. Cấu trúc Đảo Ngữ nâng cao (Inversion)"
                    className="w-full px-3 py-2 text-sm rounded-xl bg-[#131F2E] border border-[#27384E] text-white"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-[#778DA9] mb-1">Công Thức / Cấu Trúc Trọng Tâm *</label>
                <input
                  type="text"
                  required
                  value={grammarFormula}
                  onChange={(e) => setGrammarFormula(e.target.value)}
                  placeholder="e.g. Seldom / Rarely + Trợ động từ + S + V-inf"
                  className="w-full px-3 py-2 text-sm font-mono rounded-xl bg-[#131F2E] border border-[#27384E] text-[#00E5FF]"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-[#778DA9] mb-1">Giải Thích Ngữ Nghĩa & Cách Dùng</label>
                <textarea
                  rows={2}
                  value={grammarExpl}
                  onChange={(e) => setGrammarExpl(e.target.value)}
                  placeholder="Dùng để nhấn mạnh mức độ hiếm hoi trong câu văn học thuật..."
                  className="w-full px-3 py-2 text-sm rounded-xl bg-[#131F2E] border border-[#27384E] text-white"
                />
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-[#778DA9] mb-1">Ví Dụ Tiếng Anh</label>
                  <input
                    type="text"
                    value={grammarExEn}
                    onChange={(e) => setGrammarExEn(e.target.value)}
                    placeholder="Seldom have I seen such brilliance."
                    className="w-full px-3 py-2 text-sm rounded-xl bg-[#131F2E] border border-[#27384E] text-white"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-[#778DA9] mb-1">Dịch Nghĩa Ví Dụ</label>
                  <input
                    type="text"
                    value={grammarExVi}
                    onChange={(e) => setGrammarExVi(e.target.value)}
                    placeholder="Hiếm khi tôi thấy sự xuất sắc đến vậy."
                    className="w-full px-3 py-2 text-sm rounded-xl bg-[#131F2E] border border-[#27384E] text-white"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-[#778DA9] mb-1">Lưu Ý & Dấu Hiệu Nhận Biết</label>
                <input
                  type="text"
                  value={grammarNotes}
                  onChange={(e) => setGrammarNotes(e.target.value)}
                  placeholder="Trợ động từ đảo lên trước chủ ngữ tương tự câu hỏi nghi vấn."
                  className="w-full px-3 py-2 text-sm rounded-xl bg-[#131F2E] border border-[#27384E] text-white"
                />
              </div>

              {grammarSuccess && (
                <div className="flex items-center gap-2 text-xs font-bold text-[#06D6A0]">
                  <CheckCircle size={15} />
                  <span>Đã lưu chuyên đề ngữ pháp thành công!</span>
                </div>
              )}

              <div className="flex gap-2">
                <button
                  type="submit"
                  className="px-5 py-2.5 rounded-xl text-xs font-bold text-white bg-linear-to-r from-[#9D4EDD] to-[#7B2CBF] hover:from-[#A855F7] transition-all cursor-pointer shadow-md shadow-[#9D4EDD]/20"
                >
                  {editingGrammar ? 'Cập Nhật Chuyên Đề' : 'Lưu Chuyên Đề Ngữ Pháp'}
                </button>
                {editingGrammar && (
                  <button
                    type="button"
                    onClick={handleCancelEditGrammar}
                    className="px-4 py-2.5 rounded-xl text-xs font-bold text-[#778DA9] bg-[#131F2E] hover:text-white"
                  >
                    Hủy
                  </button>
                )}
              </div>
            </form>
          </div>

          {/* List of Custom Grammar with Edit & Delete */}
          <div className="space-y-3">
            <h4 className="text-sm font-bold text-white flex items-center justify-between">
              <span>Danh Sách Ngữ Pháp Giáo Viên Đã Soạn ({filteredGrammar.length})</span>
              <span className="text-xs text-[#778DA9]">Có thể sửa công thức hoặc xóa khi sai</span>
            </h4>

            {filteredGrammar.length === 0 ? (
              <p className="text-xs text-[#778DA9] italic">Chưa có bài ngữ pháp tự tạo nào trong khối lớp này.</p>
            ) : (
              <div className="space-y-3">
                {filteredGrammar.map((g) => (
                  <div
                    key={g.id}
                    className="p-5 rounded-2xl bg-[#1B263B] border border-[#27384E] flex items-start justify-between gap-4 hover:border-[#9D4EDD]/40 transition-all"
                  >
                    <div className="space-y-1.5 flex-1">
                      <div className="flex items-center gap-2">
                        <span className="text-[10px] font-bold text-[#9D4EDD] bg-[#9D4EDD]/20 px-2 py-0.5 rounded-md">
                          Khối Lớp {g.grade}
                        </span>
                        <h5 className="text-base font-bold text-white">{g.title}</h5>
                      </div>
                      <p className="text-xs font-mono text-[#00E5FF] bg-[#131F2E] p-2 rounded-xl inline-block">
                        {g.formula}
                      </p>
                      {g.explanationVi && (
                        <p className="text-xs text-[#ADB5BD]">{g.explanationVi}</p>
                      )}
                      {g.exampleEn && (
                        <p className="text-xs text-[#FFD166] italic">
                          Ví dụ: {g.exampleEn} ({g.exampleVi})
                        </p>
                      )}
                    </div>

                    <div className="flex items-center gap-1.5 shrink-0">
                      <button
                        onClick={() => handleStartEditGrammar(g)}
                        className="px-3 py-1.5 rounded-xl bg-[#131F2E] hover:bg-[#00E5FF]/20 text-[#00E5FF] text-xs font-bold transition-all flex items-center gap-1 cursor-pointer"
                        title="Sửa chuyên đề"
                      >
                        <Edit3 size={13} />
                        <span>Sửa</span>
                      </button>
                      <button
                        onClick={() => {
                          if (window.confirm(`Bạn có chắc muốn xóa chuyên đề '${g.title}'?`)) {
                            deleteCustomGrammar(g.id);
                          }
                        }}
                        className="p-2 rounded-xl bg-[#131F2E] hover:bg-[#EF476F]/20 text-[#EF476F] transition-all cursor-pointer"
                        title="Xóa chuyên đề"
                      >
                        <Trash2 size={14} />
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}

      {/* ============================================================ */}
      {/* MODAL 1: SINH ĐỀ TỪ NGUỒN UY TÍN (CÓ XEM TRƯỚC & SỬA CÂU HỎI) */}
      {/* ============================================================ */}
      {showReputableExamModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/85 backdrop-blur-xs">
          <div className="relative w-full max-w-2xl p-6 rounded-3xl bg-[#1E2D40] border border-[#FFD166]/40 shadow-2xl space-y-4 max-h-[92vh] overflow-y-auto">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Sparkles size={20} className="text-[#FFD166]" />
                <h3 className="text-base font-extrabold text-white">
                  Ngân Hàng Đề Thi Chuẩn Hóa Uy Tín
                </h3>
              </div>
              <button
                onClick={() => {
                  setShowReputableExamModal(false);
                  setRepGeneratedPreview(null);
                }}
                className="text-[#778DA9] hover:text-white"
              >
                ✕
              </button>
            </div>

            {!repGeneratedPreview ? (
              // Step 1: Select parameters
              <form onSubmit={handlePreGenerateExam} className="space-y-4">
                <p className="text-xs text-[#ADB5BD]">
                  Tự động trích xuất bộ câu hỏi chuẩn hóa bám sát ma trận đề thi từ Bộ GD&ĐT, Kỳ thi HSG, Tuyển sinh Lớp 10 Chuyên, Cambridge và SGK Global Success.
                </p>

                <div>
                  <label className="block text-xs font-semibold text-[#778DA9] mb-1">Nguồn Đề Thi Uy Tín</label>
                  <select
                    value={repSource}
                    onChange={(e) => setRepSource(e.target.value as ExamSourceType)}
                    className="w-full px-3 py-2.5 text-xs font-semibold rounded-xl bg-[#131F2E] border border-[#27384E] text-white"
                  >
                    {Object.entries(EXAM_SOURCES).map(([k, meta]) => (
                      <option key={k} value={k}>
                        {meta.sourceName} ({meta.badge})
                      </option>
                    ))}
                  </select>
                  <div className="mt-1.5 p-2.5 rounded-xl bg-[#131F2E] border border-[#27384E] text-[11px] text-[#778DA9]">
                    <p className="text-[#FFD166] font-semibold">{EXAM_SOURCES[repSource].description}</p>
                    {EXAM_SOURCES[repSource].yearCitation && (
                      <p className="text-[#00E5FF] mt-0.5">Trích dẫn: {EXAM_SOURCES[repSource].yearCitation}</p>
                    )}
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="block text-xs font-semibold text-[#778DA9] mb-1">Khối Lớp</label>
                    <select
                      value={repGrade}
                      onChange={(e) => setRepGrade(Number(e.target.value))}
                      className="w-full px-3 py-2 text-xs rounded-xl bg-[#131F2E] border border-[#27384E] text-white"
                    >
                      {[6, 7, 8, 9, 10, 11, 12].map((g) => (
                        <option key={g} value={g}>Lớp {g}</option>
                      ))}
                    </select>
                  </div>
                  <div>
                    <label className="block text-xs font-semibold text-[#778DA9] mb-1">Số Lượng Câu Hỏi</label>
                    <input
                      type="number"
                      min={3}
                      max={12}
                      value={repCount}
                      onChange={(e) => setRepCount(Number(e.target.value))}
                      className="w-full px-3 py-2 text-xs rounded-xl bg-[#131F2E] border border-[#27384E] text-white"
                    />
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-[#778DA9] mb-1">Tiêu Đề Tùy Biến (Tùy chọn)</label>
                  <input
                    type="text"
                    value={repCustomTitle}
                    onChange={(e) => setRepCustomTitle(e.target.value)}
                    placeholder="Để trống sẽ tự đặt theo nguồn và khối lớp"
                    className="w-full px-3 py-2 text-xs rounded-xl bg-[#131F2E] border border-[#27384E] text-white"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-[#778DA9] mb-1">Tên Giáo Viên / Tổ Bộ Môn</label>
                  <input
                    type="text"
                    value={repTeacherName}
                    onChange={(e) => setRepTeacherName(e.target.value)}
                    className="w-full px-3 py-2 text-xs rounded-xl bg-[#131F2E] border border-[#27384E] text-white"
                  />
                </div>

                <div className="flex gap-2 pt-2">
                  <button
                    type="button"
                    onClick={() => setShowReputableExamModal(false)}
                    className="flex-1 py-2.5 rounded-xl text-xs font-bold text-[#778DA9] bg-[#131F2E] hover:text-white"
                  >
                    Hủy
                  </button>
                  <button
                    type="submit"
                    className="flex-1 py-2.5 rounded-xl text-xs font-extrabold bg-[#FFD166] text-[#0D1B2A] hover:bg-[#ffe082] transition-all shadow-md shadow-[#FFD166]/20 cursor-pointer flex items-center justify-center gap-1.5"
                  >
                    <Eye size={14} />
                    <span>Trích Xuất & Xem Trước Câu Hỏi</span>
                  </button>
                </div>
              </form>
            ) : (
              // Step 2: Interactive Preview and Question Review/Edit before saving
              <div className="space-y-4">
                <div className="p-3 rounded-2xl bg-[#131F2E] border border-[#FFD166]/30">
                  <span className="text-[10px] font-bold uppercase text-[#FFD166]">Xem trước đề chuẩn hóa:</span>
                  <h4 className="text-sm font-bold text-white">{repGeneratedPreview.title}</h4>
                  <p className="text-xs text-[#778DA9]">{repGeneratedPreview.description}</p>
                </div>

                <div className="space-y-3 max-h-[50vh] overflow-y-auto pr-1">
                  <div className="flex items-center justify-between text-xs text-[#778DA9]">
                    <span>Kiểm tra & sửa sai sót ({repGeneratedPreview.questions.length} câu)</span>
                    <span className="text-[#06D6A0] font-semibold">Tất cả thông tin đã chuẩn hóa</span>
                  </div>

                  {repGeneratedPreview.questions.map((q, idx) => (
                    <div key={q.id || idx} className="p-3.5 rounded-xl bg-[#131F2E] border border-[#27384E] space-y-2 text-xs">
                      <div className="flex items-center justify-between font-bold">
                        <span className="text-[#00E5FF]">Câu #{idx + 1} {q.topic ? `• ${q.topic}` : ''}</span>
                        {q.sourceName && (
                          <span className="text-[10px] text-[#FFD166]">{q.sourceName}</span>
                        )}
                      </div>

                      <p className="text-white font-medium">{q.question}</p>

                      <div className="grid grid-cols-2 gap-1.5 text-[11px]">
                        {q.options.map((opt, optIdx) => (
                          <div
                            key={optIdx}
                            className={`p-1.5 rounded-lg border ${
                              optIdx === q.correctIndex
                                ? 'bg-[#06D6A0]/15 border-[#06D6A0] text-[#06D6A0] font-bold'
                                : 'bg-[#1B263B] border-[#27384E] text-[#ADB5BD]'
                            }`}
                          >
                            {String.fromCharCode(65 + optIdx)}. {opt}
                          </div>
                        ))}
                      </div>

                      {q.explanation && (
                        <div className="p-2 rounded-lg bg-[#1B263B] text-[11px] text-[#778DA9] border-l-2 border-[#00E5FF]">
                          <strong className="text-[#00E5FF]">Lời giải:</strong> {q.explanation}
                        </div>
                      )}
                    </div>
                  ))}
                </div>

                <div className="flex gap-2 pt-2">
                  <button
                    type="button"
                    onClick={() => setRepGeneratedPreview(null)}
                    className="flex-1 py-2.5 rounded-xl text-xs font-bold text-[#778DA9] bg-[#131F2E] hover:text-white"
                  >
                    ← Chọn Lại Nguồn Khác
                  </button>
                  <button
                    type="button"
                    onClick={handleConfirmSaveReputableExam}
                    className="flex-1 py-2.5 rounded-xl text-xs font-extrabold bg-[#06D6A0] text-[#0D1B2A] hover:bg-[#34d399] transition-all shadow-md shadow-[#06D6A0]/20 cursor-pointer flex items-center justify-center gap-1.5"
                  >
                    <Check size={15} />
                    <span>Lưu & Xuất Bản Cho Học Sinh</span>
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      )}

      {/* ============================================================ */}
      {/* MODAL 2: SOẠN & CHỈNH SỬA ĐỀ THI CHI TIẾT (FULL QUESTION CRUD) */}
      {/* ============================================================ */}
      {showManualQuizModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/85 backdrop-blur-xs">
          <div className="relative w-full max-w-2xl p-6 rounded-3xl bg-[#1E2D40] border border-[#9D4EDD]/40 shadow-2xl space-y-4 max-h-[92vh] overflow-y-auto">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="text-base font-bold text-white flex items-center gap-2">
                  <Edit3 size={18} className="text-[#00E5FF]" />
                  <span>{editingAssignment ? 'Chỉnh Sửa & Khắc Phục Sai Sót Đề Thi' : 'Soạn Đề Thi Mới'}</span>
                </h3>
                <p className="text-xs text-[#778DA9]">
                  Chỉnh sửa câu hỏi, đáp án đúng và lời giải chi tiết để đảm bảo đề thi chính xác 100%.
                </p>
              </div>
              <button
                onClick={() => setShowManualQuizModal(false)}
                className="text-[#778DA9] hover:text-white"
              >
                ✕
              </button>
            </div>

            <form onSubmit={handleSaveQuiz} className="space-y-4">
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                <div className="sm:col-span-2">
                  <label className="block text-xs font-semibold text-[#778DA9] mb-1">Tiêu Đề Đề Thi *</label>
                  <input
                    type="text"
                    required
                    value={quizTitle}
                    onChange={(e) => setQuizTitle(e.target.value)}
                    placeholder="e.g. Kiểm tra 15p Từ vựng & Ngữ pháp Unit 1"
                    className="w-full px-3 py-2 text-xs rounded-xl bg-[#131F2E] border border-[#27384E] text-white"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-[#778DA9] mb-1">Khối Lớp</label>
                  <select
                    value={quizGrade}
                    onChange={(e) => setQuizGrade(Number(e.target.value))}
                    className="w-full px-3 py-2 text-xs rounded-xl bg-[#131F2E] border border-[#27384E] text-white"
                  >
                    {[6, 7, 8, 9, 10, 11, 12].map((g) => (
                      <option key={g} value={g}>Lớp {g}</option>
                    ))}
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-[#778DA9] mb-1">Mô Tả & Hướng Dẫn</label>
                <input
                  type="text"
                  value={quizDesc}
                  onChange={(e) => setQuizDesc(e.target.value)}
                  placeholder="Ôn tập kiến thức trọng tâm cho học sinh..."
                  className="w-full px-3 py-2 text-xs rounded-xl bg-[#131F2E] border border-[#27384E] text-white"
                />
              </div>

              {/* Questions list with edit & delete controls */}
              <div className="space-y-4">
                <div className="flex items-center justify-between">
                  <h4 className="text-xs font-bold uppercase tracking-wider text-[#9D4EDD]">
                    Danh Sách Câu Hỏi ({quizQuestions.length})
                  </h4>
                  <button
                    type="button"
                    onClick={() => {
                      setQuizQuestions([
                        ...quizQuestions,
                        {
                          id: `q_${Date.now()}_${Math.random()}`,
                          question: '',
                          options: ['', '', '', ''],
                          correctIndex: 0,
                          explanation: '',
                          topic: 'Ngữ pháp'
                        }
                      ]);
                    }}
                    className="text-xs text-[#00E5FF] hover:underline font-bold flex items-center gap-1 cursor-pointer"
                  >
                    <Plus size={13} />
                    <span>Thêm Câu Hỏi Mới</span>
                  </button>
                </div>

                {quizQuestions.map((q, qIndex) => (
                  <div key={q.id || qIndex} className="p-4 rounded-2xl bg-[#131F2E] border border-[#27384E] space-y-3">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-2">
                        <span className="text-xs font-extrabold text-[#00E5FF]">Câu #{qIndex + 1}</span>
                        <input
                          type="text"
                          placeholder="Chuyên đề (vd: Câu điều kiện, Thì...)"
                          value={q.topic || ''}
                          onChange={(e) => {
                            const updated = [...quizQuestions];
                            updated[qIndex].topic = e.target.value;
                            setQuizQuestions(updated);
                          }}
                          className="px-2 py-0.5 text-[11px] rounded-md bg-[#1B263B] border border-[#27384E] text-[#FFD166] max-w-[140px]"
                        />
                      </div>

                      {quizQuestions.length > 1 && (
                        <button
                          type="button"
                          onClick={() => setQuizQuestions(quizQuestions.filter((_, i) => i !== qIndex))}
                          className="text-xs text-[#EF476F] hover:underline flex items-center gap-1 cursor-pointer"
                        >
                          <Trash2 size={12} />
                          <span>Xóa câu này</span>
                        </button>
                      )}
                    </div>

                    <textarea
                      rows={2}
                      required
                      placeholder="Nội dung câu hỏi (e.g. If she ______ earlier, she would have caught the train.)"
                      value={q.question}
                      onChange={(e) => {
                        const updated = [...quizQuestions];
                        updated[qIndex].question = e.target.value;
                        setQuizQuestions(updated);
                      }}
                      className="w-full px-3 py-2 text-xs rounded-xl bg-[#1B263B] border border-[#27384E] text-white"
                    />

                    {/* 4 Options with radio selector */}
                    <div className="space-y-1.5">
                      <span className="text-[10px] font-semibold text-[#778DA9] block">
                        Chọn nút tròn trước phương án để đặt làm đáp án ĐÚNG:
                      </span>
                      <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                        {q.options.map((opt, optIndex) => (
                          <div
                            key={optIndex}
                            className={`flex items-center gap-2 p-1.5 rounded-xl border transition-all ${
                              q.correctIndex === optIndex
                                ? 'bg-[#06D6A0]/15 border-[#06D6A0]'
                                : 'bg-[#1B263B] border-[#27384E]'
                            }`}
                          >
                            <input
                              type="radio"
                              name={`correct_${q.id || qIndex}`}
                              checked={q.correctIndex === optIndex}
                              onChange={() => {
                                const updated = [...quizQuestions];
                                updated[qIndex].correctIndex = optIndex;
                                setQuizQuestions(updated);
                              }}
                              className="accent-[#06D6A0] cursor-pointer"
                            />
                            <span className="text-xs font-bold text-[#778DA9]">
                              {String.fromCharCode(65 + optIndex)}:
                            </span>
                            <input
                              type="text"
                              required
                              placeholder={`Phương án ${String.fromCharCode(65 + optIndex)}`}
                              value={opt}
                              onChange={(e) => {
                                const updated = [...quizQuestions];
                                updated[qIndex].options[optIndex] = e.target.value;
                                setQuizQuestions(updated);
                              }}
                              className="w-full px-2 py-1 text-xs rounded-lg bg-transparent text-white focus:outline-hidden"
                            />
                          </div>
                        ))}
                      </div>
                    </div>

                    <div>
                      <label className="block text-[10px] font-semibold text-[#778DA9] mb-1">
                        Lời giải chi tiết (sửa lỗi để học sinh hiểu lý do đúng/sai):
                      </label>
                      <input
                        type="text"
                        placeholder="Giải thích vì sao đáp án này đúng và các đáp án khác sai..."
                        value={q.explanation || ''}
                        onChange={(e) => {
                          const updated = [...quizQuestions];
                          updated[qIndex].explanation = e.target.value;
                          setQuizQuestions(updated);
                        }}
                        className="w-full px-3 py-1.5 text-xs rounded-lg bg-[#1B263B] border border-[#27384E] text-[#ADB5BD]"
                      />
                    </div>
                  </div>
                ))}
              </div>

              <div className="flex gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setShowManualQuizModal(false)}
                  className="flex-1 py-2.5 rounded-xl text-xs font-bold text-[#778DA9] bg-[#131F2E]"
                >
                  Hủy
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2.5 rounded-xl text-xs font-extrabold bg-[#9D4EDD] text-white hover:bg-[#a855f7] cursor-pointer flex items-center justify-center gap-1.5 shadow-md shadow-[#9D4EDD]/20"
                >
                  <Save size={14} />
                  <span>{editingAssignment ? 'Lưu Thay Đổi Đề Thi' : 'Tạo Đề Thi Mới'}</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* ============================================================ */}
      {/* MODAL 3: XEM CHI TIẾT ĐỀ THI (PREVIEW MODAL)                  */}
      {/* ============================================================ */}
      {previewAssignment && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/85 backdrop-blur-xs">
          <div className="relative w-full max-w-xl p-6 rounded-3xl bg-[#1E2D40] border border-[#00E5FF]/40 shadow-2xl space-y-4 max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between">
              <div>
                <span className="text-[10px] font-bold text-[#00E5FF] uppercase">
                  Khối Lớp {previewAssignment.grade} • {previewAssignment.questions.length} Câu
                </span>
                <h3 className="text-base font-extrabold text-white">{previewAssignment.title}</h3>
              </div>
              <button
                onClick={() => setPreviewAssignment(null)}
                className="text-[#778DA9] hover:text-white"
              >
                ✕
              </button>
            </div>

            <p className="text-xs text-[#ADB5BD]">{previewAssignment.description}</p>

            <div className="space-y-3 max-h-[55vh] overflow-y-auto pr-1">
              {previewAssignment.questions.map((q, idx) => (
                <div key={q.id || idx} className="p-3.5 rounded-2xl bg-[#131F2E] border border-[#27384E] space-y-2 text-xs">
                  <div className="flex items-center justify-between font-bold text-[#00E5FF]">
                    <span>Câu #{idx + 1}</span>
                    {q.topic && <span className="text-[10px] text-[#FFD166]">{q.topic}</span>}
                  </div>
                  <p className="text-white font-medium">{q.question}</p>

                  <div className="grid grid-cols-2 gap-1.5 text-[11px]">
                    {q.options.map((opt, optIdx) => (
                      <div
                        key={optIdx}
                        className={`p-1.5 rounded-lg border ${
                          optIdx === q.correctIndex
                            ? 'bg-[#06D6A0]/20 border-[#06D6A0] text-[#06D6A0] font-bold'
                            : 'bg-[#1B263B] border-[#27384E] text-[#778DA9]'
                        }`}
                      >
                        {String.fromCharCode(65 + optIdx)}. {opt}
                      </div>
                    ))}
                  </div>

                  {q.explanation && (
                    <div className="p-2 rounded-lg bg-[#1B263B] text-[11px] text-[#ADB5BD] border-l-2 border-[#00E5FF]">
                      <strong className="text-[#00E5FF]">Lời giải:</strong> {q.explanation}
                    </div>
                  )}
                </div>
              ))}
            </div>

            <div className="flex gap-2 pt-2">
              <button
                onClick={() => {
                  const assign = previewAssignment;
                  setPreviewAssignment(null);
                  handleOpenEditAssignment(assign);
                }}
                className="flex-1 py-2.5 rounded-xl text-xs font-bold bg-[#00E5FF] text-[#0D1B2A] hover:bg-[#38bdf8] flex items-center justify-center gap-1.5 cursor-pointer"
              >
                <Edit3 size={14} />
                <span>Chỉnh Sửa Đề Này</span>
              </button>
              <button
                onClick={() => setPreviewAssignment(null)}
                className="py-2.5 px-4 rounded-xl text-xs font-bold text-[#778DA9] bg-[#131F2E] hover:text-white"
              >
                Đóng
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
