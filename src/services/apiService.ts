import { UserProfile, TeacherAssignment, WordItem, GrammarLesson } from '../types';

export interface NeonStatus {
  isNeonConfigured: boolean;
  isConnected: boolean;
  isNeonUrl: boolean;
  message: string;
}

export const apiService = {
  async getNeonStatus(): Promise<NeonStatus> {
    try {
      const res = await fetch('/api/neon/status');
      if (res.ok) {
        return await res.json();
      }
    } catch {
      // offline / standalone
    }
    return {
      isNeonConfigured: false,
      isConnected: false,
      isNeonUrl: false,
      message: 'Đang chạy chế độ client lưu trữ cục bộ.'
    };
  },

  async registerUser(userData: {
    name: string;
    customClassName: string;
    baseGrade: number;
    registeredAcademicYear: number;
    role: 'STUDENT' | 'TEACHER';
    pin?: string;
  }): Promise<UserProfile | null> {
    try {
      const res = await fetch('/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(userData)
      });
      const data = await res.json();
      if (res.ok && data.user) {
        return data.user as UserProfile;
      }
      if (data.error) {
        throw new Error(data.error);
      }
    } catch (err: any) {
      console.warn('Backend register failed, falling back to local creation:', err.message);
    }
    return null;
  },

  async loginUser(identifier: string, pin: string): Promise<UserProfile | null> {
    try {
      const res = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ identifier, pin })
      });
      const data = await res.json();
      if (res.ok && data.user) {
        return data.user as UserProfile;
      }
      if (data.error) {
        throw new Error(data.error);
      }
    } catch (err: any) {
      if (err.message && !err.message.includes('fetch')) {
        throw err;
      }
    }
    return null;
  },

  async updateProgress(uid: string, xpToAdd: number, scoreEarned: number): Promise<void> {
    try {
      await fetch(`/api/users/${uid}/progress`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ xpToAdd, scoreEarned })
      });
    } catch {
      // safe ignore in offline mode
    }
  },

  async getLeaderboard(): Promise<any[]> {
    try {
      const res = await fetch('/api/leaderboard');
      if (res.ok) {
        const data = await res.json();
        return data.leaderboard || [];
      }
    } catch {
      // offline
    }
    return [];
  },

  async getAssignments(): Promise<TeacherAssignment[]> {
    try {
      const res = await fetch('/api/assignments');
      if (res.ok) {
        const data = await res.json();
        return data.assignments || [];
      }
    } catch {}
    return [];
  },

  async saveAssignment(assignment: TeacherAssignment): Promise<void> {
    try {
      await fetch('/api/assignments', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(assignment)
      });
    } catch {}
  },

  async deleteAssignment(id: string): Promise<void> {
    try {
      await fetch(`/api/assignments/${id}`, { method: 'DELETE' });
    } catch {}
  },

  async getCustomWords(): Promise<WordItem[]> {
    try {
      const res = await fetch('/api/custom-words');
      if (res.ok) {
        const data = await res.json();
        return data.words || [];
      }
    } catch {}
    return [];
  },

  async saveCustomWord(word: WordItem): Promise<void> {
    try {
      await fetch('/api/custom-words', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(word)
      });
    } catch {}
  },

  async deleteCustomWord(id: string): Promise<void> {
    try {
      await fetch(`/api/custom-words/${id}`, { method: 'DELETE' });
    } catch {}
  },

  async getCustomGrammar(): Promise<GrammarLesson[]> {
    try {
      const res = await fetch('/api/custom-grammar');
      if (res.ok) {
        const data = await res.json();
        return data.grammar || [];
      }
    } catch {}
    return [];
  },

  async saveCustomGrammar(lesson: GrammarLesson): Promise<void> {
    try {
      await fetch('/api/custom-grammar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(lesson)
      });
    } catch {}
  },

  async deleteCustomGrammar(id: string): Promise<void> {
    try {
      await fetch(`/api/custom-grammar/${id}`, { method: 'DELETE' });
    } catch {}
  }
};
