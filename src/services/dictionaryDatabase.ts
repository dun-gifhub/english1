import { DictionaryEntry } from '../types';

export function removeDiacritics(text: string): string {
  return text
    .trim()
    .toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/đ/g, 'd')
    .replace(/Đ/g, 'd');
}

// Extensive Core Dictionary
export const BASE_DICTIONARY: DictionaryEntry[] = [
  // School & Learning
  { wordEn: "school", phonetic: "/skuːl/", partOfSpeech: "n", meaningVi: "trường học, ngôi trường, viện đào tạo", exampleEn: "Luong Phu High School is located in Thai Nguyen.", exampleVi: "Trường THPT Lương Phú nằm tại Thái Nguyên.", synonyms: ["academy", "institution"] },
  { wordEn: "teacher", phonetic: "/ˈtiː.tʃər/", partOfSpeech: "n", meaningVi: "thầy cô giáo, giáo viên, người dạy học", exampleEn: "Our teacher is dedicated and patient.", exampleVi: "Thầy giáo của chúng tôi rất tận tụy và kiên nhẫn.", synonyms: ["educator", "instructor"] },
  { wordEn: "student", phonetic: "/ˈstjuː.dənt/", partOfSpeech: "n", meaningVi: "học sinh, sinh viên, người theo học", exampleEn: "The student scored high in the exam.", exampleVi: "Học sinh đó đạt điểm cao trong kỳ thi.", synonyms: ["pupil", "learner"] },
  { wordEn: "classroom", phonetic: "/ˈklɑːs.ruːm/", partOfSpeech: "n", meaningVi: "phòng học, lớp học", exampleEn: "The classroom is bright and tidy.", exampleVi: "Phòng học rất sáng sủa và ngăn nắp.", synonyms: ["lecture room"] },
  { wordEn: "lesson", phonetic: "/ˈles.ən/", partOfSpeech: "n", meaningVi: "bài học, tiết học, bài giảng", exampleEn: "Today's English lesson is exciting.", exampleVi: "Tiết học tiếng Anh hôm nay rất thú vị.", synonyms: ["lecture", "class"] },
  { wordEn: "homework", phonetic: "/ˈhəʊm.wɜːk/", partOfSpeech: "n", meaningVi: "bài tập về nhà, nhiệm vụ tự học", exampleEn: "Remember to complete your homework.", exampleVi: "Nhớ hoàn thành bài tập về nhà nhé.", synonyms: ["assignment"] },
  { wordEn: "examination", phonetic: "/ɪɡˌzæm.ɪˈneɪ.ʃən/", partOfSpeech: "n", meaningVi: "kỳ thi, bài kiểm tra đánh giá", exampleEn: "She passed the national examination.", exampleVi: "Cô ấy đã thi đỗ kỳ thi quốc gia.", synonyms: ["exam", "test"] },
  { wordEn: "knowledge", phonetic: "/ˈnɒl.ɪdʒ/", partOfSpeech: "n", meaningVi: "tri thức, kiến thức, sự hiểu biết", exampleEn: "Knowledge opens doors to success.", exampleVi: "Kiến thức mở ra những cánh cửa thành công.", synonyms: ["understanding", "wisdom"] },
  { wordEn: "education", phonetic: "/ˌedʒ.ʊˈkeɪ.ʃən/", partOfSpeech: "n", meaningVi: "nền giáo dục, sự giáo dục, việc học tập", exampleEn: "Education is the key to the future.", exampleVi: "Giáo dục là chìa khóa đến tương lai.", synonyms: ["learning", "instruction"] },
  { wordEn: "practice", phonetic: "/ˈpræk.tɪs/", partOfSpeech: "v, n", meaningVi: "luyện tập, rèn luyện, thực hành", exampleEn: "Practice speaking English every day.", exampleVi: "Hãy luyện nói tiếng Anh mỗi ngày.", synonyms: ["exercise", "rehearsal"] },
  { wordEn: "library", phonetic: "/ˈlaɪ.brər.i/", partOfSpeech: "n", meaningVi: "thư viện, phòng đọc sách", exampleEn: "Students read books in the library.", exampleVi: "Học sinh đọc sách trong thư viện.", synonyms: ["book room"] },
  { wordEn: "subject", phonetic: "/ˈsʌb.dʒɪkt/", partOfSpeech: "n", meaningVi: "môn học, chủ đề, đối tượng", exampleEn: "English is my favorite subject.", exampleVi: "Tiếng Anh là môn học yêu thích của tôi.", synonyms: ["discipline", "topic"] },
  { wordEn: "dictionary", phonetic: "/ˈdɪk.ʃən.ər.i/", partOfSpeech: "n", meaningVi: "từ điển, sách tra cứu ngôn ngữ", exampleEn: "Use a dictionary to check meanings.", exampleVi: "Dùng từ điển để tra cứu nghĩa của từ.", synonyms: ["lexicon", "wordbook"] },
  { wordEn: "language", phonetic: "/ˈlæŋ.ɡwɪdʒ/", partOfSpeech: "n", meaningVi: "ngôn ngữ, tiếng nói", exampleEn: "English is a global language.", exampleVi: "Tiếng Anh là ngôn ngữ toàn cầu.", synonyms: ["speech", "tongue"] },
  { wordEn: "vocabulary", phonetic: "/vəˈkæb.jə.lər.i/", partOfSpeech: "n", meaningVi: "từ vựng, vốn từ", exampleEn: "Expand your vocabulary daily.", exampleVi: "Hãy mở rộng vốn từ vựng của bạn mỗi ngày.", synonyms: ["lexicon", "words"] },
  { wordEn: "grammar", phonetic: "/ˈɡræm.ər/", partOfSpeech: "n", meaningVi: "ngữ pháp, cấu trúc câu", exampleEn: "Grammar helps you write correctly.", exampleVi: "Ngữ pháp giúp bạn viết chính xác.", synonyms: ["syntax"] },
  { wordEn: "pronunciation", phonetic: "/prəˌnʌn.siˈeɪ.ʃən/", partOfSpeech: "n", meaningVi: "sự phát âm, cách đọc", exampleEn: "Good pronunciation improves listening.", exampleVi: "Phát âm tốt giúp cải thiện kỹ năng nghe.", synonyms: ["articulation"] },

  // Daily Life, Society, Science, Nature
  { wordEn: "family", phonetic: "/ˈfæm.əl.i/", partOfSpeech: "n", meaningVi: "gia đình, người thân", exampleEn: "Family is our solid support.", exampleVi: "Gia đình là điểm tựa vững chắc của chúng ta.", synonyms: ["household"] },
  { wordEn: "friend", phonetic: "/frend/", partOfSpeech: "n", meaningVi: "người bạn, bạn bè", exampleEn: "A loyal friend is a true treasure.", exampleVi: "Người bạn trung thành là báu vật thực sự.", synonyms: ["companion", "pal"] },
  { wordEn: "computer", phonetic: "/kəmˈpjuː.tər/", partOfSpeech: "n", meaningVi: "máy vi tính, máy điện toán", exampleEn: "Computers aid modern learning.", exampleVi: "Máy tính hỗ trợ đắc lực cho việc học hiện đại.", synonyms: ["PC"] },
  { wordEn: "phone", phonetic: "/fəʊn/", partOfSpeech: "n", meaningVi: "điện thoại thông minh, liên lạc", exampleEn: "Use your phone to learn vocabulary.", exampleVi: "Dùng điện thoại để học từ vựng.", synonyms: ["mobile", "cellphone"] },
  { wordEn: "internet", phonetic: "/ˈɪn.tə.net/", partOfSpeech: "n", meaningVi: "mạng internet, mạng toàn cầu", exampleEn: "The internet connects learners globally.", exampleVi: "Internet kết nối người học toàn cầu.", synonyms: ["web"] },
  { wordEn: "world", phonetic: "/wɜːld/", partOfSpeech: "n", meaningVi: "thế giới, nhân loại, hoàn cầu", exampleEn: "Explore the world through languages.", exampleVi: "Khám phá thế giới qua những ngôn ngữ.", synonyms: ["globe", "earth"] },
  { wordEn: "life", phonetic: "/laɪf/", partOfSpeech: "n", meaningVi: "cuộc sống, cuộc đời, sinh mệnh", exampleEn: "Live a meaningful and kind life.", exampleVi: "Hãy sống một cuộc đời ý nghĩa và nhân ái.", synonyms: ["existence"] },
  { wordEn: "time", phonetic: "/taɪm/", partOfSpeech: "n", meaningVi: "thời gian, thời khắc, giờ giấc", exampleEn: "Time management is key to success.", exampleVi: "Quản lý thời gian là chìa khóa thành công.", synonyms: ["period"] },
  { wordEn: "nature", phonetic: "/ˈneɪ.tʃər/", partOfSpeech: "n", meaningVi: "thiên nhiên, tự nhiên, bản tính", exampleEn: "Respect and protect surrounding nature.", exampleVi: "Hãy tôn trọng và bảo vệ thiên nhiên quanh ta.", synonyms: ["environment"] },
  { wordEn: "environment", phonetic: "/ɪnˈvaɪ.rən.mənt/", partOfSpeech: "n", meaningVi: "môi trường sinh thái", exampleEn: "Protect our living environment.", exampleVi: "Bảo vệ môi trường sống của chúng ta.", synonyms: ["surroundings"] },
  { wordEn: "health", phonetic: "/helθ/", partOfSpeech: "n", meaningVi: "sức khỏe, thể trạng cơ thể", exampleEn: "Good health is true wealth.", exampleVi: "Sức khỏe tốt là tài sản đích thực.", synonyms: ["wellness", "fitness"] },
  { wordEn: "water", phonetic: "/ˈwɔː.tər/", partOfSpeech: "n", meaningVi: "nước uống, nguồn nước", exampleEn: "Drink clean water every day.", exampleVi: "Uống nước sạch mỗi ngày.", synonyms: ["liquid"] },
  { wordEn: "weather", phonetic: "/ˈweð.ər/", partOfSpeech: "n", meaningVi: "thời tiết, khí hậu", exampleEn: "The weather in Thai Nguyen is pleasant today.", exampleVi: "Thời tiết tại Thái Nguyên hôm nay rất dễ chịu.", synonyms: ["climate"] },
  { wordEn: "city", phonetic: "/ˈsɪt.i/", partOfSpeech: "n", meaningVi: "thành phố, đô thị", exampleEn: "Smart cities are rising.", exampleVi: "Các đô thị thông minh đang mọc lên.", synonyms: ["metropolis", "town"] },
  { wordEn: "countryside", phonetic: "/ˈkʌn.tri.saɪd/", partOfSpeech: "n", meaningVi: "vùng nông thôn, làng quê", exampleEn: "Life in the countryside is serene.", exampleVi: "Cuộc sống ở vùng nông thôn thật yên bình.", synonyms: ["rural area"] },
  { wordEn: "technology", phonetic: "/tekˈnɒl.ə.dʒi/", partOfSpeech: "n", meaningVi: "công nghệ, kỹ thuật hiện đại", exampleEn: "Technology empowers education.", exampleVi: "Công nghệ nâng bước cho giáo dục.", synonyms: ["tech", "engineering"] },

  // Curriculum & Exam High-Frequency Words
  { wordEn: "ubiquitous", phonetic: "/juːˈbɪk.wə.təs/", partOfSpeech: "adj", meaningVi: "có mặt khắp nơi, phổ biến rộng rãi", exampleEn: "Smartphones are ubiquitous today.", exampleVi: "Điện thoại hiện diện khắp mọi nơi ngày nay.", synonyms: ["omnipresent", "pervasive"] },
  { wordEn: "resilience", phonetic: "/rɪˈzɪl.jəns/", partOfSpeech: "n", meaningVi: "sự kiên cường, khả năng phục hồi nhanh", exampleEn: "He showed great resilience in crisis.", exampleVi: "Anh ấy thể hiện sự kiên cường trong khủng hoảng.", synonyms: ["toughness", "flexibility"] },
  { wordEn: "meticulous", phonetic: "/məˈtɪk.jə.ləs/", partOfSpeech: "adj", meaningVi: "tỉ mỉ, cẩn thận từng chi tiết", exampleEn: "She is meticulous about her work.", exampleVi: "Cô ấy rất tỉ mỉ trong công việc.", synonyms: ["thorough", "diligent"] },
  { wordEn: "ephemeral", phonetic: "/ɪˈfem.ər.əl/", partOfSpeech: "adj", meaningVi: "phù du, ngắn ngủi, chóng tàn", exampleEn: "Fashions are ephemeral.", exampleVi: "Thời trang thường rất chóng tàn.", synonyms: ["transient", "fleeting"] },
  { wordEn: "breadwinner", phonetic: "/ˈbredˌwɪn.ər/", partOfSpeech: "n", meaningVi: "trụ cột gia đình, người kiếm sống chính", exampleEn: "He is the sole breadwinner.", exampleVi: "Anh ấy là người trụ cột kiếm tiền duy nhất.", synonyms: ["provider"] },
  { wordEn: "homemaker", phonetic: "/ˈhəʊmˌmeɪ.kər/", partOfSpeech: "n", meaningVi: "người nội trợ quán xuyến gia đình", exampleEn: "She decided to be a full-time homemaker.", exampleVi: "Bà ấy quyết định làm người nội trợ toàn thời gian.", synonyms: ["housewife"] },
  { wordEn: "biodiversity", phonetic: "/ˌbaɪ.əʊ.daɪˈvɜː.sə.ti/", partOfSpeech: "n", meaningVi: "đa dạng sinh học", exampleEn: "Conserving forest biodiversity is vital.", exampleVi: "Bảo tồn đa dạng sinh học rừng là tối quan trọng.", synonyms: ["ecological diversity"] },
  { wordEn: "catastrophic", phonetic: "/ˌkæt.əˈstrɒf.ɪk/", partOfSpeech: "adj", meaningVi: "thảm họa, thảm khốc, tàn phá lớn", exampleEn: "A catastrophic earthquake struck.", exampleVi: "Một trận động đất thảm khốc đã ập đến.", synonyms: ["disastrous", "devastating"] },
  { wordEn: "perseverance", phonetic: "/ˌpɜː.sɪˈvɪə.rəns/", partOfSpeech: "n", meaningVi: "sự bền chí, lòng kiên trì vượt khó", exampleEn: "Success comes with perseverance.", exampleVi: "Thành công đến cùng sự kiên trì bền bỉ.", synonyms: ["persistence", "tenacity"] },
  { wordEn: "lucid", phonetic: "/ˈluː.sɪd/", partOfSpeech: "adj", meaningVi: "rõ ràng, minh bạch, dễ hiểu", exampleEn: "His explanation was lucid.", exampleVi: "Lời giải thích của ông ấy rất rõ ràng.", synonyms: ["clear", "coherent"] },
  { wordEn: "sustainable", phonetic: "/səˈsteɪ.nə.bəl/", partOfSpeech: "adj", meaningVi: "bền vững, thân thiện môi trường", exampleEn: "Sustainable energy is essential.", exampleVi: "Năng lượng bền vững là điều thiết yếu.", synonyms: ["renewable", "eco-friendly"] },
  { wordEn: "infrastructure", phonetic: "/ˈɪn.frəˌstrʌk.tʃər/", partOfSpeech: "n", meaningVi: "cơ sở hạ tầng kỹ thuật", exampleEn: "Modern transport infrastructure helps trade.", exampleVi: "Hạ tầng giao thông hiện đại hỗ trợ thương mại.", synonyms: ["facilities"] },
  { wordEn: "distinguished", phonetic: "/dɪˈstɪŋ.ɡwɪʃt/", partOfSpeech: "adj", meaningVi: "kiệt xuất, lỗi lạc, xuất chúng", exampleEn: "A distinguished scientist gave a speech.", exampleVi: "Một nhà khoa học kiệt xuất đã phát biểu.", synonyms: ["eminent", "renowned"] },
  { wordEn: "ambiguous", phonetic: "/æmˈbɪɡ.ju.əs/", partOfSpeech: "adj", meaningVi: "mơ hồ, đa nghĩa, không rõ ràng", exampleEn: "The law was ambiguous.", exampleVi: "Điều luật này rất mơ hồ.", synonyms: ["vague", "unclear"] },
  { wordEn: "profound", phonetic: "/prəˈfaʊnd/", partOfSpeech: "adj", meaningVi: "sâu sắc, thâm thúy, to lớn", exampleEn: "A profound impact on culture.", exampleVi: "Tác động sâu sắc đến nền văn hóa.", synonyms: ["deep", "insightful"] },
  { wordEn: "indispensable", phonetic: "/ˌɪn.dɪˈspen.sə.bəl/", partOfSpeech: "adj", meaningVi: "không thể thiếu, thiết yếu", exampleEn: "Water is indispensable for life.", exampleVi: "Nước là không thể thiếu đối với sự sống.", synonyms: ["essential", "crucial"] },
  { wordEn: "curfew", phonetic: "/ˈkɜː.fjuː/", partOfSpeech: "n", meaningVi: "giờ giới nghiêm về nhà", exampleEn: "Her parents set a 10 PM curfew.", exampleVi: "Bố mẹ cô ấy đặt giờ giới nghiêm lúc 10 giờ tối.", synonyms: ["deadline"] },
  { wordEn: "generation gap", phonetic: "/ˌdʒen.əˈreɪ.ʃən ɡæp/", partOfSpeech: "n", meaningVi: "khoảng cách thế hệ", exampleEn: "Parents and teens face a generation gap.", exampleVi: "Phụ huynh và con cái đối mặt khoảng cách thế hệ.", synonyms: ["age divide"] },
  { wordEn: "heritage", phonetic: "/ˈher.ɪ.tɪdʒ/", partOfSpeech: "n", meaningVi: "di sản văn hóa, truyền thống", exampleEn: "Preserving cultural heritage is essential.", exampleVi: "Bảo tồn di sản văn hóa là việc cần thiết.", synonyms: ["legacy", "tradition"] },
  { wordEn: "compass", phonetic: "/ˈkʌm.pəs/", partOfSpeech: "n", meaningVi: "com-pa vẽ hình tròn, la bàn định hướng", exampleEn: "Use a compass to draw circles.", exampleVi: "Dùng com-pa để vẽ các hình tròn.", synonyms: ["pair of compasses"] },
  { wordEn: "calculator", phonetic: "/ˈkæl.kjə.leɪ.tər/", partOfSpeech: "n", meaningVi: "máy tính bỏ túi", exampleEn: "A scientific calculator helps in exams.", exampleVi: "Máy tính khoa học giúp ích trong các kỳ thi.", synonyms: ["adding machine"] },
  { wordEn: "uniform", phonetic: "/ˈjuː.nɪ.fɔːm/", partOfSpeech: "n", meaningVi: "bộ đồng phục học sinh / công sở", exampleEn: "Students wear clean uniforms.", exampleVi: "Học sinh mặc những bộ đồng phục sạch đẹp.", synonyms: ["costume"] },
  { wordEn: "pottery", phonetic: "/ˈpɒt.ər.i/", partOfSpeech: "n", meaningVi: "đồ gốm, nghệ thuật gốm sứ", exampleEn: "Bat Trang is famous for traditional pottery.", exampleVi: "Bát Tràng nổi tiếng với đồ gốm truyền thống.", synonyms: ["ceramics"] },
  { wordEn: "longevity", phonetic: "/lɒnˈdʒev.ə.ti/", partOfSpeech: "n", meaningVi: "tuổi thọ, sự sống lâu dài", exampleEn: "Diet plays a role in longevity.", exampleVi: "Chế độ ăn đóng vai trò với tuổi thọ.", synonyms: ["long life"] },
  { wordEn: "independent", phonetic: "/ˌɪn.dɪˈpen.dənt/", partOfSpeech: "adj", meaningVi: "độc lập, tự chủ, tự lực", exampleEn: "He became independent after university.", exampleVi: "Anh ấy trở nên tự lập sau khi tốt nghiệp đại học.", synonyms: ["self-reliant"] },
  { wordEn: "opportunity", phonetic: "/ˌɒp.əˈtʃuː.nə.ti/", partOfSpeech: "n", meaningVi: "cơ hội, thời cơ tốt", exampleEn: "Grab the opportunity to study abroad.", exampleVi: "Hãy nắm bắt cơ hội đi du học.", synonyms: ["chance", "opening"] },
  { wordEn: "confidence", phonetic: "/ˈkɒn.fɪ.dəns/", partOfSpeech: "n", meaningVi: "sự tự tin, niềm tin vào bản thân", exampleEn: "Speak English with confidence.", exampleVi: "Hãy nói tiếng Anh với sự tự tin.", synonyms: ["assurance", "belief"] }
];

// Vast bilingual mapping for everyday words
const EXTENSIVE_WORD_MAP: Record<string, [string, string, string]> = {
  // [Word, POS, Meaning]
  love: ["v, n", "yêu thương, tình cảm sâu đậm", "/lʌv/"],
  like: ["v", "thích, ưa chuộng", "/laɪk/"],
  happy: ["adj", "hạnh phúc, vui sướng", "/ˈhæp.i/"],
  sad: ["adj", "buồn rầu, đau lòng", "/sæd/"],
  angry: ["adj", "tức giận, phẫn nộ", "/ˈæŋ.ɡri/"],
  beautiful: ["adj", "xinh đẹp, tuyệt mỹ", "/ˈbjuː.tɪ.fəl/"],
  intelligent: ["adj", "thông minh, sáng dạ", "/ɪnˈtel.ɪ.dʒənt/"],
  smart: ["adj", "nhanh trí, khéo léo", "/smɑːt/"],
  good: ["adj", "tốt, giỏi, hay", "/ɡʊd/"],
  bad: ["adj", "xấu, tồi tệ", "/bæd/"],
  hard: ["adj, adv", "chăm chỉ, khó khăn, cứng", "/hɑːd/"],
  easy: ["adj", "dễ dàng, đơn giản", "/ˈiː.zi/"],
  difficult: ["adj", "khó khăn, phức tạp", "/ˈdɪf.ɪ.kəlt/"],
  fast: ["adj, adv", "nhanh chóng, thần tốc", "/fɑːst/"],
  slow: ["adj, adv", "chậm chạp, từ tốn", "/sləʊ/"],
  brave: ["adj", "dũng cảm, gan dạ", "/breɪv/"],
  kind: ["adj, n", "tử tế, nhân hậu, thể loại", "/kaɪnd/"],
  honest: ["adj", "thành thật, trung thực", "/ˈɒn.ɪst/"],
  polite: ["adj", "lịch sự, nhã nhặn", "/pəˈlaɪt/"],
  generous: ["adj", "hào phóng, rộng lượng", "/ˈdʒen.ər.əs/"],
  eat: ["v", "ăn uống, dùng bữa", "/iːt/"],
  drink: ["v, n", "uống, đồ uống", "/drɪŋk/"],
  sleep: ["v, n", "ngủ, giấc ngủ", "/sliːp/"],
  run: ["v", "chạy, vận hành", "/rʌn/"],
  walk: ["v, n", "đi bộ, dạo chơi", "/wɔːk/"],
  speak: ["v", "nói chuyện, phát biểu", "/spiːk/"],
  talk: ["v, n", "nói chuyện, đàm đạo", "/tɔːk/"],
  listen: ["v", "lắng nghe, chú ý nghe", "/ˈlɪs.ən/"],
  read: ["v", "đọc sách, xem nội dung", "/riːd/"],
  write: ["v", "viết, sáng tác", "/raɪt/"],
  see: ["v", "nhìn thấy, trông thấy", "/siː/"],
  watch: ["v, n", "xem, theo dõi, đồng hồ đeo tay", "/wɒtʃ/"],
  think: ["v", "suy nghĩ, ngẫm nghĩ", "/θɪŋk/"],
  understand: ["v", "thấu hiểu, hiểu rõ", "/ˌʌn.dəˈstænd/"],
  know: ["v", "biết, am hiểu", "/nəʊ/"],
  learn: ["v", "học tập, tiếp thu", "/lɜːn/"],
  study: ["v, n", "nghiên cứu, học hành", "/ˈstʌd.i/"],
  teach: ["v", "giảng dạy, dạy học", "/tiːtʃ/"],
  help: ["v, n", "giúp đỡ, trợ giúp", "/help/"],
  create: ["v", "tạo ra, sáng tạo", "/kriˈeɪt/"],
  build: ["v", "xây dựng, kiến thiết", "/bɪld/"],
  destroy: ["v", "phá hủy, tiêu diệt", "/dɪˈstrɔɪ/"],
  improve: ["v", "cải thiện, nâng cao", "/ɪmˈpruːv/"],
  develop: ["v", "phát triển, mở rộng", "/dɪˈvel.əp/"],
  protect: ["v", "bảo vệ, che chở", "/prəˈtekt/"],
  succeed: ["v", "thành công, thắng lợi", "/səkˈsiːd/"],
  fail: ["v", "thất bại, hỏng", "/feɪl/"],
  win: ["v, n", "chiến thắng, thắng cuộc", "/wɪn/"],
  lose: ["v", "thua cuộc, làm mất", "/luːz/"],
  house: ["n", "ngôi nhà, căn nhà", "/haʊs/"],
  home: ["n", "mái ấm, gia đình", "/həʊm/"],
  car: ["n", "xe ô tô, xe hơi", "/kɑːr/"],
  bicycle: ["n", "xe đạp", "/ˈbaɪ.sɪ.kəl/"],
  bus: ["n", "xe buýt công cộng", "/bʌs/"],
  train: ["n, v", "tàu hỏa, huấn luyện", "/treɪn/"],
  airplane: ["n", "máy bay", "/ˈeə.pleɪn/"],
  tree: ["n", "cây cối", "/triː/"],
  flower: ["n", "bông hoa", "/ˈflaʊ.ər/"],
  river: ["n", "dòng sông", "/ˈrɪv.ər/"],
  mountain: ["n", "ngọn núi", "/ˈmaʊn.tɪn/"],
  sea: ["n", "biển cả", "/siː/"],
  ocean: ["n", "đại dương bao la", "/ˈəʊ.ʃən/"],
  sun: ["n", "mặt trời", "/sʌn/"],
  moon: ["n", "mặt trăng", "/muːn/"],
  star: ["n", "ngôi sao, vì sao", "/stɑːr/"],
  book: ["n, v", "cuốn sách, đặt chỗ", "/bʊk/"],
  pen: ["n", "cây bút mực", "/pen/"],
  pencil: ["n", "bút chì", "/ˈpen.səl/"],
  table: ["n", "cái bàn, bảng biểu", "/ˈteɪ.bəl/"],
  chair: ["n", "cái ghế tựa", "/tʃeər/"],
  music: ["n", "âm nhạc, giai điệu", "/ˈmjuː.zɪk/"],
  song: ["n", "bài hát, khúc ca", "/sɒŋ/"],
  art: ["n", "nghệ thuật, hội họa", "/ɑːt/"],
  picture: ["n", "bức tranh, bức ảnh", "/ˈpɪk.tʃər/"],
  money: ["n", "tiền bạc, tài chính", "/ˈmʌn.i/"],
  price: ["n", "giá cả", "/praɪs/"],
  market: ["n", "chợ, thị trường", "/ˈmɑː.kɪt/"],
  food: ["n", "thức ăn, thực phẩm", "/fuːd/"],
  rice: ["n", "cơm, hạt gạo, lúa", "/raɪs/"],
  meat: ["n", "thịt", "/miːt/"],
  fruit: ["n", "trái cây, hoa quả", "/fruːt/"],
  vegetable: ["n", "rau củ tươi", "/ˈvedʒ.tə.bəl/"]
};

// Vietnamese to English lookup map
const VIETNAMESE_TO_ENGLISH_MAP: Record<string, string> = {
  "truong hoc": "school",
  "giao vien": "teacher",
  "thay co": "teacher",
  "hoc sinh": "student",
  "ban be": "friend",
  "gia dinh": "family",
  "sach": "book",
  "but": "pen",
  "bai tap": "homework",
  "bai thi": "exam",
  "kiem tra": "test",
  "tri thuc": "knowledge",
  "kien thuc": "knowledge",
  "giao duc": "education",
  "luyen tap": "practice",
  "thu vien": "library",
  "ngon ngu": "language",
  "tu vung": "vocabulary",
  "ngu phap": "grammar",
  "phat am": "pronunciation",
  "yeu": "love",
  "thich": "like",
  "hoc": "learn",
  "nghien cuu": "study",
  "doc": "read",
  "viet": "write",
  "nghe": "listen",
  "noi": "speak",
  "giao tiep": "communicate",
  "nho": "remember",
  "quen": "forget",
  "hieu": "understand",
  "sang tao": "create",
  "giup do": "help",
  "tien bo": "improve",
  "thanh cong": "succeed",
  "kien tri": "perseverance",
  "ben bi": "perseverance",
  "cong hien": "dedicate",
  "kiet xuat": "distinguished",
  "ro rang": "lucid",
  "ben vung": "sustainable",
  "ha tang": "infrastructure",
  "co so ha tang": "infrastructure",
  "thoi tiet": "weather",
  "nuoc": "water",
  "thanh pho": "city",
  "nong thon": "countryside",
  "cong nghe": "technology",
  "may tinh": "computer",
  "dien thoai": "phone",
  "internet": "internet",
  "the gioi": "world",
  "cuoc song": "life",
  "thoi gian": "time",
  "thien nhien": "nature",
  "moi truong": "environment",
  "suc khoe": "health",
  "vui ve": "happy",
  "buon": "sad",
  "dep": "beautiful",
  "thong minh": "intelligent",
  "nhanh": "fast",
  "cham": "slow",
  "de": "easy",
  "kho": "difficult",
  "an": "eat",
  "uong": "drink",
  "ngu": "sleep",
  "chay": "run",
  "di": "go",
  "den": "come",
  "nha": "house",
  "xe": "car",
  "xe dap": "bicycle",
  "may bay": "airplane",
  "am nhac": "music",
  "tien": "money",
  "thuc an": "food"
};

function generateApproximatedIPA(word: string): string {
  const ipa = word
    .toLowerCase()
    .replace(/ph/g, 'f')
    .replace(/sh/g, 'ʃ')
    .replace(/ch/g, 'tʃ')
    .replace(/th/g, 'θ')
    .replace(/ee/g, 'iː')
    .replace(/oo/g, 'uː')
    .replace(/tion/g, 'ʃən')
    .replace(/sion/g, 'ʒən');
  return `/${ipa}/`;
}

// In-memory cache for online lookups
const ONLINE_CACHE: Record<string, DictionaryEntry> = {};

/**
 * Intelligent Universal Morphological Analyzer for ANY English or Vietnamese word
 */
export function synthesizeFallbackEntry(query: string, isEnglishToVietnamese: boolean): DictionaryEntry {
  const clean = query.trim();
  const lower = clean.toLowerCase();

  if (isEnglishToVietnamese) {
    // Check extensive map first
    if (EXTENSIVE_WORD_MAP[lower]) {
      const [pos, meaning, ipa] = EXTENSIVE_WORD_MAP[lower];
      return {
        wordEn: clean,
        phonetic: ipa,
        partOfSpeech: pos,
        meaningVi: meaning,
        exampleEn: `We can use the word '${clean}' naturally in English sentences.`,
        exampleVi: `Chúng ta có thể sử dụng từ '${clean}' một cách tự nhiên trong câu tiếng Anh.`,
        synonyms: [`related_${lower}`]
      };
    }

    // Morphological analyzer for prefixes and suffixes
    let pos = 'n';
    let meaning = `từ vựng biểu thị khái niệm '${clean}'`;

    if (lower.endsWith('tion') || lower.endsWith('sion')) {
      pos = 'n';
      meaning = `sự/quá trình '${lower.replace(/tion$|sion$/, '')}', hành động liên quan`;
    } else if (lower.endsWith('ment')) {
      pos = 'n';
      meaning = `sự/kết quả thực hiện '${lower.replace(/ment$/, '')}'`;
    } else if (lower.endsWith('ness')) {
      pos = 'n';
      meaning = `tính chất/trạng thái '${lower.replace(/ness$/, '')}'`;
    } else if (lower.endsWith('ity') || lower.endsWith('ty')) {
      pos = 'n';
      meaning = `đặc tính/khả năng mang tính chất '${lower.replace(/ity$|ty$/, '')}'`;
    } else if (lower.endsWith('er') || lower.endsWith('or')) {
      pos = 'n';
      meaning = `người hoặc vật thực hiện hành động '${lower.replace(/er$|or$/, '')}'`;
    } else if (lower.endsWith('ist')) {
      pos = 'n';
      meaning = `chuyên gia hoặc người theo đuổi lĩnh vực '${lower.replace(/ist$/, '')}'`;
    } else if (lower.endsWith('able') || lower.endsWith('ible')) {
      pos = 'adj';
      meaning = `có thể '${lower.replace(/able$|ible$/, '')}' được, mang tính khả thi`;
    } else if (lower.endsWith('ful')) {
      pos = 'adj';
      meaning = `tràn đầy, mang nhiều tính chất '${lower.replace(/ful$/, '')}'`;
    } else if (lower.endsWith('less')) {
      pos = 'adj';
      meaning = `không có, thiếu vắng '${lower.replace(/less$/, '')}'`;
    } else if (lower.endsWith('ous') || lower.endsWith('ious')) {
      pos = 'adj';
      meaning = `mang tính chất, đặc trưng của '${lower.replace(/ous$|ious$/, '')}'`;
    } else if (lower.endsWith('ly')) {
      pos = 'adv';
      meaning = `một cách '${lower.replace(/ly$/, '')}'`;
    } else if (lower.endsWith('ize') || lower.endsWith('ise')) {
      pos = 'v';
      meaning = `thực hiện hóa, biến thành '${lower.replace(/ize$|ise$/, '')}'`;
    } else if (lower.startsWith('un') || lower.startsWith('in') || lower.startsWith('im') || lower.startsWith('dis')) {
      pos = 'adj, v';
      meaning = `không '${lower.replace(/^un|^in|^im|^dis/, '')}', phủ định hoặc trái ngược`;
    } else if (lower.startsWith('re')) {
      pos = 'v';
      meaning = `làm lại, tái lập '${lower.replace(/^re/, '')}'`;
    }

    return {
      wordEn: clean,
      phonetic: generateApproximatedIPA(lower),
      partOfSpeech: pos,
      meaningVi: meaning,
      exampleEn: `You should learn how to use '${clean}' correctly in communication.`,
      exampleVi: `Bạn nên học cách sử dụng từ '${clean}' một cách chính xác trong giao tiếp.`,
      synonyms: [`related_${lower}`]
    };
  } else {
    // VIETNAMESE TO ENGLISH
    const norm = removeDiacritics(lower);
    let enWord = norm.replace(/\s+/g, '_');
    let pos = 'n, v';
    let meaningExpl = `từ tiếng Anh tương đương với '${clean}'`;

    if (VIETNAMESE_TO_ENGLISH_MAP[norm]) {
      enWord = VIETNAMESE_TO_ENGLISH_MAP[norm];
      const match = EXTENSIVE_WORD_MAP[enWord];
      if (match) {
        pos = match[0];
        meaningExpl = match[1];
      }
    } else {
      // Search partial match in maps
      for (const [vKey, eVal] of Object.entries(VIETNAMESE_TO_ENGLISH_MAP)) {
        if (norm.includes(vKey) || vKey.includes(norm)) {
          enWord = eVal;
          break;
        }
      }
    }

    const ipa = EXTENSIVE_WORD_MAP[enWord] ? EXTENSIVE_WORD_MAP[enWord][2] : generateApproximatedIPA(enWord);

    return {
      wordEn: enWord,
      phonetic: ipa,
      partOfSpeech: pos,
      meaningVi: `${clean} (${meaningExpl})`,
      exampleEn: `In English, '${clean}' can be translated as '${enWord}'.`,
      exampleVi: `Trong tiếng Anh, '${clean}' được diễn đạt là '${enWord}'.`,
      synonyms: [enWord]
    };
  }
}

/**
 * Universal online & offline search:
 * Returns instant matches, and initiates async online dictionary lookup if needed!
 */
export async function lookupUniversalOnline(query: string, isEnglishToVietnamese: boolean): Promise<DictionaryEntry | null> {
  const clean = query.trim();
  if (!clean) return null;

  const cacheKey = `${isEnglishToVietnamese ? 'en' : 'vi'}_${clean.toLowerCase()}`;
  if (ONLINE_CACHE[cacheKey]) {
    return ONLINE_CACHE[cacheKey];
  }

  try {
    // Detect if input has Vietnamese diacritics
    const hasVietnameseChars = /[àáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ]/i.test(clean);
    const effectiveIsEnToVi = hasVietnameseChars ? false : isEnglishToVietnamese;

    if (effectiveIsEnToVi) {
      // 1. ENGLISH TO VIETNAMESE LOOKUP
      let meaningVi = '';
      let phonetic = generateApproximatedIPA(clean);
      let pos = 'n';
      let defEn = '';
      let syns: string[] = [];
      let exampleEn = `We often use '${clean}' in everyday English communication.`;
      let exampleVi = `Chúng ta thường sử dụng từ '${clean}' trong giao tiếp tiếng Anh hàng ngày.`;

      // Parallel fetch: Google Translate (for accurate Vietnamese meaning) + Free Dictionary API (for IPA, English definitions & synonyms)
      const gTranslatePromise = fetch(
        `https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=vi&dt=t&q=${encodeURIComponent(clean)}`
      )
        .then(async (res) => {
          if (res.ok) {
            const data = await res.json();
            if (Array.isArray(data) && Array.isArray(data[0]) && data[0][0] && data[0][0][0]) {
              return String(data[0][0][0]).trim();
            }
          }
          return null;
        })
        .catch(() => null);

      const dictApiPromise = fetch(
        `https://api.dictionaryapi.dev/api/v2/entries/en/${encodeURIComponent(clean.toLowerCase())}`
      )
        .then(async (res) => {
          if (res.ok) {
            const data = await res.json();
            if (Array.isArray(data) && data[0]) {
              return data[0];
            }
          }
          return null;
        })
        .catch(() => null);

      const [gMeaning, dictData] = await Promise.all([gTranslatePromise, dictApiPromise]);

      if (gMeaning) {
        meaningVi = gMeaning.toLowerCase();
      }

      if (dictData) {
        phonetic = dictData.phonetic || dictData.phonetics?.find((p: { text?: string }) => p.text)?.text || phonetic;
        if (dictData.meanings?.[0]) {
          pos = dictData.meanings[0].partOfSpeech || pos;
          defEn = dictData.meanings[0].definitions?.[0]?.definition || '';
          if (dictData.meanings[0].definitions?.[0]?.example) {
            exampleEn = dictData.meanings[0].definitions[0].example;
            exampleVi = `Ví dụ: "${exampleEn}"`;
          }
          syns = (dictData.meanings[0].synonyms || []).slice(0, 4);
        }
      }

      // Fallback to MyMemory if Google Translate was unreachable
      if (!meaningVi) {
        try {
          const transRes = await fetch(
            `https://api.mymemory.translated.net/get?q=${encodeURIComponent(clean)}&langpair=en|vi`
          );
          if (transRes.ok) {
            const transData = await transRes.json();
            if (transData.responseData?.translatedText) {
              meaningVi = transData.responseData.translatedText.toLowerCase();
            }
          }
        } catch {}
      }

      // If still missing, check offline extensive maps and morphological rules
      if (!meaningVi && EXTENSIVE_WORD_MAP[clean.toLowerCase()]) {
        const item = EXTENSIVE_WORD_MAP[clean.toLowerCase()];
        pos = item[0];
        meaningVi = item[1];
        phonetic = item[2];
      }

      if (!meaningVi) {
        const synth = synthesizeFallbackEntry(clean, true);
        meaningVi = defEn || synth.meaningVi;
      }

      const entry: DictionaryEntry = {
        wordEn: clean,
        phonetic,
        partOfSpeech: pos,
        meaningVi: meaningVi || clean,
        definitionEn: defEn || undefined,
        exampleEn,
        exampleVi: meaningVi ? `Giải nghĩa: '${clean}' có nghĩa là '${meaningVi}'.` : exampleVi,
        synonyms: syns.length > 0 ? syns : [`related_${clean.toLowerCase()}`]
      };

      ONLINE_CACHE[cacheKey] = entry;
      return entry;
    } else {
      // 2. VIETNAMESE TO ENGLISH LOOKUP
      let translatedEn = '';

      // Google Translate VI -> EN
      try {
        const gRes = await fetch(
          `https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=en&dt=t&q=${encodeURIComponent(clean)}`
        );
        if (gRes.ok) {
          const gData = await gRes.json();
          if (Array.isArray(gData) && Array.isArray(gData[0]) && gData[0][0] && gData[0][0][0]) {
            translatedEn = String(gData[0][0][0]).trim();
          }
        }
      } catch {}

      // Fallback MyMemory VI -> EN
      if (!translatedEn) {
        try {
          const transRes = await fetch(
            `https://api.mymemory.translated.net/get?q=${encodeURIComponent(clean)}&langpair=vi|en`
          );
          if (transRes.ok) {
            const transData = await transRes.json();
            translatedEn = transData.responseData?.translatedText || '';
          }
        } catch {}
      }

      if (!translatedEn) {
        translatedEn = synthesizeFallbackEntry(clean, false).wordEn;
      }

      const cleanEn = translatedEn.replace(/[^\w\s-]/g, '').trim() || clean;

      // Also fetch phonetic and details for the resulting English word
      let phonetic = generateApproximatedIPA(cleanEn);
      let pos = 'n, v';
      let defEn = '';
      let syns: string[] = [cleanEn];

      try {
        const dictRes = await fetch(
          `https://api.dictionaryapi.dev/api/v2/entries/en/${encodeURIComponent(cleanEn.toLowerCase())}`
        );
        if (dictRes.ok) {
          const dictData = await dictRes.json();
          if (Array.isArray(dictData) && dictData[0]) {
            phonetic = dictData[0].phonetic || dictData[0].phonetics?.find((p: { text?: string }) => p.text)?.text || phonetic;
            if (dictData[0].meanings?.[0]) {
              pos = dictData[0].meanings[0].partOfSpeech || pos;
              defEn = dictData[0].meanings[0].definitions?.[0]?.definition || '';
              syns = (dictData[0].meanings[0].synonyms || []).slice(0, 4);
            }
          }
        }
      } catch {}

      const entry: DictionaryEntry = {
        wordEn: cleanEn,
        phonetic,
        partOfSpeech: pos,
        meaningVi: clean,
        definitionEn: defEn || undefined,
        exampleEn: `In English, '${clean}' is translated as '${cleanEn}'.`,
        exampleVi: `Trong tiếng Anh, '${clean}' được diễn đạt là '${cleanEn}'.`,
        synonyms: syns.length > 0 ? syns : [cleanEn]
      };

      ONLINE_CACHE[cacheKey] = entry;
      return entry;
    }
  } catch (err) {
    console.warn("Universal online lookup fallback:", err);
    return synthesizeFallbackEntry(query, isEnglishToVietnamese);
  }
}

/**
 * Fast synchronous lookup combining exact match, partial match, and morphological synthesis
 */
export function lookupDictionary(query: string, isEnglishToVietnamese: boolean): DictionaryEntry[] {
  const q = query.trim().toLowerCase();
  if (!q) {
    return BASE_DICTIONARY.slice(0, 20);
  }

  const normQuery = removeDiacritics(q);

  // 1. Matches in base dictionary
  const matches = BASE_DICTIONARY.filter(entry => {
    const normMeaning = removeDiacritics(entry.meaningVi);
    const normWord = entry.wordEn.toLowerCase();

    if (isEnglishToVietnamese) {
      return (
        normWord === q ||
        normWord.startsWith(q) ||
        normWord.includes(q) ||
        entry.synonyms.some(s => s.toLowerCase().includes(q)) ||
        normMeaning.includes(normQuery)
      );
    } else {
      return (
        normMeaning.includes(normQuery) ||
        entry.meaningVi.toLowerCase().includes(q) ||
        normWord.includes(q)
      );
    }
  }).sort((a, b) => {
    const targetA = isEnglishToVietnamese ? a.wordEn.toLowerCase() : removeDiacritics(a.meaningVi);
    const targetB = isEnglishToVietnamese ? b.wordEn.toLowerCase() : removeDiacritics(b.meaningVi);
    const probe = isEnglishToVietnamese ? q : normQuery;

    const scoreA = targetA === probe ? 4 : targetA.startsWith(probe) ? 3 : 1;
    const scoreB = targetB === probe ? 4 : targetB.startsWith(probe) ? 3 : 1;
    return scoreB - scoreA;
  });

  // Check extensive map if not found in base dictionary
  if (matches.length === 0 && EXTENSIVE_WORD_MAP[q] && isEnglishToVietnamese) {
    const [pos, meaning, ipa] = EXTENSIVE_WORD_MAP[q];
    matches.push({
      wordEn: query.trim(),
      phonetic: ipa,
      partOfSpeech: pos,
      meaningVi: meaning,
      exampleEn: `The word '${query.trim()}' is widely used in standard English.`,
      exampleVi: `Từ '${query.trim()}' được sử dụng phổ biến trong tiếng Anh chuẩn.`,
      synonyms: [`related_${q}`]
    });
  }

  if (matches.length > 0) {
    return matches;
  }

  // Universal Fallback Synthesis for ANY word
  return [synthesizeFallbackEntry(query, isEnglishToVietnamese)];
}
