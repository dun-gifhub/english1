export type UserRole = 'STUDENT' | 'TEACHER';

export interface UserProfile {
  uid: string;
  email: string;
  displayName: string;
  customClassName: string; // e.g. "10A1", "11A2"
  baseGrade: number; // 6..12
  registeredAcademicYear: number; // e.g. 2026
  friendCode: string;
  role: UserRole;
  selectedGrade: number; // 6..12
  level: number;
  xp: number;
  highestScore: number;
  monthlyScore: number;
  lastScoreMonthKey: string; // e.g. "2026-10"
  streakDays: number;
  avatarColor: string; // Hex or CSS color, default "#00E5FF"
  status: string;
}

export interface WordItem {
  id: string;
  word: string;
  phonetic: string;
  meaningVi: string;
  exampleEn: string;
  exampleVi: string;
  grade: number;
  unitNumber: number;
  distractorsVi: string[];
  partOfSpeech?: string;
}

export interface GrammarLesson {
  id: string;
  grade: number;
  title: string;
  formula: string;
  explanationVi: string;
  exampleEn: string;
  exampleVi: string;
  usageNotes: string;
}

export interface UnitTopic {
  id: string;
  grade: number;
  unitNumber: number;
  title: string;
  description: string;
  difficulty: string; // "Cơ bản", "Trung cấp", "Nâng cao", "Nâng cao GV"
  words: WordItem[];
  grammarLesson?: GrammarLesson | null;
  bestScore: number;
  isCompleted: boolean;
}

export interface Subject {
  id: string;
  grade: number;
  title: string;
  subtitle: string;
  iconCategory: string;
  units: UnitTopic[];
}

export interface CustomQuestion {
  id: string;
  question: string;
  options: string[];
  correctIndex: number;
  explanation: string;
  topic?: string;
  sourceName?: string;
  grade?: number;
}

export interface TeacherAssignment {
  id: string;
  teacherUid: string;
  teacherName: string;
  grade: number;
  title: string;
  description: string;
  questions: CustomQuestion[];
  createdAt: number;
  sourceType?: ExamSourceType;
}

export interface DictionaryEntry {
  wordEn: string;
  phonetic: string;
  partOfSpeech: string;
  meaningVi: string;
  exampleEn: string;
  exampleVi: string;
  synonyms: string[];
  antonyms?: string[];
  definitionEn?: string;
}

export interface Friend {
  uid: string;
  friendCode: string;
  displayName: string;
  status: string;
  isOnline: boolean;
  currentActivity: string;
  avatarColor: string;
  isSpeaking?: boolean;
}

export interface CallRoom {
  roomId: string;
  roomCode: string;
  title: string;
  hostName: string;
  participants: Friend[];
  currentWordTopic: string;
  isMicOn: boolean;
  isScreenSharing?: boolean;
  isCameraOn?: boolean;
}

export interface LeaderboardEntry {
  rank: number;
  name: string;
  className: string;
  score: number;
  monthlyScore: number;
  level: number;
  grade: number;
  badge: string;
  avatarColor: string;
}

export type GameDifficulty = 'EASY' | 'MEDIUM' | 'HARD';

export interface DifficultyConfig {
  key: GameDifficulty;
  label: string;
  fallDurationMs: number;
  description: string;
}

export type ExamSourceType =
  | 'THPT_QUOC_GIA'
  | 'HOC_SINH_GIOI'
  | 'CHUYEN_CAP_3'
  | 'CAMBRIDGE_DGNL'
  | 'SGK_GLOBAL_SUCCESS'
  | 'IELTS_FOUNDATION';

export interface ExamSourceMeta {
  type: ExamSourceType;
  sourceName: string;
  badge: string;
  description: string;
  yearCitation?: string;
}
