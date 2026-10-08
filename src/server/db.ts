import { Pool } from 'pg';

// Support DATABASE_URL for Neon PostgreSQL (e.g. postgresql://user:pass@ep-xyz.us-east-2.aws.neon.tech/neondb?sslmode=require)
const connectionString = process.env.DATABASE_URL;

let pool: Pool | null = null;
let isNeonConnected = false;
let connectionErrorMessage: string | null = null;

if (connectionString) {
  try {
    pool = new Pool({
      connectionString,
      ssl: {
        rejectUnauthorized: false
      },
      max: 10,
      idleTimeoutMillis: 30000,
      connectionTimeoutMillis: 5000
    });
  } catch (err: any) {
    console.error('Failed to initialize Neon PostgreSQL pool:', err);
    connectionErrorMessage = err.message || 'Database initialization error';
  }
} else {
  connectionErrorMessage = 'DATABASE_URL is not set. Using local in-memory fallback. Add DATABASE_URL to connect to Neon PostgreSQL.';
}

export async function initDatabase() {
  if (!pool) {
    console.log('[Neon DB] No DATABASE_URL provided. App running with local fallback.');
    return false;
  }

  try {
    const client = await pool.connect();
    try {
      console.log('[Neon DB] Successfully connected to Neon PostgreSQL. Initializing tables...');

      await client.query(`
        CREATE TABLE IF NOT EXISTS users (
          uid VARCHAR(128) PRIMARY KEY,
          email VARCHAR(255),
          display_name VARCHAR(255) NOT NULL,
          custom_class_name VARCHAR(50),
          base_grade INT DEFAULT 10,
          registered_academic_year INT DEFAULT 2026,
          friend_code VARCHAR(50) UNIQUE,
          role VARCHAR(20) DEFAULT 'STUDENT',
          selected_grade INT DEFAULT 10,
          level INT DEFAULT 1,
          xp INT DEFAULT 0,
          highest_score INT DEFAULT 0,
          monthly_score INT DEFAULT 0,
          last_score_month_key VARCHAR(20),
          streak_days INT DEFAULT 1,
          avatar_color VARCHAR(30),
          status VARCHAR(255),
          pin VARCHAR(100),
          created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
          updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
        );

        CREATE TABLE IF NOT EXISTS assignments (
          id VARCHAR(128) PRIMARY KEY,
          teacher_uid VARCHAR(128),
          teacher_name VARCHAR(255),
          grade INT,
          title VARCHAR(255),
          description TEXT,
          questions JSONB,
          created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
        );

        CREATE TABLE IF NOT EXISTS custom_words (
          id VARCHAR(128) PRIMARY KEY,
          word VARCHAR(100) NOT NULL,
          ipa VARCHAR(100),
          meaning TEXT NOT NULL,
          example TEXT,
          grade INT,
          unit_id VARCHAR(100),
          created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
        );

        CREATE TABLE IF NOT EXISTS custom_grammar (
          id VARCHAR(128) PRIMARY KEY,
          title VARCHAR(255) NOT NULL,
          grade INT,
          structure TEXT,
          usage TEXT,
          example TEXT,
          exercise JSONB,
          created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
        );

        CREATE TABLE IF NOT EXISTS friends (
          id SERIAL PRIMARY KEY,
          user_uid VARCHAR(128) NOT NULL,
          friend_uid VARCHAR(128) NOT NULL,
          created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
          UNIQUE(user_uid, friend_uid)
        );
      `);

      isNeonConnected = true;
      connectionErrorMessage = null;
      console.log('[Neon DB] Tables verified/created successfully.');
      return true;
    } finally {
      client.release();
    }
  } catch (err: any) {
    console.error('[Neon DB] Error connecting or initializing tables:', err);
    isNeonConnected = false;
    connectionErrorMessage = err.message || 'Neon connection error';
    return false;
  }
}

export function getDatabaseStatus() {
  return {
    isNeonConfigured: !!connectionString,
    isConnected: isNeonConnected,
    isNeonUrl: connectionString ? connectionString.includes('neon.tech') : false,
    message: isNeonConnected
      ? 'Đã kết nối cơ sở dữ liệu Neon PostgreSQL đám mây thành công!'
      : connectionErrorMessage || 'Chưa kết nối Neon database.'
  };
}

export { pool };
