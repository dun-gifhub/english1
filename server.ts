import express from 'express';
import cors from 'cors';
import path from 'path';
import { fileURLToPath } from 'url';
import dotenv from 'dotenv';
import { pool, initDatabase, getDatabaseStatus } from './src/server/db.js';

dotenv.config();

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();
const port = parseInt(process.env.PORT || '3000', 10);
const isProduction = process.env.NODE_ENV === 'production';

app.use(cors());
app.use(express.json());

// In-memory fallback stores if Neon is not yet connected via DATABASE_URL
const memUsers: Map<string, any> = new Map();
const memAssignments: Map<string, any> = new Map();
const memWords: Map<string, any> = new Map();
const memGrammar: Map<string, any> = new Map();

// Initialize Neon DB on startup
initDatabase().catch((err) => {
  console.warn('[Server] DB initialization warning:', err.message);
});

// ----------------- API ROUTES ----------------- //

// Health check
app.get('/api/health', (_req, res) => {
  res.json({
    status: 'ok',
    app: 'Tap Hunter English',
    timestamp: new Date().toISOString(),
    env: process.env.NODE_ENV || 'development',
    database: getDatabaseStatus()
  });
});

// Neon status check
app.get('/api/neon/status', (_req, res) => {
  res.json(getDatabaseStatus());
});

// Auth: Register (No demo accounts)
app.post('/api/auth/register', async (req, res) => {
  try {
    const {
      name,
      customClassName,
      baseGrade,
      registeredAcademicYear,
      role = 'STUDENT',
      pin = ''
    } = req.body;

    if (!name || !name.trim()) {
      return res.status(400).json({ error: 'Tên tài khoản không được để trống' });
    }

    const cleanName = name.trim();
    const uid = `usr_${Date.now()}_${Math.random().toString(36).substring(2, 7)}`;
    const randomCode = Math.floor(1000 + Math.random() * 9000);
    const friendCode = `LP-${randomCode}`;
    const grade = Number(baseGrade) || 10;
    const academicYear = Number(registeredAcademicYear) || 2026;
    const email = `${uid}@luongphu.edu.vn`;
    const avatarColors = ['#00E5FF', '#9D4EDD', '#FFD166', '#06D6A0', '#EF476F'];
    const avatarColor = avatarColors[Math.floor(Math.random() * avatarColors.length)];

    const newUser = {
      uid,
      email,
      displayName: cleanName,
      customClassName: (customClassName || '10A1').trim(),
      baseGrade: grade,
      registeredAcademicYear: academicYear,
      friendCode,
      role,
      selectedGrade: grade,
      level: 1,
      xp: 0,
      highestScore: 0,
      monthlyScore: 0,
      lastScoreMonthKey: new Date().toISOString().slice(0, 7),
      streakDays: 1,
      avatarColor,
      status: role === 'TEACHER' ? 'Giáo viên Tiếng Anh' : 'Tân binh săn từ vựng',
      pin: pin ? String(pin).trim() : ''
    };

    if (pool && getDatabaseStatus().isConnected) {
      await pool.query(
        `INSERT INTO users (
          uid, email, display_name, custom_class_name, base_grade, registered_academic_year,
          friend_code, role, selected_grade, level, xp, highest_score, monthly_score,
          last_score_month_key, streak_days, avatar_color, status, pin
        ) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, $15, $16, $17, $18)`,
        [
          newUser.uid,
          newUser.email,
          newUser.displayName,
          newUser.customClassName,
          newUser.baseGrade,
          newUser.registeredAcademicYear,
          newUser.friendCode,
          newUser.role,
          newUser.selectedGrade,
          newUser.level,
          newUser.xp,
          newUser.highestScore,
          newUser.monthlyScore,
          newUser.lastScoreMonthKey,
          newUser.streakDays,
          newUser.avatarColor,
          newUser.status,
          newUser.pin
        ]
      );
    } else {
      memUsers.set(uid, newUser);
    }

    res.status(201).json({ success: true, user: newUser });
  } catch (err: any) {
    console.error('Register error:', err);
    res.status(500).json({ error: err.message || 'Lỗi đăng ký tài khoản' });
  }
});

// Auth: Login by FriendCode or Name + PIN
app.post('/api/auth/login', async (req, res) => {
  try {
    const { identifier, pin = '' } = req.body;
    if (!identifier || !identifier.trim()) {
      return res.status(400).json({ error: 'Vui lòng nhập Tên tài khoản hoặc Mã bạn bè' });
    }

    const clean = identifier.trim();

    let user: any = null;

    if (pool && getDatabaseStatus().isConnected) {
      const result = await pool.query(
        `SELECT * FROM users WHERE LOWER(friend_code) = LOWER($1) OR LOWER(display_name) = LOWER($1) LIMIT 1`,
        [clean]
      );
      if (result.rows.length > 0) {
        const row = result.rows[0];
        user = {
          uid: row.uid,
          email: row.email,
          displayName: row.display_name,
          customClassName: row.custom_class_name,
          baseGrade: row.base_grade,
          registeredAcademicYear: row.registered_academic_year,
          friendCode: row.friend_code,
          role: row.role,
          selectedGrade: row.selected_grade,
          level: row.level,
          xp: row.xp,
          highestScore: row.highest_score,
          monthlyScore: row.monthly_score,
          lastScoreMonthKey: row.last_score_month_key,
          streakDays: row.streak_days,
          avatarColor: row.avatar_color,
          status: row.status,
          pin: row.pin
        };
      }
    } else {
      for (const u of memUsers.values()) {
        if (
          u.friendCode.toLowerCase() === clean.toLowerCase() ||
          u.displayName.toLowerCase() === clean.toLowerCase()
        ) {
          user = u;
          break;
        }
      }
    }

    if (!user) {
      return res.status(404).json({ error: 'Không tìm thấy tài khoản. Vui lòng kiểm tra lại tên hoặc đăng ký mới.' });
    }

    if (user.pin && pin && user.pin !== pin.trim()) {
      return res.status(401).json({ error: 'Mã PIN bảo mật không chính xác!' });
    }

    res.json({ success: true, user });
  } catch (err: any) {
    console.error('Login error:', err);
    res.status(500).json({ error: err.message || 'Lỗi đăng nhập' });
  }
});

// Update Progress & XP
app.post('/api/users/:uid/progress', async (req, res) => {
  try {
    const { uid } = req.params;
    const { xpToAdd = 0, scoreEarned = 0 } = req.body;

    const currentMonthKey = new Date().toISOString().slice(0, 7);

    if (pool && getDatabaseStatus().isConnected) {
      const userRes = await pool.query(`SELECT * FROM users WHERE uid = $1`, [uid]);
      if (userRes.rows.length === 0) {
        return res.status(404).json({ error: 'User not found' });
      }
      const user = userRes.rows[0];
      const newXp = (user.xp || 0) + xpToAdd;
      const newLevel = Math.max(1, Math.floor(newXp / 500) + 1);
      const isNewMonth = user.last_score_month_key !== currentMonthKey;
      const currentMonthly = isNewMonth ? 0 : (user.monthly_score || 0);
      const newMonthly = currentMonthly + scoreEarned;
      const newHighest = Math.max(user.highest_score || 0, newMonthly);

      await pool.query(
        `UPDATE users SET xp = $1, level = $2, monthly_score = $3, highest_score = $4, last_score_month_key = $5, updated_at = NOW() WHERE uid = $6`,
        [newXp, newLevel, newMonthly, newHighest, currentMonthKey, uid]
      );

      return res.json({ success: true, xp: newXp, level: newLevel, monthlyScore: newMonthly, highestScore: newHighest });
    } else {
      const user = memUsers.get(uid);
      if (user) {
        user.xp = (user.xp || 0) + xpToAdd;
        user.level = Math.max(1, Math.floor(user.xp / 500) + 1);
        if (user.lastScoreMonthKey !== currentMonthKey) {
          user.monthlyScore = 0;
          user.lastScoreMonthKey = currentMonthKey;
        }
        user.monthlyScore = (user.monthlyScore || 0) + scoreEarned;
        user.highestScore = Math.max(user.highestScore || 0, user.monthlyScore);
        memUsers.set(uid, user);
        return res.json({ success: true, ...user });
      }
      return res.status(404).json({ error: 'User not found in memory' });
    }
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

// Real Leaderboard (Only real registered students, NO demo accounts)
app.get('/api/leaderboard', async (_req, res) => {
  try {
    let usersList: any[] = [];

    if (pool && getDatabaseStatus().isConnected) {
      const result = await pool.query(
        `SELECT uid, display_name, custom_class_name, base_grade, level, xp, highest_score, monthly_score, avatar_color, status
         FROM users
         WHERE role = 'STUDENT'
         ORDER BY monthly_score DESC, highest_score DESC
         LIMIT 50`
      );
      usersList = result.rows.map((r) => ({
        uid: r.uid,
        name: r.display_name,
        className: r.custom_class_name,
        level: r.level,
        xp: r.xp,
        score: r.highest_score,
        monthlyScore: r.monthly_score,
        avatarColor: r.avatar_color,
        status: r.status
      }));
    } else {
      usersList = Array.from(memUsers.values())
        .filter((u) => u.role === 'STUDENT')
        .map((u) => ({
          uid: u.uid,
          name: u.displayName,
          className: u.customClassName,
          level: u.level,
          xp: u.xp,
          score: u.highestScore,
          monthlyScore: u.monthlyScore,
          avatarColor: u.avatarColor,
          status: u.status
        }))
        .sort((a, b) => b.monthlyScore - a.monthlyScore);
    }

    res.json({ success: true, count: usersList.length, leaderboard: usersList });
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

// Teacher Assignments CRUD
app.get('/api/assignments', async (_req, res) => {
  try {
    if (pool && getDatabaseStatus().isConnected) {
      const result = await pool.query(`SELECT * FROM assignments ORDER BY created_at DESC`);
      const items = result.rows.map((row) => ({
        id: row.id,
        teacherUid: row.teacher_uid,
        teacherName: row.teacher_name,
        grade: row.grade,
        title: row.title,
        description: row.description,
        questions: row.questions,
        createdAt: new Date(row.created_at).getTime()
      }));
      return res.json({ success: true, assignments: items });
    }
    res.json({ success: true, assignments: Array.from(memAssignments.values()) });
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/api/assignments', async (req, res) => {
  try {
    const item = req.body;
    if (!item.id || !item.title) {
      return res.status(400).json({ error: 'Assignment must have id and title' });
    }

    if (pool && getDatabaseStatus().isConnected) {
      await pool.query(
        `INSERT INTO assignments (id, teacher_uid, teacher_name, grade, title, description, questions)
         VALUES ($1, $2, $3, $4, $5, $6, $7)
         ON CONFLICT (id) DO UPDATE SET
          teacher_name = EXCLUDED.teacher_name,
          grade = EXCLUDED.grade,
          title = EXCLUDED.title,
          description = EXCLUDED.description,
          questions = EXCLUDED.questions`,
        [
          item.id,
          item.teacherUid || 'unknown_teacher',
          item.teacherName || 'Giáo viên THPT Lương Phú',
          item.grade || 10,
          item.title,
          item.description || '',
          JSON.stringify(item.questions || [])
        ]
      );
    } else {
      memAssignments.set(item.id, item);
    }

    res.json({ success: true, assignment: item });
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

app.delete('/api/assignments/:id', async (req, res) => {
  try {
    const { id } = req.params;
    if (pool && getDatabaseStatus().isConnected) {
      await pool.query(`DELETE FROM assignments WHERE id = $1`, [id]);
    } else {
      memAssignments.delete(id);
    }
    res.json({ success: true, id });
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

// Custom Words CRUD
app.get('/api/custom-words', async (_req, res) => {
  try {
    if (pool && getDatabaseStatus().isConnected) {
      const result = await pool.query(`SELECT * FROM custom_words ORDER BY created_at DESC`);
      const words = result.rows.map((r) => ({
        id: r.id,
        word: r.word,
        ipa: r.ipa,
        meaning: r.meaning,
        example: r.example,
        grade: r.grade,
        unitId: r.unit_id
      }));
      return res.json({ success: true, words });
    }
    res.json({ success: true, words: Array.from(memWords.values()) });
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/api/custom-words', async (req, res) => {
  try {
    const wordItem = req.body;
    if (pool && getDatabaseStatus().isConnected) {
      await pool.query(
        `INSERT INTO custom_words (id, word, ipa, meaning, example, grade, unit_id)
         VALUES ($1, $2, $3, $4, $5, $6, $7)
         ON CONFLICT (id) DO UPDATE SET
          word = EXCLUDED.word,
          ipa = EXCLUDED.ipa,
          meaning = EXCLUDED.meaning,
          example = EXCLUDED.example,
          grade = EXCLUDED.grade`,
        [
          wordItem.id,
          wordItem.word,
          wordItem.ipa || '',
          wordItem.meaning,
          wordItem.example || '',
          wordItem.grade || 10,
          wordItem.unitId || ''
        ]
      );
    } else {
      memWords.set(wordItem.id, wordItem);
    }
    res.json({ success: true, word: wordItem });
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

app.delete('/api/custom-words/:id', async (req, res) => {
  try {
    const { id } = req.params;
    if (pool && getDatabaseStatus().isConnected) {
      await pool.query(`DELETE FROM custom_words WHERE id = $1`, [id]);
    } else {
      memWords.delete(id);
    }
    res.json({ success: true, id });
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

// Custom Grammar CRUD
app.get('/api/custom-grammar', async (_req, res) => {
  try {
    if (pool && getDatabaseStatus().isConnected) {
      const result = await pool.query(`SELECT * FROM custom_grammar ORDER BY created_at DESC`);
      const grammar = result.rows.map((r) => ({
        id: r.id,
        title: r.title,
        grade: r.grade,
        structure: r.structure,
        usage: r.usage,
        example: r.example,
        exercise: r.exercise
      }));
      return res.json({ success: true, grammar });
    }
    res.json({ success: true, grammar: Array.from(memGrammar.values()) });
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/api/custom-grammar', async (req, res) => {
  try {
    const grammarItem = req.body;
    if (pool && getDatabaseStatus().isConnected) {
      await pool.query(
        `INSERT INTO custom_grammar (id, title, grade, structure, usage, example, exercise)
         VALUES ($1, $2, $3, $4, $5, $6, $7)
         ON CONFLICT (id) DO UPDATE SET
          title = EXCLUDED.title,
          grade = EXCLUDED.grade,
          structure = EXCLUDED.structure,
          usage = EXCLUDED.usage,
          example = EXCLUDED.example,
          exercise = EXCLUDED.exercise`,
        [
          grammarItem.id,
          grammarItem.title,
          grammarItem.grade || 10,
          grammarItem.structure || '',
          grammarItem.usage || '',
          grammarItem.example || '',
          JSON.stringify(grammarItem.exercise || null)
        ]
      );
    } else {
      memGrammar.set(grammarItem.id, grammarItem);
    }
    res.json({ success: true, grammar: grammarItem });
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

app.delete('/api/custom-grammar/:id', async (req, res) => {
  try {
    const { id } = req.params;
    if (pool && getDatabaseStatus().isConnected) {
      await pool.query(`DELETE FROM custom_grammar WHERE id = $1`, [id]);
    } else {
      memGrammar.delete(id);
    }
    res.json({ success: true, id });
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

// ----------------- VITE / STATIC SERVING ----------------- //
async function startServer() {
  if (!isProduction) {
    const { createServer: createViteServer } = await import('vite');
    const vite = await createViteServer({
      server: { middlewareMode: true }
    });
    app.use(vite.middlewares);
  } else {
    const distPath = path.resolve(__dirname, 'dist');
    app.use(express.static(distPath));
    app.get('*', (_req, res) => {
      res.sendFile(path.join(distPath, 'index.html'));
    });
  }

  app.listen(port, '0.0.0.0', () => {
    console.log(`[Tap Hunter] Server listening on http://0.0.0.0:${port}`);
    console.log(`[Neon DB] Status: ${getDatabaseStatus().message}`);
  });
}

startServer().catch((err) => {
  console.error('[Tap Hunter] Failed to start server:', err);
});
