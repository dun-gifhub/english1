import React, { createContext, useContext, useState, useEffect, ReactNode, useMemo } from 'react';
import {
  UserProfile,
  UserRole,
  Subject,
  GrammarLesson,
  TeacherAssignment,
  WordItem,
  Friend,
  CallRoom,
  LeaderboardEntry,
  UnitTopic
} from '../types';
import { BASE_SUBJECTS_BY_GRADE, GRAMMAR_DATABASE, DEFAULT_ASSIGNMENTS } from '../services/curriculumDatabase';
import { db, auth, logoutFirebase } from '../services/firebase';
import { apiService } from '../services/apiService';
import {
  collection,
  doc,
  setDoc,
  deleteDoc,
  onSnapshot,
  serverTimestamp
} from 'firebase/firestore';
import { onAuthStateChanged } from 'firebase/auth';

export function getCurrentMonthKey(): string {
  const d = new Date();
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  return `${y}-${m}`;
}

export function calculateCurrentGrade(user: UserProfile): number {
  const now = new Date();
  const currentYear = now.getFullYear();
  const currentMonth = now.getMonth() + 1; // 1..12
  const currentAcademicYear = currentMonth >= 9 ? currentYear : currentYear - 1;
  const yearsPassed = Math.max(0, currentAcademicYear - (user.registeredAcademicYear || 2026));
  return Math.min(13, (user.baseGrade || 10) + yearsPassed);
}

export function getDisplayClassName(user: UserProfile): string {
  const grade = calculateCurrentGrade(user);
  if (grade > 12) {
    return `Cựu học sinh Lương Phú (K${user.registeredAcademicYear || 2026})`;
  }
  const trimmed = (user.customClassName || '10A1').trim();
  if (trimmed) {
    const match = trimmed.match(/^\d+/);
    if (match) {
      return trimmed.replace(match[0], String(grade));
    }
    return `Lớp ${grade} (${trimmed})`;
  }
  return `Lớp ${grade}`;
}

export function getCurrentSchoolYearString(): string {
  const now = new Date();
  const currentYear = now.getFullYear();
  const currentMonth = now.getMonth() + 1;
  const startYear = currentMonth >= 9 ? currentYear : currentYear - 1;
  return `${startYear} - ${startYear + 1}`;
}

interface AppContextType {
  currentUser: UserProfile | null;
  selectedGrade: number;
  setSelectedGrade: (g: number) => void;
  subjects: Subject[];
  grammarLessons: GrammarLesson[];
  assignments: TeacherAssignment[];
  customWords: WordItem[];
  customGrammar: GrammarLesson[];
  friends: Friend[];
  activeRoom: CallRoom | null;
  friendMessage: string | null;
  monthlyLeaderboard: LeaderboardEntry[];
  allTimeLeaderboard: LeaderboardEntry[];
  soundEnabled: boolean;
  setSoundEnabled: (v: boolean) => void;
  setCurrentUser: (u: UserProfile | null) => void;
  neonStatus: any;
  refreshNeonStatus: () => Promise<void>;
  registerOrUpdateAccount: (name: string, className: string, baseGrade: number, registeredYear?: number, role?: UserRole) => void;
  setRole: (r: UserRole) => void;
  addXpAndScore: (pointsEarned: number) => void;
  signOut: () => Promise<void>;
  addTeacherAssignment: (assignment: TeacherAssignment) => void;
  updateTeacherAssignment: (assignment: TeacherAssignment) => void;
  deleteTeacherAssignment: (id: string) => void;
  addCustomWord: (word: Omit<WordItem, 'id'>) => void;
  updateCustomWord: (word: WordItem) => void;
  deleteCustomWord: (id: string) => void;
  addCustomGrammar: (lesson: Omit<GrammarLesson, 'id'>) => void;
  updateCustomGrammar: (lesson: GrammarLesson) => void;
  deleteCustomGrammar: (id: string) => void;
  addFriendByCode: (code: string) => boolean;
  clearFriendMessage: () => void;
  createRoom: (hostName: string) => CallRoom;
  joinRoom: (code: string, userName: string) => boolean;
  leaveRoom: () => void;
  toggleMicInRoom: () => void;
  toggleScreenSharingInRoom: (active?: boolean) => void;
}

const AppContext = createContext<AppContextType | undefined>(undefined);

const LOCAL_STORAGE_USER_KEY = 'tap_hunter_user_profile_v2';
const LOCAL_STORAGE_ASSIGNMENTS_KEY = 'tap_hunter_assignments_v2';
const LOCAL_STORAGE_CUSTOM_WORDS_KEY = 'tap_hunter_custom_words_v2';
const LOCAL_STORAGE_CUSTOM_GRAMMAR_KEY = 'tap_hunter_custom_grammar_v2';
const LOCAL_STORAGE_FRIENDS_KEY = 'tap_hunter_friends_v2';

export const AppProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  // Load initial user from localStorage
  const [currentUser, setCurrentUser] = useState<UserProfile | null>(() => {
    try {
      const saved = localStorage.getItem(LOCAL_STORAGE_USER_KEY);
      if (saved) return JSON.parse(saved);
    } catch {
      // Ignore
    }
    return null;
  });

  const [selectedGrade, setSelectedGradeState] = useState<number>(() => {
    return currentUser ? currentUser.selectedGrade : 10;
  });

  const [assignments, setAssignments] = useState<TeacherAssignment[]>(() => {
    try {
      const saved = localStorage.getItem(LOCAL_STORAGE_ASSIGNMENTS_KEY);
      if (saved) {
        const parsed = JSON.parse(saved);
        return [...parsed, ...DEFAULT_ASSIGNMENTS].reduce((acc: TeacherAssignment[], curr: TeacherAssignment) => {
          if (!acc.some(item => item.id === curr.id)) acc.push(curr);
          return acc;
        }, []);
      }
    } catch {}
    return DEFAULT_ASSIGNMENTS;
  });

  const [customWords, setCustomWords] = useState<WordItem[]>(() => {
    try {
      const saved = localStorage.getItem(LOCAL_STORAGE_CUSTOM_WORDS_KEY);
      if (saved) return JSON.parse(saved);
    } catch {}
    return [];
  });

  const [customGrammar, setCustomGrammar] = useState<GrammarLesson[]>(() => {
    try {
      const saved = localStorage.getItem(LOCAL_STORAGE_CUSTOM_GRAMMAR_KEY);
      if (saved) return JSON.parse(saved);
    } catch {}
    return [];
  });

  const [friends, setFriends] = useState<Friend[]>(() => {
    try {
      const saved = localStorage.getItem(LOCAL_STORAGE_FRIENDS_KEY);
      if (saved) return JSON.parse(saved);
    } catch {}
    return [];
  });

  const [registeredUsers, setRegisteredUsers] = useState<UserProfile[]>([]);
  const [activeRoom, setActiveRoom] = useState<CallRoom | null>(null);
  const [friendMessage, setFriendMessage] = useState<string | null>(null);
  const [soundEnabled, setSoundEnabled] = useState<boolean>(true);
  const [neonStatus, setNeonStatus] = useState<any>({
    isNeonConfigured: false,
    isConnected: false,
    isNeonUrl: false,
    message: 'Đang kiểm tra kết nối...'
  });

  // Sync state to local storage
  useEffect(() => {
    if (currentUser) {
      localStorage.setItem(LOCAL_STORAGE_USER_KEY, JSON.stringify(currentUser));
      if (currentUser.selectedGrade !== selectedGrade) {
        setSelectedGradeState(currentUser.selectedGrade);
      }
    } else {
      localStorage.removeItem(LOCAL_STORAGE_USER_KEY);
    }
  }, [currentUser]);

  useEffect(() => {
    localStorage.setItem(LOCAL_STORAGE_ASSIGNMENTS_KEY, JSON.stringify(assignments));
  }, [assignments]);

  useEffect(() => {
    localStorage.setItem(LOCAL_STORAGE_CUSTOM_WORDS_KEY, JSON.stringify(customWords));
  }, [customWords]);

  useEffect(() => {
    localStorage.setItem(LOCAL_STORAGE_CUSTOM_GRAMMAR_KEY, JSON.stringify(customGrammar));
  }, [customGrammar]);

  useEffect(() => {
    localStorage.setItem(LOCAL_STORAGE_FRIENDS_KEY, JSON.stringify(friends));
  }, [friends]);

  const refreshNeonStatus = async () => {
    try {
      const status = await apiService.getNeonStatus();
      setNeonStatus(status);
    } catch {
      // ignore
    }
  };

  useEffect(() => {
    refreshNeonStatus();

    // Sync remote data from server/Neon if available
    apiService.getAssignments().then((remotes) => {
      if (remotes && remotes.length > 0) {
        setAssignments((prev) => {
          const merged = [...prev];
          remotes.forEach((r) => {
            if (!merged.some((m) => m.id === r.id)) merged.push(r);
          });
          return merged;
        });
      }
    }).catch(() => {});

    apiService.getCustomWords().then((remotes) => {
      if (remotes && remotes.length > 0) {
        setCustomWords((prev) => {
          const merged = [...prev];
          remotes.forEach((r) => {
            if (!merged.some((m) => m.id === r.id)) merged.push(r);
          });
          return merged;
        });
      }
    }).catch(() => {});

    apiService.getCustomGrammar().then((remotes) => {
      if (remotes && remotes.length > 0) {
        setCustomGrammar((prev) => {
          const merged = [...prev];
          remotes.forEach((r) => {
            if (!merged.some((m) => m.id === r.id)) merged.push(r);
          });
          return merged;
        });
      }
    }).catch(() => {});
  }, []);

  // Firebase auth & firestore listeners
  useEffect(() => {
    const firestore = db;
    if (!auth || !firestore) return;

    const unsubAuth = onAuthStateChanged(auth, async (fbUser) => {
      if (fbUser) {
        const uid = fbUser.uid;
        const userDocRef = doc(firestore, 'users', uid);

        const unsubUser = onSnapshot(userDocRef, (snap) => {
          if (snap.exists()) {
            const data = snap.data();
            const currentMonth = getCurrentMonthKey();
            const activeMonthly = data.lastScoreMonthKey === currentMonth ? (data.monthlyScore || 0) : 0;
            const updated: UserProfile = {
              uid,
              email: fbUser.email || `${uid}@luongphu.edu.vn`,
              displayName: data.displayName || fbUser.displayName || "Học Sinh Lương Phú",
              customClassName: data.customClassName || "10A1",
              baseGrade: data.baseGrade || 10,
              registeredAcademicYear: data.registeredAcademicYear || 2026,
              friendCode: data.friendCode || `LP-${uid.slice(-4).toUpperCase()}`,
              role: (data.role as UserRole) || 'STUDENT',
              selectedGrade: data.selectedGrade || 10,
              level: data.level || 1,
              xp: data.xp || 0,
              highestScore: data.highestScore || 0,
              monthlyScore: activeMonthly,
              lastScoreMonthKey: currentMonth,
              streakDays: data.streakDays || 1,
              avatarColor: data.avatarColor || "#00E5FF",
              status: data.status || "Sẵn sàng săn từ vựng THPT Lương Phú!"
            };
            setCurrentUser(updated);
          } else {
            // Document does not exist yet; initialize
            const currentMonth = getCurrentMonthKey();
            const friendCode = `LP-${Math.floor(1000 + Math.random() * 9000)}`;
            const initUser: UserProfile = {
              uid,
              email: fbUser.email || `${uid}@luongphu.edu.vn`,
              displayName: fbUser.displayName || "Học Sinh Lương Phú",
              customClassName: "10A1",
              baseGrade: 10,
              registeredAcademicYear: 2026,
              friendCode,
              role: 'STUDENT',
              selectedGrade: 10,
              level: 1,
              xp: 0,
              highestScore: 0,
              monthlyScore: 0,
              lastScoreMonthKey: currentMonth,
              streakDays: 1,
              avatarColor: "#00E5FF",
              status: "Sẵn sàng săn từ vựng THPT Lương Phú!"
            };
            setDoc(userDocRef, {
              ...initUser,
              userId: uid,
              updatedAt: serverTimestamp()
            }).catch(console.error);
            setCurrentUser(initUser);
          }
        });

        return () => unsubUser();
      }
    });

    // Assignments Listener
    const assignCol = collection(firestore, 'assignments');
    const unsubAssign = onSnapshot(assignCol, (snap) => {
      const docs = snap.docs.map(d => ({ ...(d.data() as TeacherAssignment), id: d.id }));
      if (docs.length > 0) {
        setAssignments(prev => {
          const combined = [...docs, ...prev];
          return combined.filter((v, i, a) => a.findIndex(t => t.id === v.id) === i);
        });
      }
    }, (err) => console.warn("Firestore assignments snapshot:", err.message));

    // Custom Words Listener
    const wordsCol = collection(firestore, 'customWords');
    const unsubWords = onSnapshot(wordsCol, (snap) => {
      const docs = snap.docs.map(d => ({ ...(d.data() as WordItem), id: d.id }));
      if (docs.length > 0) {
        setCustomWords(docs);
      }
    }, (err) => console.warn("Firestore words snapshot:", err.message));

    // Custom Grammar Listener
    const grammarCol = collection(firestore, 'customGrammar');
    const unsubGrammar = onSnapshot(grammarCol, (snap) => {
      const docs = snap.docs.map(d => ({ ...(d.data() as GrammarLesson), id: d.id }));
      if (docs.length > 0) {
        setCustomGrammar(docs);
      }
    }, (err) => console.warn("Firestore grammar snapshot:", err.message));

    // Users Leaderboard Listener
    const usersCol = collection(firestore, 'users');
    const unsubUsers = onSnapshot(usersCol, (snap) => {
      const currentMonth = getCurrentMonthKey();
      const docs: UserProfile[] = snap.docs.map(d => {
        const data = d.data();
        const activeMonthly = data.lastScoreMonthKey === currentMonth ? (data.monthlyScore || 0) : 0;
        return {
          uid: d.id,
          email: data.email || "",
          displayName: data.displayName || "Học sinh THPT Lương Phú",
          customClassName: data.customClassName || "10A1",
          baseGrade: data.baseGrade || 10,
          registeredAcademicYear: data.registeredAcademicYear || 2026,
          friendCode: data.friendCode || "LP-0000",
          role: (data.role as UserRole) || 'STUDENT',
          selectedGrade: data.selectedGrade || 10,
          level: data.level || 1,
          xp: data.xp || 0,
          highestScore: data.highestScore || 0,
          monthlyScore: activeMonthly,
          lastScoreMonthKey: currentMonth,
          streakDays: data.streakDays || 1,
          avatarColor: data.avatarColor || "#00E5FF",
          status: data.status || ""
        };
      });
      if (docs.length > 0) {
        setRegisteredUsers(docs);
      }
    }, (err) => console.warn("Firestore users snapshot:", err.message));

    return () => {
      unsubAuth();
      unsubAssign();
      unsubWords();
      unsubGrammar();
      unsubUsers();
    };
  }, []);

  const setSelectedGrade = (g: number) => {
    const clamped = Math.max(6, Math.min(12, g));
    setSelectedGradeState(clamped);
    if (currentUser) {
      const updated = { ...currentUser, selectedGrade: clamped };
      setCurrentUser(updated);
      const firestore = db;
      if (firestore && currentUser.uid) {
        setDoc(doc(firestore, 'users', currentUser.uid), { selectedGrade: clamped, updatedAt: serverTimestamp() }, { merge: true }).catch(() => {});
      }
    }
  };

  const registerOrUpdateAccount = (
    name: string,
    className: string,
    baseGrade: number,
    registeredYear: number = 2026,
    role: UserRole = 'STUDENT'
  ) => {
    const clampedGrade = Math.max(6, Math.min(12, baseGrade));
    const validClass = className.trim() || `${clampedGrade}A1`;
    const uid = currentUser?.uid || `local_${Date.now()}`;
    const currentMonth = getCurrentMonthKey();

    const newProfile: UserProfile = {
      uid,
      email: currentUser?.email || `${uid}@luongphu.edu.vn`,
      displayName: name.trim() || "Học Sinh Lương Phú",
      customClassName: validClass,
      baseGrade: clampedGrade,
      registeredAcademicYear: registeredYear,
      friendCode: currentUser?.friendCode || `LP-${Math.floor(1000 + Math.random() * 9000)}`,
      role,
      selectedGrade: clampedGrade,
      level: currentUser?.level || 1,
      xp: currentUser?.xp || 0,
      highestScore: currentUser?.highestScore || 0,
      monthlyScore: currentUser?.lastScoreMonthKey === currentMonth ? currentUser.monthlyScore : 0,
      lastScoreMonthKey: currentMonth,
      streakDays: currentUser?.streakDays || 1,
      avatarColor: currentUser?.avatarColor || "#00E5FF",
      status: "Học sinh Trường THPT Lương Phú"
    };

    setCurrentUser(newProfile);
    setSelectedGradeState(clampedGrade);

    const firestore = db;
    if (firestore && uid) {
      setDoc(doc(firestore, 'users', uid), {
        ...newProfile,
        userId: uid,
        updatedAt: serverTimestamp()
      }, { merge: true }).catch(err => console.warn("Firestore sync queued locally:", err.message));
    }
  };

  const setRole = (newRole: UserRole) => {
    if (!currentUser) return;
    const updated = { ...currentUser, role: newRole };
    setCurrentUser(updated);
    const firestore = db;
    if (firestore && currentUser.uid) {
      setDoc(doc(firestore, 'users', currentUser.uid), { role: newRole, updatedAt: serverTimestamp() }, { merge: true }).catch(() => {});
    }
  };

  const addXpAndScore = (pointsEarned: number) => {
    if (!currentUser) return;
    const currentMonth = getCurrentMonthKey();
    const newMonthlyScore = currentUser.lastScoreMonthKey === currentMonth
      ? currentUser.monthlyScore + pointsEarned
      : pointsEarned;
    const newXp = currentUser.xp + pointsEarned;
    const newLevel = 1 + Math.floor(newXp / 500);
    const newHighScore = Math.max(currentUser.highestScore, pointsEarned);

    const updated: UserProfile = {
      ...currentUser,
      xp: newXp,
      level: newLevel,
      highestScore: newHighScore,
      monthlyScore: newMonthlyScore,
      lastScoreMonthKey: currentMonth
    };

    setCurrentUser(updated);

    if (currentUser.uid) {
      apiService.updateProgress(currentUser.uid, pointsEarned, pointsEarned).catch(() => {});
    }

    const firestore = db;
    if (firestore && currentUser.uid) {
      setDoc(doc(firestore, 'users', currentUser.uid), {
        xp: newXp,
        level: newLevel,
        highestScore: newHighScore,
        monthlyScore: newMonthlyScore,
        lastScoreMonthKey: currentMonth,
        updatedAt: serverTimestamp()
      }, { merge: true }).catch(() => {});
    }
  };

  const signOut = async () => {
    setCurrentUser(null);
    localStorage.removeItem(LOCAL_STORAGE_USER_KEY);
    await logoutFirebase();
  };

  const addTeacherAssignment = (assignment: TeacherAssignment) => {
    setAssignments(prev => [assignment, ...prev.filter(a => a.id !== assignment.id)]);
    apiService.saveAssignment(assignment).catch(() => {});
    const firestore = db;
    if (firestore) {
      setDoc(doc(firestore, 'assignments', assignment.id), {
        ...assignment,
        createdAt: serverTimestamp()
      }).catch(err => console.warn("Sync assignment failed:", err.message));
    }
  };

  const updateTeacherAssignment = (assignment: TeacherAssignment) => {
    setAssignments(prev => prev.map(a => (a.id === assignment.id ? assignment : a)));
    apiService.saveAssignment(assignment).catch(() => {});
    const firestore = db;
    if (firestore) {
      setDoc(doc(firestore, 'assignments', assignment.id), {
        ...assignment,
        updatedAt: serverTimestamp()
      }, { merge: true }).catch(() => {});
    }
  };

  const deleteTeacherAssignment = (id: string) => {
    setAssignments(prev => prev.filter(a => a.id !== id));
    apiService.deleteAssignment(id).catch(() => {});
    const firestore = db;
    if (firestore) {
      deleteDoc(doc(firestore, 'assignments', id)).catch(() => {});
    }
  };

  const addCustomWord = (wordData: Omit<WordItem, 'id'>) => {
    const id = `word_${Date.now()}`;
    const newWord: WordItem = { id, ...wordData };
    setCustomWords(prev => {
      const updated = [newWord, ...prev];
      try { localStorage.setItem(LOCAL_STORAGE_CUSTOM_WORDS_KEY, JSON.stringify(updated)); } catch {}
      return updated;
    });
    apiService.saveCustomWord(newWord).catch(() => {});
    const firestore = db;
    if (firestore) {
      setDoc(doc(firestore, 'customWords', id), {
        ...newWord,
        teacherUid: currentUser?.uid || 'teacher_local',
        createdAt: serverTimestamp()
      }).catch(() => {});
    }
  };

  const updateCustomWord = (word: WordItem) => {
    setCustomWords(prev => {
      const updated = prev.map(w => (w.id === word.id ? word : w));
      try { localStorage.setItem(LOCAL_STORAGE_CUSTOM_WORDS_KEY, JSON.stringify(updated)); } catch {}
      return updated;
    });
    apiService.saveCustomWord(word).catch(() => {});
    const firestore = db;
    if (firestore) {
      setDoc(doc(firestore, 'customWords', word.id), {
        ...word,
        teacherUid: currentUser?.uid || 'teacher_local',
        updatedAt: serverTimestamp()
      }, { merge: true }).catch(() => {});
    }
  };

  const deleteCustomWord = (id: string) => {
    setCustomWords(prev => {
      const updated = prev.filter(w => w.id !== id);
      try { localStorage.setItem(LOCAL_STORAGE_CUSTOM_WORDS_KEY, JSON.stringify(updated)); } catch {}
      return updated;
    });
    apiService.deleteCustomWord(id).catch(() => {});
    const firestore = db;
    if (firestore) {
      deleteDoc(doc(firestore, 'customWords', id)).catch(() => {});
    }
  };

  const addCustomGrammar = (lessonData: Omit<GrammarLesson, 'id'>) => {
    const id = `grammar_${Date.now()}`;
    const newLesson: GrammarLesson = { id, ...lessonData };
    setCustomGrammar(prev => {
      const updated = [newLesson, ...prev];
      try { localStorage.setItem(LOCAL_STORAGE_CUSTOM_GRAMMAR_KEY, JSON.stringify(updated)); } catch {}
      return updated;
    });
    apiService.saveCustomGrammar(newLesson).catch(() => {});
    const firestore = db;
    if (firestore) {
      setDoc(doc(firestore, 'customGrammar', id), {
        ...newLesson,
        teacherUid: currentUser?.uid || 'teacher_local',
        createdAt: serverTimestamp()
      }).catch(() => {});
    }
  };

  const updateCustomGrammar = (lesson: GrammarLesson) => {
    setCustomGrammar(prev => {
      const updated = prev.map(g => (g.id === lesson.id ? lesson : g));
      try { localStorage.setItem(LOCAL_STORAGE_CUSTOM_GRAMMAR_KEY, JSON.stringify(updated)); } catch {}
      return updated;
    });
    apiService.saveCustomGrammar(lesson).catch(() => {});
    const firestore = db;
    if (firestore) {
      setDoc(doc(firestore, 'customGrammar', lesson.id), {
        ...lesson,
        teacherUid: currentUser?.uid || 'teacher_local',
        updatedAt: serverTimestamp()
      }, { merge: true }).catch(() => {});
    }
  };

  const deleteCustomGrammar = (id: string) => {
    setCustomGrammar(prev => {
      const updated = prev.filter(g => g.id !== id);
      try { localStorage.setItem(LOCAL_STORAGE_CUSTOM_GRAMMAR_KEY, JSON.stringify(updated)); } catch {}
      return updated;
    });
    apiService.deleteCustomGrammar(id).catch(() => {});
    const firestore = db;
    if (firestore) {
      deleteDoc(doc(firestore, 'customGrammar', id)).catch(() => {});
    }
  };

  const addFriendByCode = (code: string): boolean => {
    const trimmed = code.trim().toUpperCase();
    if (trimmed.length < 6) {
      setFriendMessage("Mã kết bạn không hợp lệ (Ví dụ: LP-1234)");
      return false;
    }
    if (friends.some(f => f.friendCode.toUpperCase() === trimmed)) {
      setFriendMessage("Bạn đã có người bạn này trong danh sách!");
      return false;
    }

    const newFriend: Friend = {
      uid: `u_${Date.now()}`,
      friendCode: trimmed,
      displayName: `Bạn học ${trimmed}`,
      status: "Học sinh THPT Lương Phú",
      isOnline: true,
      currentActivity: "Vừa kết bạn",
      avatarColor: "#FF70A6"
    };

    setFriends(prev => [newFriend, ...prev]);
    setFriendMessage(`Đã kết bạn thành công với ${trimmed}!`);
    return true;
  };

  const clearFriendMessage = () => {
    setFriendMessage(null);
  };

  const createRoom = (hostName: string): CallRoom => {
    const roomCode = String(Math.floor(100000 + Math.random() * 900000));
    const room: CallRoom = {
      roomId: `room_${Date.now()}`,
      roomCode,
      title: `Phòng Học & Luyện Phản Xạ #${roomCode}`,
      hostName,
      participants: [
        {
          uid: "host",
          friendCode: currentUser?.friendCode || "YOU",
          displayName: hostName,
          status: "Trưởng phòng",
          isOnline: true,
          currentActivity: "Đang chủ trì",
          avatarColor: "#00E5FF"
        }
      ],
      currentWordTopic: `Lớp ${selectedGrade} - Unit 1`,
      isMicOn: true
    };
    setActiveRoom(room);
    return room;
  };

  const joinRoom = (code: string, userName: string): boolean => {
    const trimmed = code.trim();
    if (trimmed.length < 4) {
      setFriendMessage("Mã phòng phải có từ 4 đến 6 chữ số!");
      return false;
    }
    const room: CallRoom = {
      roomId: `room_${trimmed}`,
      roomCode: trimmed,
      title: `Phòng Học Nhóm HSG #${trimmed}`,
      hostName: `Chủ phòng #${trimmed}`,
      participants: [
        {
          uid: "me",
          friendCode: currentUser?.friendCode || "YOU",
          displayName: userName,
          status: "Thành viên",
          isOnline: true,
          currentActivity: "Vừa tham gia",
          avatarColor: "#00E5FF"
        }
      ],
      currentWordTopic: `Lớp ${selectedGrade} - Unit 1`,
      isMicOn: true
    };
    setActiveRoom(room);
    return true;
  };

  const leaveRoom = () => {
    setActiveRoom(null);
  };

  const toggleMicInRoom = () => {
    if (activeRoom) {
      setActiveRoom({ ...activeRoom, isMicOn: !activeRoom.isMicOn });
    }
  };

  const toggleScreenSharingInRoom = (active?: boolean) => {
    if (activeRoom) {
      const nextState = active !== undefined ? active : !activeRoom.isScreenSharing;
      setActiveRoom({ ...activeRoom, isScreenSharing: nextState });
    }
  };

  // Derive subjects dynamically for selectedGrade
  const subjects = useMemo(() => {
    const baseSubjects = BASE_SUBJECTS_BY_GRADE[selectedGrade] || BASE_SUBJECTS_BY_GRADE[10];
    const customForGrade = customWords.filter(w => w.grade === selectedGrade);
    const customGrammarForGrade = customGrammar.filter(g => g.grade === selectedGrade);

    if (customForGrade.length === 0 && customGrammarForGrade.length === 0) {
      return baseSubjects;
    }

    const baseUnits = baseSubjects.flatMap(s => s.units);
    const updatedUnits: UnitTopic[] = baseUnits.map(unit => {
      const wordsForUnit = customForGrade.filter(w => w.unitNumber === unit.unitNumber);
      if (wordsForUnit.length > 0) {
        const combined = [...unit.words, ...wordsForUnit];
        const unique = combined.filter((w, i, a) => a.findIndex(t => t.word.toLowerCase() === w.word.toLowerCase()) === i);
        return { ...unit, words: unique };
      }
      return unit;
    });

    const extraWords = customForGrade.filter(w => !baseUnits.some(u => u.unitNumber === w.unitNumber));
    if (extraWords.length > 0 || customGrammarForGrade.length > 0) {
      updatedUnits.unshift({
        id: `unit_teacher_custom_${selectedGrade}`,
        grade: selectedGrade,
        unitNumber: 0,
        title: "Unit Bổ Sung (Thầy Cô)",
        description: "Từ vựng & chuyên đề giáo viên vừa cập nhật trực tuyến",
        difficulty: "Nâng cao GV",
        words: extraWords.length > 0 ? extraWords : customForGrade,
        grammarLesson: customGrammarForGrade[0] || null,
        bestScore: 0,
        isCompleted: false
      });
    }

    return [
      {
        id: `sub_grade_${selectedGrade}`,
        grade: selectedGrade,
        title: `Tiếng Anh Lớp ${selectedGrade}`,
        subtitle: "Chương trình GDPT mới chuẩn Bộ GD&ĐT",
        iconCategory: "school",
        units: updatedUnits
      }
    ];
  }, [selectedGrade, customWords, customGrammar]);

  // Derive grammar lessons
  const grammarLessons = useMemo(() => {
    const base = GRAMMAR_DATABASE[selectedGrade] || [];
    const custom = customGrammar.filter(g => g.grade === selectedGrade);
    return [...custom, ...base];
  }, [selectedGrade, customGrammar]);

  // Derive Leaderboards
  const { monthlyLeaderboard, allTimeLeaderboard } = useMemo(() => {
    const allUsers: UserProfile[] = [...registeredUsers];
    if (currentUser) {
      const idx = allUsers.findIndex(u => u.uid === currentUser.uid);
      if (idx >= 0) allUsers[idx] = currentUser;
      else allUsers.push(currentUser);
    }

    // Filter only real registered students (NO demo accounts)
    const realStudents = allUsers.filter(u => u.role === 'STUDENT' && (u.highestScore > 0 || u.monthlyScore > 0 || u.xp > 0 || u.uid === currentUser?.uid));

    const sortMonthly = [...realStudents].sort((a, b) => b.monthlyScore - a.monthlyScore || b.highestScore - a.highestScore);
    const sortAllTime = [...realStudents].sort((a, b) => b.highestScore - a.highestScore || b.xp - a.xp);

    const mapEntries = (list: UserProfile[], isMonthly: boolean): LeaderboardEntry[] => {
      return list.map((user, index) => {
        const badge = index === 0 ? "Quán Quân" : index === 1 ? "Á Quân" : index === 2 ? "Hạng Ba" : user.level >= 10 ? "Thần Tốc" : user.level >= 5 ? "Chiến Binh" : "Tân Binh";
        return {
          rank: index + 1,
          name: user.displayName || "Học sinh THPT Lương Phú",
          className: getDisplayClassName(user),
          score: user.highestScore,
          monthlyScore: user.monthlyScore,
          level: user.level,
          grade: calculateCurrentGrade(user),
          badge,
          avatarColor: user.avatarColor || "#00E5FF"
        };
      });
    };

    return {
      monthlyLeaderboard: mapEntries(sortMonthly, true),
      allTimeLeaderboard: mapEntries(sortAllTime, false)
    };
  }, [registeredUsers, currentUser]);

  return (
    <AppContext.Provider
      value={{
        currentUser,
        setCurrentUser,
        neonStatus,
        refreshNeonStatus,
        selectedGrade,
        setSelectedGrade,
        subjects,
        grammarLessons,
        assignments: assignments.filter(a => a.grade === selectedGrade),
        customWords,
        customGrammar,
        friends,
        activeRoom,
        friendMessage,
        monthlyLeaderboard,
        allTimeLeaderboard,
        soundEnabled,
        setSoundEnabled,
        registerOrUpdateAccount,
        setRole,
        addXpAndScore,
        signOut,
        addTeacherAssignment,
        updateTeacherAssignment,
        deleteTeacherAssignment,
        addCustomWord,
        updateCustomWord,
        deleteCustomWord,
        addCustomGrammar,
        updateCustomGrammar,
        deleteCustomGrammar,
        addFriendByCode,
        clearFriendMessage,
        createRoom,
        joinRoom,
        leaveRoom,
        toggleMicInRoom,
        toggleScreenSharingInRoom
      }}
    >
      {children}
    </AppContext.Provider>
  );
};

export function useApp() {
  const context = useContext(AppContext);
  if (!context) throw new Error("useApp must be used within an AppProvider");
  return context;
}
