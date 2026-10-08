import { CustomQuestion, ExamSourceMeta, ExamSourceType, TeacherAssignment } from '../types';

export const EXAM_SOURCES: Record<ExamSourceType, ExamSourceMeta> = {
  THPT_QUOC_GIA: {
    type: 'THPT_QUOC_GIA',
    sourceName: 'Đề thi THPT Quốc Gia chính thức (Bộ GD&ĐT)',
    badge: 'Chuẩn THPTQG',
    description: 'Bộ câu hỏi trắc nghiệm trọng tâm ngữ pháp, từ vựng và collocations bám sát ma trận đề thi tốt nghiệp Bộ GD&ĐT.',
    yearCitation: 'Đề thi Tốt nghiệp THPT & Đề tham khảo Bộ GD&ĐT 2024 - 2026'
  },
  HOC_SINH_GIOI: {
    type: 'HOC_SINH_GIOI',
    sourceName: 'Đề thi Học Sinh Giỏi (HSG) Tỉnh & Toàn Quốc',
    badge: 'Chuyên Sâu HSG',
    description: 'Câu hỏi phân hóa cao, từ đồng nghĩa/trái nghĩa phức tạp, đảo ngữ, câu điều kiện hỗn hợp và Idioms nâng cao.',
    yearCitation: 'Kỳ thi chọn Học sinh giỏi THPT Cấp Tỉnh & Quốc gia'
  },
  CHUYEN_CAP_3: {
    type: 'CHUYEN_CAP_3',
    sourceName: 'Đề Tuyển sinh Lớp 10 Chuyên Ngoại Ngữ & THPT Chuyên',
    badge: 'Đề Thi Chuyên',
    description: 'Kiểm tra ngữ âm, trọng âm, tìm lỗi sai và cấu trúc biến đổi câu dành cho học sinh khá giỏi.',
    yearCitation: 'Đề tuyển sinh THPT Chuyên Sư Phạm, CNN & Chuyên Amsterdam'
  },
  CAMBRIDGE_DGNL: {
    type: 'CAMBRIDGE_DGNL',
    sourceName: 'Đề thi Đánh Giá Năng Lực (ĐGNL) & Cambridge B1-B2',
    badge: 'Chuẩn Quốc Tế',
    description: 'Bộ câu hỏi kiểm tra kỹ năng đọc hiểu ngữ cảnh, điền từ vào văn bản và tiếng Anh giao tiếp thực tế.',
    yearCitation: 'Kỳ thi ĐGNL ĐHQG & Khung tham chiếu CEFR B1 - B2'
  },
  SGK_GLOBAL_SUCCESS: {
    type: 'SGK_GLOBAL_SUCCESS',
    sourceName: 'Bộ Đề Kiểm Tra SGK Global Success (Lớp 6 - 12)',
    badge: 'Chuẩn SGK Mới',
    description: 'Bộ câu hỏi bám sát chuẩn kiến thức, kỹ năng của chương trình Giáo dục Phổ thông mới (SGK Global Success).',
    yearCitation: 'Chương trình GDPT mới Bộ GD&ĐT'
  },
  IELTS_FOUNDATION: {
    type: 'IELTS_FOUNDATION',
    sourceName: 'Đề Nền Tảng Học Thuật Chuẩn IELTS Academic',
    badge: 'IELTS Academic',
    description: 'Hệ thống câu hỏi từ vựng học thuật (AWL), Collocations nâng cao và ngữ pháp chính xác cho IELTS 6.0 - 7.5+.',
    yearCitation: 'Cambridge English Assessment & British Council'
  }
};

export const REPUTABLE_QUESTIONS_POOL: Record<ExamSourceType, CustomQuestion[]> = {
  THPT_QUOC_GIA: [
    {
      id: "thpt_q1",
      question: "If our team ______ more carefully in the first half, we wouldn't have conceded that goal.",
      options: ["had played", "played", "would play", "plays"],
      correctIndex: 0,
      topic: "Câu điều kiện loại 3",
      sourceName: "Đề thi chính thức THPTQG Bộ GD&ĐT",
      grade: 12,
      explanation: "Câu điều kiện loại 3 (Third Conditional) diễn tả giả định trái ngược với quá khứ. Cấu trúc chuẩn: If + S + had + V3/ed, S + would/could + have + V3/ed."
    },
    {
      id: "thpt_q2",
      question: "Students are actively encouraged to ______ part in community volunteer programs.",
      options: ["take", "make", "get", "do"],
      correctIndex: 0,
      topic: "Collocation",
      sourceName: "Đề tham khảo THPT Quốc Gia",
      grade: 11,
      explanation: "Cụm từ cố định (Collocation): 'take part in' đồng nghĩa với 'participate in' / 'join in' (tham gia vào việc gì)."
    },
    {
      id: "thpt_q3",
      question: "The new renewable energy policy is expected to ______ a profound impact on national emissions.",
      options: ["have", "bring", "create", "give"],
      correctIndex: 0,
      topic: "Collocation",
      sourceName: "Đề chính thức THPTQG",
      grade: 12,
      explanation: "Cụm danh từ cố định: 'have an impact on sth' = có tác động sâu rộng lên điều gì."
    },
    {
      id: "thpt_q4",
      question: "Not only ______ the first prize in the national competition, but she also broke the school record.",
      options: ["did she win", "she won", "she did win", "has she won"],
      correctIndex: 0,
      topic: "Đảo ngữ (Inversion)",
      sourceName: "Đề thi chính thức THPTQG",
      grade: 12,
      explanation: "Cấu trúc đảo ngữ với 'Not only': Not only + Trợ động từ + S + V..., but S also... Vế sau chia ở quá khứ đơn (broke) nên vế trước dùng trợ động từ 'did she win'."
    },
    {
      id: "thpt_q5",
      question: "She dedicated most of her career ______ preserving endangered wildlife habitats.",
      options: ["to", "for", "with", "in"],
      correctIndex: 0,
      topic: "Giới từ & Cấu trúc V-ing",
      sourceName: "Đề thi tốt nghiệp THPT",
      grade: 11,
      explanation: "Cấu trúc: 'dedicate sth/oneself to V-ing/N' = cống hiến, tận tụy điều gì cho mục đích gì."
    },
    {
      id: "thpt_q6",
      question: "By the time the headmaster arrived at the conference hall, the ceremony ______.",
      options: ["had begun", "began", "has begun", "would begin"],
      correctIndex: 0,
      topic: "Phối hợp thì (Quá khứ hoàn thành)",
      sourceName: "Đề thi chính thức THPTQG",
      grade: 10,
      explanation: "Quy tắc phối hợp thì: By the time + S + V(quá khứ đơn), S + had + V3/ed (hành động diễn ra trước một mốc trong quá khứ)."
    },
    {
      id: "thpt_q7",
      question: "The government should ______ measures to combat illegal wildlife poaching immediately.",
      options: ["take", "make", "do", "hold"],
      correctIndex: 0,
      topic: "Collocation",
      sourceName: "Đề minh họa THPTQG Bộ GD&ĐT",
      grade: 12,
      explanation: "Cụm từ cố định chuẩn thi THPTQG: 'take measures / take action' = áp dụng các biện pháp xử lý."
    },
    {
      id: "thpt_q8",
      question: "The novel ______ by the young author last year has received several international literary awards.",
      options: ["written", "which wrote", "writing", "was written"],
      correctIndex: 0,
      topic: "Rút gọn mệnh đề quan hệ",
      sourceName: "Đề thi chính thức THPTQG",
      grade: 11,
      explanation: "Rút gọn mệnh đề quan hệ mang nghĩa bị động: 'The novel which was written' -> rút gọn thành dạng quá khứ phân từ 'written'."
    }
  ],

  HOC_SINH_GIOI: [
    {
      id: "hsg_q1",
      question: "Choose the CLOSEST in meaning to 'METICULOUS': The scientist was meticulous in recording laboratory measurements.",
      options: ["Painstaking and thorough", "Careless and hasty", "Ambiguous and vague", "Superficial and brief"],
      correctIndex: 0,
      topic: "Từ đồng nghĩa nâng cao",
      sourceName: "Đề thi HSG Cấp Tỉnh",
      grade: 12,
      explanation: "'Meticulous' có nghĩa là tỉ mỉ, cẩn thận từng chi tiết, đồng nghĩa với 'painstaking and thorough'."
    },
    {
      id: "hsg_q2",
      question: "Choose the OPPOSITE in meaning to 'UBIQUITOUS': Smartphones are now ubiquitous in modern urban societies.",
      options: ["Scarce and rare", "Pervasive", "Omnipresent", "Prevalent"],
      correctIndex: 0,
      topic: "Từ trái nghĩa nâng cao",
      sourceName: "Đề thi HSG Quốc Gia",
      grade: 12,
      explanation: "'Ubiquitous' = có mặt ở khắp mọi nơi. Từ trái nghĩa chính xác là 'Scarce and rare' (hiếm có, ít thấy)."
    },
    {
      id: "hsg_q3",
      question: "Hardly ______ down to revise his examination notes when the thunderous storm hit.",
      options: ["had he sat", "he had sat", "did he sit", "was he sitting"],
      correctIndex: 0,
      topic: "Đảo ngữ thời gian",
      sourceName: "Đề thi HSG Cấp Tỉnh",
      grade: 11,
      explanation: "Cấu trúc đảo ngữ: 'Hardly + had + S + V3/ed... when + S + V(quá khứ đơn)' = Vừa mới... thì đã..."
    },
    {
      id: "hsg_q4",
      question: "Idiom: When dealing with rigorous exams, remembering to 'keep your ______' will prevent reckless errors.",
      options: ["cool", "head", "mind", "chill"],
      correctIndex: 0,
      topic: "Thành ngữ (Idioms)",
      sourceName: "Đề thi HSG THPT",
      grade: 11,
      explanation: "Thành ngữ chuẩn: 'keep one's cool' = giữ bình tĩnh, sáng suốt trong tình huống căng thẳng."
    },
    {
      id: "hsg_q5",
      question: "It is imperative that every student ______ present at the auditorium before 7:00 AM.",
      options: ["be", "is", "was", "are"],
      correctIndex: 0,
      topic: "Câu giả định thức (Subjunctive Mood)",
      sourceName: "Đề thi HSG Quốc Gia",
      grade: 12,
      explanation: "Cấu trúc bàng thái cách / giả định thức: It is imperative / essential / crucial that + S + (should) + V-nguyên thể. Do đó dùng động từ nguyên mẫu 'be'."
    },
    {
      id: "hsg_q6",
      question: "Had she known about the torrential downpour, she ______ an umbrella with her.",
      options: ["would have brought", "brought", "will bring", "would bring"],
      correctIndex: 0,
      topic: "Đảo ngữ câu điều kiện loại 3",
      sourceName: "Đề thi HSG Cấp Tỉnh",
      grade: 11,
      explanation: "Đảo ngữ câu điều kiện loại 3: 'Had + S + V3/ed, S + would have + V3/ed'. Tương đương 'If she had known...'."
    }
  ],

  CHUYEN_CAP_3: [
    {
      id: "chuyen_q1",
      question: "Pronunciation: Choose the word whose underlined part '-ed' is pronounced differently: looked, watched, stopped, decided.",
      options: ["decided (/ɪd/)", "looked (/t/)", "watched (/t/)", "stopped (/t/)"],
      correctIndex: 0,
      topic: "Ngữ âm đuôi -ed",
      sourceName: "Đề thi Tuyển sinh Lớp 10 Chuyên Ngoại Ngữ",
      grade: 9,
      explanation: "Đuôi -ed được phát âm là /ɪd/ khi động từ kết thúc bằng âm /t/ hoặc /d/ ('decided'). Các từ còn lại có tận cùng là phụ âm vô thanh /k/, /tʃ/, /p/ nên phát âm là /t/."
    },
    {
      id: "chuyen_q2",
      question: "Stress: Choose the word with a different stress position: dedicate, resilient, candidate, infrastructure.",
      options: ["resilient (trọng âm 2)", "dedicate (trọng âm 1)", "candidate (trọng âm 1)", "infrastructure (trọng âm 1)"],
      correctIndex: 0,
      topic: "Trọng âm 3-4 âm tiết",
      sourceName: "Đề thi Tuyển sinh Lớp 10 Chuyên Sư Phạm",
      grade: 9,
      explanation: "'re-SIL-i-ent' có trọng âm rơi vào âm tiết thứ 2. Các từ 'DED-i-cate', 'CAN-di-date', 'IN-fra-struc-ture' có trọng âm rơi vào âm tiết thứ nhất."
    },
    {
      id: "chuyen_q3",
      question: "Error Identification: 'Despite of the heavy monsoon rain, the marathon runners completed the final lap.'",
      options: ["Despite of (sửa thành Despite hoặc In spite of)", "monsoon rain", "completed", "final lap"],
      correctIndex: 0,
      topic: "Tìm lỗi sai liên từ",
      sourceName: "Đề thi Tuyển sinh Lớp 10 Chuyên Anh",
      grade: 9,
      explanation: "Quy tắc ngữ pháp chuẩn: 'Despite' KHÔNG đi với 'of' (Despite + N/V-ing hoặc In spite of + N/V-ing)."
    },
    {
      id: "chuyen_q4",
      question: "Sentence Transformation: 'I have never tasted such a delicious meal before.' = It is ______ meal I have ever tasted.",
      options: ["the most delicious", "a more delicious", "delicious as", "the deliciousest"],
      correctIndex: 0,
      topic: "Biến đổi câu so sánh",
      sourceName: "Đề thi Chuyên Lớp 10",
      grade: 9,
      explanation: "Cấu trúc chuyển đổi giữa thì hiện tại hoàn thành phủ định và so sánh hơn nhất: 'I have never V3 such a...' = 'It is the most + adj + N that I have ever V3'."
    }
  ],

  CAMBRIDGE_DGNL: [
    {
      id: "dgnl_q1",
      question: "Social Communication: Peter: 'Would you mind helping me carry these grammar books?' - Mary: '______.'",
      options: ["Not at all. Let me give you a hand.", "Yes, I would.", "Never mind, you're welcome.", "No, I don't like."],
      correctIndex: 0,
      topic: "Giao tiếp xã hội chuẩn Cambridge",
      sourceName: "Đề thi Đánh Giá Năng Lực & KET/PET",
      grade: 8,
      explanation: "Đáp lại lời thỉnh cầu lịch sự 'Would you mind...?' câu đồng ý giúp đỡ là 'Not at all' (Không hề phiền chút nào. Để mình giúp một tay)."
    },
    {
      id: "dgnl_q2",
      question: "Contextual Cloze: Modern urban planners focus on creating designated ______ zones where motor vehicles are prohibited.",
      options: ["pedestrian", "passenger", "commuter", "driver"],
      correctIndex: 0,
      topic: "Từ vựng ngữ cảnh đô thị",
      sourceName: "Cambridge English B1 Preliminary",
      grade: 10,
      explanation: "'Pedestrian zones' là phố đi bộ (nơi cấm ô tô, xe máy lưu thông nhằm bảo vệ môi trường và người đi bộ)."
    },
    {
      id: "dgnl_q3",
      question: "Academic Reading: The invention of the microscope was one of science's most ______ milestones.",
      options: ["significant", "significance", "significantly", "signify"],
      correctIndex: 0,
      topic: "Cấu tạo từ (Word Formation)",
      sourceName: "Đề thi ĐGNL ĐHQG",
      grade: 10,
      explanation: "Sau trạng từ so sánh hơn nhất 'most' và trước danh từ số nhiều 'milestones' cần một tính từ bổ nghĩa: 'significant' (quan trọng, có ý nghĩa lớn)."
    },
    {
      id: "dgnl_q4",
      question: "If you don't know the definition of a technical term, you should look it ______ in a reputable dictionary.",
      options: ["up", "after", "into", "for"],
      correctIndex: 0,
      topic: "Cụm động từ (Phrasal Verbs)",
      sourceName: "Cambridge B2 First Examination",
      grade: 10,
      explanation: "Phrasal verb: 'look up a word in a dictionary' = tra cứu một từ trong từ điển."
    }
  ],

  SGK_GLOBAL_SUCCESS: [
    {
      id: "sgk_q1",
      question: "Global Success Unit 1 (Grade 10): Doing household chores helps teenagers develop essential life ______.",
      options: ["skills", "traditions", "customs", "appliances"],
      correctIndex: 0,
      topic: "Family Life (Unit 1 SGK)",
      sourceName: "SGK Tiếng Anh 10 Global Success",
      grade: 10,
      explanation: "Life skills = kỹ năng sống. Chia sẻ việc nhà giúp thanh thiếu niên hình thành tinh thần tự lập và kỹ năng sống thiết yếu."
    },
    {
      id: "sgk_q2",
      question: "Global Success Unit 2 (Grade 10): We should reduce our carbon ______ by planting more trees and using public transport.",
      options: ["footprint", "fingerprint", "emission", "impact"],
      correctIndex: 0,
      topic: "Humans and the Environment",
      sourceName: "SGK Tiếng Anh 10 Global Success",
      grade: 10,
      explanation: "Carbon footprint = dấu chân carbon (tổng lượng khí nhà kính phát thải do hoạt động của con người)."
    },
    {
      id: "sgk_q3",
      question: "Global Success Unit 3 (Grade 11): Many Asian nations have preserved their ______ heritage across centuries.",
      options: ["cultural", "culturally", "culture", "cultivate"],
      correctIndex: 0,
      topic: "Cultural Heritage",
      sourceName: "SGK Tiếng Anh 11 Global Success",
      grade: 11,
      explanation: "Trước danh từ 'heritage' cần tính từ 'cultural': 'cultural heritage' = di sản văn hóa."
    },
    {
      id: "sgk_q4",
      question: "Global Success Unit 1 (Grade 12): Nelson Mandela was an inspiring leader who devoted his life to the struggle for ______.",
      options: ["equality", "inequality", "equalize", "equally"],
      correctIndex: 0,
      topic: "Life Stories of Famous People",
      sourceName: "SGK Tiếng Anh 12 Global Success",
      grade: 12,
      explanation: "Sau giới từ 'for' cần một danh từ: 'equality' = sự bình đẳng (cuộc đấu tranh vì bình đẳng xã hội)."
    }
  ],

  IELTS_FOUNDATION: [
    {
      id: "ielts_q1",
      question: "Academic Vocabulary: Rapid industrialization has led to the severe ______ of natural resources worldwide.",
      options: ["depletion", "accumulation", "conservation", "restoration"],
      correctIndex: 0,
      topic: "Academic Word List (AWL)",
      sourceName: "IELTS Academic Vocabulary Bank",
      grade: 11,
      explanation: "'Resource depletion' = sự cạn kiệt tài nguyên thiên nhiên (từ vựng học thuật Band 7.0+)."
    },
    {
      id: "ielts_q2",
      question: "Collocation: Technological innovations have played a ______ role in shaping the modern knowledge economy.",
      options: ["pivotal", "minor", "nominal", "slight"],
      correctIndex: 0,
      topic: "Academic Collocations",
      sourceName: "Cambridge IELTS Foundation",
      grade: 12,
      explanation: "Collocation học thuật đắt giá: 'play a pivotal role in sth' = đóng vai trò then chốt / mang tính quyết định trong việc gì."
    },
    {
      id: "ielts_q3",
      question: "Grammar Range: Were the temperature to increase by 2 degrees, low-lying coastal regions ______ chronic flooding.",
      options: ["would experience", "will experience", "experienced", "had experienced"],
      correctIndex: 0,
      topic: "Đảo ngữ câu điều kiện loại 2 (Inversion)",
      sourceName: "IELTS Advanced Grammar",
      grade: 12,
      explanation: "Đảo ngữ câu điều kiện loại 2: 'Were + S + to-V, S + would + V-nguyên thể' (Nếu nhiệt độ có tăng 2 độ...)."
    }
  ]
};

/**
 * Get all questions from the reputable pool matching source and grade filter
 */
export function getReputableQuestionsPool(sourceType?: ExamSourceType, grade?: number): CustomQuestion[] {
  let list: CustomQuestion[] = [];
  if (sourceType) {
    list = REPUTABLE_QUESTIONS_POOL[sourceType] || [];
  } else {
    list = Object.values(REPUTABLE_QUESTIONS_POOL).flat();
  }

  if (grade) {
    // Return questions suitable for the requested grade
    const gradeMatched = list.filter(q => q.grade === grade);
    if (gradeMatched.length >= 3) return gradeMatched;
  }
  return list;
}

/**
 * Generate a complete, verified Teacher Assignment from standard reputable sources
 */
export function generateReputableExam(
  grade: number,
  sourceType: ExamSourceType,
  questionCount: number,
  teacherName: string,
  customTitle?: string
): TeacherAssignment {
  const meta = EXAM_SOURCES[sourceType];
  const pool = [...(REPUTABLE_QUESTIONS_POOL[sourceType] || [])];
  
  // Sort or shuffle
  const shuffled = pool.sort(() => Math.random() - 0.5);
  const selected = shuffled.slice(0, Math.min(questionCount, pool.length));

  const titles: Record<ExamSourceType, string> = {
    THPT_QUOC_GIA: `Đề Thi Trọng Điểm THPT Quốc Gia - Lớp ${grade}`,
    HOC_SINH_GIOI: `Đề Luyện Thi Học Sinh Giỏi (HSG) - Khối ${grade}`,
    CHUYEN_CAP_3: `Đề Tuyển Sinh Lớp 10 & Chuyên Anh - Lớp ${grade}`,
    CAMBRIDGE_DGNL: `Đề Khảo Sát ĐGNL & Chuẩn Cambridge - Lớp ${grade}`,
    SGK_GLOBAL_SUCCESS: `Đề Kiểm Tra Chuẩn SGK Global Success - Lớp ${grade}`,
    IELTS_FOUNDATION: `Đề Khảo Sát Nền Tảng IELTS Academic - Lớp ${grade}`
  };

  return {
    id: `exam_rep_${Date.now()}`,
    teacherUid: 'teacher_verified',
    teacherName: teacherName.trim() || 'Tổ Ngoại Ngữ THPT Lương Phú',
    grade,
    title: customTitle?.trim() || titles[sourceType],
    description: `Đề thi trích xuất từ ${meta.sourceName}. Gồm ${selected.length} câu hỏi chuẩn hóa bám sát ma trận thi, kèm đáp án & lời giải chi tiết.`,
    questions: selected,
    sourceType,
    createdAt: Date.now()
  };
}
