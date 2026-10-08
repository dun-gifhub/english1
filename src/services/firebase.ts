import { initializeApp, getApps, FirebaseApp } from 'firebase/app';
import { getAuth, Auth, GoogleAuthProvider, signInWithPopup, signOut as fbSignOut } from 'firebase/auth';
import { getFirestore, Firestore } from 'firebase/firestore';

const firebaseConfig = {
  apiKey: "AIzaSyAFrHKz4UMyZAZYc5Ean_A2LNHtCZK7Js0",
  authDomain: "gen-lang-client-0998315428.firebaseapp.com",
  projectId: "gen-lang-client-0998315428",
  storageBucket: "gen-lang-client-0998315428.firebasestorage.app",
  appId: "1:582517326980:web:d89ef23910ab38c4091e",
};

export const FIRESTORE_DATABASE_ID = "ai-studio-android-english-98b478f5-b792-4fea-a083-8e090a60c349";

let app: FirebaseApp | null = null;
let auth: Auth | null = null;
let db: Firestore | null = null;

try {
  if (!getApps().length) {
    app = initializeApp(firebaseConfig);
  } else {
    app = getApps()[0];
  }
  auth = getAuth(app);
  // Initialize firestore with custom databaseId if configured
  try {
    db = getFirestore(app, FIRESTORE_DATABASE_ID);
  } catch {
    db = getFirestore(app);
  }
} catch (err) {
  console.warn("Firebase initialization notice (offline fallback mode active):", err);
}

export { app, auth, db };

export async function loginWithGoogle(): Promise<{ uid: string; displayName: string; email: string } | null> {
  if (!auth) throw new Error("Firebase Auth chưa khởi tạo.");
  const provider = new GoogleAuthProvider();
  try {
    const res = await signInWithPopup(auth, provider);
    return {
      uid: res.user.uid,
      displayName: res.user.displayName || "Hunter Lương Phú",
      email: res.user.email || ""
    };
  } catch (err) {
    console.warn("Google popup login failed, fallback to local:", err);
    throw err;
  }
}

export async function logoutFirebase(): Promise<void> {
  if (auth) {
    await fbSignOut(auth);
  }
}
