const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = "demo-no-project";
const TEACHER_UID = "teacher_123";
const STUDENT_UID = "student_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read users", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").get());
});

test("Authenticated user: can read and write own profile", async () => {
  const studentDb = testEnv.authenticatedContext(STUDENT_UID).firestore();
  await assertSucceeds(
    studentDb.collection("users").doc(STUDENT_UID).set({
      userId: STUDENT_UID,
      displayName: "Student A",
      role: "STUDENT",
      selectedGrade: 10,
      xp: 100,
    })
  );
  await assertSucceeds(studentDb.collection("users").doc(STUDENT_UID).get());
});

test("Authenticated user: cannot write another user's profile", async () => {
  const studentDb = testEnv.authenticatedContext(STUDENT_UID).firestore();
  await assertFails(
    studentDb.collection("users").doc(TEACHER_UID).set({
      userId: TEACHER_UID,
      displayName: "Hacked",
    })
  );
});

test("Teacher: can create assignments and students can read", async () => {
  const teacherDb = testEnv.authenticatedContext(TEACHER_UID).firestore();
  await assertSucceeds(
    teacherDb.collection("assignments").doc("quiz_1").set({
      id: "quiz_1",
      teacherUid: TEACHER_UID,
      grade: 10,
      title: "Quiz 1",
    })
  );

  const studentDb = testEnv.authenticatedContext(STUDENT_UID).firestore();
  await assertSucceeds(studentDb.collection("assignments").doc("quiz_1").get());
});

test("Teacher: can create customWords and customGrammar", async () => {
  const teacherDb = testEnv.authenticatedContext(TEACHER_UID).firestore();
  await assertSucceeds(
    teacherDb.collection("customWords").doc("w_1").set({
      id: "w_1",
      teacherUid: TEACHER_UID,
      grade: 11,
      word: "Resilience",
    })
  );
  await assertSucceeds(
    teacherDb.collection("customGrammar").doc("g_1").set({
      id: "g_1",
      teacherUid: TEACHER_UID,
      grade: 12,
      title: "Inversion",
    })
  );
});
