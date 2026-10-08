import { GrammarLesson, Subject } from '../types';

export const GRAMMAR_DATABASE: Record<number, GrammarLesson[]> = {
  6: [
    {
      id: "g6_1",
      grade: 6,
      title: "Thì Hiện Tại Đơn (Present Simple)",
      formula: "Khẳng định: S + V(s/es) | Phủ định: S + do/does not + V-inf | Nghi vấn: Do/Does + S + V-inf?",
      explanationVi: "Dùng để diễn tả thói quen, hành động lặp đi lặp lại hoặc chân lý, sự thật hiển nhiên.",
      exampleEn: "She studies English every morning at 7 AM.",
      exampleVi: "Cô ấy học tiếng Anh vào mỗi buổi sáng lúc 7 giờ.",
      usageNotes: "Dấu hiệu: always, usually, often, sometimes, everyday."
    },
    {
      id: "g6_2",
      grade: 6,
      title: "Thì Hiện Tại Tiếp Diễn (Present Continuous)",
      formula: "S + am/is/are + V-ing",
      explanationVi: "Diễn tả hành động đang xảy ra tại thời điểm nói hoặc kế hoạch chắc chắn trong tương lai gần.",
      exampleEn: "They are playing football right now.",
      exampleVi: "Họ đang chơi bóng đá ngay lúc này.",
      usageNotes: "Dấu hiệu: now, at the moment, at present, Look!, Listen!"
    }
  ],
  7: [
    {
      id: "g7_1",
      grade: 7,
      title: "Thì Quá Khứ Đơn (Past Simple)",
      formula: "Động từ to-be: S + was/were | Động từ thường: S + V2/ed",
      explanationVi: "Diễn tả hành động đã xảy ra và chấm dứt hoàn toàn trong quá khứ.",
      exampleEn: "We visited Ha Long Bay last summer.",
      exampleVi: "Chúng tôi đã đi thăm Vịnh Hạ Long vào mùa hè năm ngoái.",
      usageNotes: "Dấu hiệu: yesterday, ago, last week/year, in 2020."
    },
    {
      id: "g7_2",
      grade: 7,
      title: "Câu Mệnh Lệnh & Lời Khuyên Sức Khỏe",
      formula: "Do more exercise! / Don't eat too much junk food! / You should + V-inf",
      explanationVi: "Dùng để đưa ra lời hướng dẫn hoặc lời khuyên bảo vệ sức khỏe cho học sinh.",
      exampleEn: "You should drink plenty of water everyday.",
      exampleVi: "Bạn nên uống nhiều nước mỗi ngày.",
      usageNotes: "Should = nên; Shouldn't = không nên."
    }
  ],
  8: [
    {
      id: "g8_1",
      grade: 8,
      title: "So Sánh Hơn Của Trạng Từ (Comparative Adverbs)",
      formula: "Ngắn: S1 + V + adv-er + than + S2 | Dài: S1 + V + more + adv + than + S2",
      explanationVi: "Dùng để so sánh mức độ, tốc độ hay cách thức thực hiện hành động giữa hai đối tượng.",
      exampleEn: "A horse runs faster than a camel.",
      exampleVi: "Một con ngựa chạy nhanh hơn một con lạc đà.",
      usageNotes: "Ngoại lệ: well -> better, badly -> worse, far -> farther/further."
    },
    {
      id: "g8_2",
      grade: 8,
      title: "Câu Phức Với Liên Từ Chỉ Thời Gian & Nhượng Bộ",
      formula: "Although/Even though + S + V, S + V | Because/Since + S + V, S + V",
      explanationVi: "Nối các mệnh đề chỉ sự tương phản, đối lập hoặc nguyên nhân kết quả.",
      exampleEn: "Although it rained heavily, we still went camping.",
      exampleVi: "Mặc dù trời mưa to, chúng tôi vẫn đi cắm trại.",
      usageNotes: "Không dùng 'Although' đi kèm với 'But' trong cùng một câu."
    }
  ],
  9: [
    {
      id: "g9_1",
      grade: 9,
      title: "Cụm Động Từ Thông Dụng (Phrasal Verbs)",
      formula: "Verb + Preposition / Particle (look after, set off, pass down, turn down)",
      explanationVi: "Sự kết hợp giữa động từ và giới từ tạo nên nghĩa mới hoàn toàn.",
      exampleEn: "Traditional craft skills are passed down from generation to generation.",
      exampleVi: "Các kỹ năng thủ công truyền thống được truyền lại từ đời này sang đời khác.",
      usageNotes: "Pass down = lưu truyền; Set off = khởi hành; Look after = chăm sóc."
    },
    {
      id: "g9_2",
      grade: 9,
      title: "Mệnh Đề Quan Hệ Xác Định & Không Xác Định",
      formula: "Who (chỉ người làm S/O), Which (chỉ vật), Whose (sở hữu), That",
      explanationVi: "Bổ nghĩa cho danh từ đứng trước, giúp câu văn cô đọng và liên kết chặt chẽ.",
      exampleEn: "The artisan who made this vase lives in Bat Trang village.",
      exampleVi: "Nghệ nhân người làm ra chiếc bình này sống ở làng Bát Tràng.",
      usageNotes: "Mệnh đề không xác định có dấu phẩy và KHÔNG được dùng 'that'."
    }
  ],
  10: [
    {
      id: "g10_1",
      grade: 10,
      title: "Thì Hiện Tại Đơn vs Hiện Tại Tiếp Diễn (Family Life)",
      formula: "Hiện tại đơn: S + V(s/es) | Hiện tại tiếp diễn: S + is/am/are + V-ing",
      explanationVi: "Phân biệt thói quen phân chia việc nhà thường nhật và hành động đang diễn ra tại thời điểm nói.",
      exampleEn: "My mother usually cooks dinner, but today my father is preparing the meal.",
      exampleVi: "Mẹ tôi thường nấu cơm tối, nhưng hôm nay bố tôi đang chuẩn bị bữa ăn.",
      usageNotes: "Không dùng thì tiếp diễn với động từ tri giác: know, believe, like, understand."
    },
    {
      id: "g10_2",
      grade: 10,
      title: "Câu Bị Động Với Động Từ Khuyết Thiếu (Passive Modals)",
      formula: "S + modal verb (can/must/should/will) + be + V3/ed",
      explanationVi: "Nhấn mạnh vào hành động bảo vệ môi trường thay vì người thực hiện.",
      exampleEn: "Plastic bags should be replaced by reusable canvas bags.",
      exampleVi: "Túi nilon nên được thay thế bằng túi vải tái sử dụng.",
      usageNotes: "By + tân ngữ có thể lược bỏ nếu chủ thể không xác định (someone, people)."
    },
    {
      id: "g10_3",
      grade: 10,
      title: "To-Infinitive và Bare Infinitive (Âm Nhạc & Thần Tượng)",
      formula: "Verb + to-V (want, decide, hope, promise) | Verb + V-bare (make, let, see, hear)",
      explanationVi: "Quy tắc sử dụng động từ nguyên mẫu có 'to' hoặc không 'to' sau các động từ chính.",
      exampleEn: "The singer decided to release her new album next month.",
      exampleVi: "Nữ ca sĩ đã quyết định phát hành album mới vào tháng tới.",
      usageNotes: "Make someone do something; Help someone (to) do something."
    }
  ],
  11: [
    {
      id: "g11_1",
      grade: 11,
      title: "Mạo Từ A / An / The & Zero Article (Lối Sống Lành Mạnh)",
      formula: "A/An + N đếm được số ít chưa xác định | The + N đã xác định / duy nhất",
      explanationVi: "Cách dùng mạo từ với các danh từ chỉ bệnh tật, hệ cơ quan cơ thể và bữa ăn.",
      exampleEn: "The immune system plays a crucial role in defending against diseases.",
      exampleVi: "Hệ miễn dịch đóng vai trò quan trọng trong việc phòng chống bệnh tật.",
      usageNotes: "Dùng 'the' với phát minh, nhạc cụ và các tổ chức độc nhất: the internet, the sun."
    },
    {
      id: "g11_2",
      grade: 11,
      title: "Động Từ Khuyết Thiếu Bắt Buộc & Cấm Đoán (Must vs Should vs Ought to)",
      formula: "Must / Have to: Bắt buộc | Mustn't: Cấm tiệt | Should / Ought to: Khuyên bảo",
      explanationVi: "Biểu thị các quy định trong gia đình, khoảng cách thế hệ và giờ giới nghiêm của phụ huynh.",
      exampleEn: "Teenagers must not break the curfew set by their parents.",
      exampleVi: "Thanh thiếu niên không được phá vỡ giờ giới nghiêm do cha mẹ đặt ra.",
      usageNotes: "Must = xuất phát từ người nói; Have to = do hoàn cảnh khách quan."
    },
    {
      id: "g11_3",
      grade: 11,
      title: "Thì Hiện Tại Hoàn Thành Tiếp Diễn (Present Perfect Continuous)",
      formula: "S + have/has + been + V-ing",
      explanationVi: "Nhấn mạnh tính liên tục của hành động bắt đầu ở quá khứ và vẫn đang tiếp diễn ở hiện tại.",
      exampleEn: "Scientists have been researching smart cities for over a decade.",
      exampleVi: "Các nhà khoa học đã và đang nghiên cứu về đô thị thông minh trong hơn một thập kỷ qua.",
      usageNotes: "Dấu hiệu: for, since, all day, lately, recently."
    }
  ],
  12: [
    {
      id: "g12_1",
      grade: 12,
      title: "Đảo Ngữ Với Phó Từ Phủ Định (Inversion - Ôn Thi HSG & THPT)",
      formula: "Seldom / Rarely / Never / Hardly + Trợ Động Từ + S + V-inf",
      explanationVi: "Cấu trúc nâng cao tạo ấn tượng mạnh mẽ trong bài thi học sinh giỏi và nghị luận tiếng Anh.",
      exampleEn: "Seldom had he witnessed such unshakeable perseverance and dedication.",
      exampleVi: "Hiếm khi nào ông lại chứng kiến lòng kiên trì và sự cống hiến kiên định đến như vậy.",
      usageNotes: "Đảo trợ động từ lên trước chủ ngữ giống như cấu trúc câu hỏi."
    },
    {
      id: "g12_2",
      grade: 12,
      title: "Câu Điều Kiện Hỗn Hợp (Mixed Conditionals 3 - 2)",
      formula: "If + S + had + V3/ed (quá khứ), S + would/could + V-inf (hiện tại)",
      explanationVi: "Giả định một hành động trái thực tế trong quá khứ dẫn đến kết quả ở hiện tại.",
      exampleEn: "If she had taken the scholarship then, she would be studying in Oxford now.",
      exampleVi: "Nếu lúc đó cô ấy nhận học bổng, thì bây giờ cô ấy đang học tại Oxford rồi.",
      usageNotes: "Thường có trạng từ 'then', 'in the past' ở vế If và 'now', 'today' ở vế chính."
    },
    {
      id: "g12_3",
      grade: 12,
      title: "Thể Giả Định (Subjunctive Mood)",
      formula: "It is essential/crucial/vital that + S + (should) + V-inf",
      explanationVi: "Dùng trong các văn bản trang trọng khi bàn về chính sách môi trường, giáo dục và công nghệ.",
      exampleEn: "It is essential that every country take prompt action on climate change.",
      exampleVi: "Điều tối cần thiết là mỗi quốc gia cần hành động kịp thời về biến đổi khí hậu.",
      usageNotes: "Động từ luôn ở dạng nguyên mẫu không chia dù chủ ngữ là số ít (he, she, it)."
    }
  ]
};

export const BASE_SUBJECTS_BY_GRADE: Record<number, Subject[]> = {
  6: [
    {
      id: "sub_grade_6",
      grade: 6,
      title: "Tiếng Anh Lớp 6",
      subtitle: "Chương trình Global Success & GDPT mới",
      iconCategory: "school",
      units: [
        {
          id: "g6_u1",
          grade: 6,
          unitNumber: 1,
          title: "Unit 1: My New School",
          description: "Ngôi trường mới, đồ dùng học tập và hoạt động hàng ngày",
          difficulty: "Cơ bản",
          words: [
            { id: "g6_w1", word: "Compass", phonetic: "/ˈkʌm.pəs/", meaningVi: "Com-pa dùng vẽ hình tròn", exampleEn: "I use a compass in maths class.", exampleVi: "Tôi dùng com-pa trong giờ toán.", grade: 6, unitNumber: 1, distractorsVi: ["Thước kẻ dẻo", "Hộp bút màu", "Cục tẩy mực"] },
            { id: "g6_w2", word: "Calculator", phonetic: "/ˈkæl.kjə.leɪ.tər/", meaningVi: "Máy tính bỏ túi", exampleEn: "Bring your calculator to school.", exampleVi: "Hãy mang máy tính bỏ túi đến trường.", grade: 6, unitNumber: 1, distractorsVi: ["Bảng viết phấn", "Bút chì kim", "Cặp sách mới"] },
            { id: "g6_w3", word: "Uniform", phonetic: "/ˈjuː.nɪ.fɔːm/", meaningVi: "Bộ đồng phục học sinh", exampleEn: "We wear school uniforms on Mondays.", exampleVi: "Chúng tôi mặc đồng phục vào các ngày thứ Hai.", grade: 6, unitNumber: 1, distractorsVi: ["Áo khoác gió", "Giày thể thao", "Mũ lưỡi trai"] },
            { id: "g6_w4", word: "Textbook", phonetic: "/ˈtekst.bʊk/", meaningVi: "Sách giáo khoa", exampleEn: "Open your English textbook to page 10.", exampleVi: "Mở sách giáo khoa tiếng Anh trang 10.", grade: 6, unitNumber: 1, distractorsVi: ["Vở ghi chép", "Tập giấy nháp", "Từ điển mini"] }
          ],
          grammarLesson: GRAMMAR_DATABASE[6][0],
          bestScore: 1500,
          isCompleted: true
        },
        {
          id: "g6_u2",
          grade: 6,
          unitNumber: 2,
          title: "Unit 2: My House",
          description: "Các phòng trong nhà, đồ nội thất và giới từ chỉ vị trí",
          difficulty: "Cơ bản",
          words: [
            { id: "g6_w5", word: "Microwave", phonetic: "/ˈmaɪ.krə.weɪv/", meaningVi: "Lò vi sóng hâm nóng thức ăn", exampleEn: "Heat the soup in the microwave.", exampleVi: "Hâm nóng canh trong lò vi sóng.", grade: 6, unitNumber: 2, distractorsVi: ["Tủ lạnh hai cánh", "Máy giặt tự động", "Bếp ga đôi"] },
            { id: "g6_w6", word: "Wardrobe", phonetic: "/ˈwɔː.drəʊb/", meaningVi: "Tủ đựng quần áo", exampleEn: "Put your clothes into the wardrobe.", exampleVi: "Cất quần áo của bạn vào tủ.", grade: 6, unitNumber: 2, distractorsVi: ["Bàn học gỗ", "Giường tầng", "Kệ để sách"] }
          ],
          grammarLesson: GRAMMAR_DATABASE[6][1],
          bestScore: 0,
          isCompleted: false
        }
      ]
    }
  ],
  7: [
    {
      id: "sub_grade_7",
      grade: 7,
      title: "Tiếng Anh Lớp 7",
      subtitle: "Sở thích, lối sống lành mạnh và phục vụ cộng đồng",
      iconCategory: "school",
      units: [
        {
          id: "g7_u1",
          grade: 7,
          unitNumber: 1,
          title: "Unit 1: Hobbies",
          description: "Các sở thích độc đáo, làm đồ gốm và trượt băng",
          difficulty: "Cơ bản",
          words: [
            { id: "g7_w1", word: "Pottery", phonetic: "/ˈpɒt.ər.i/", meaningVi: "Đồ gốm, nghề làm gốm thủ công", exampleEn: "Making pottery is relaxing.", exampleVi: "Làm đồ gốm rất thư giãn.", grade: 7, unitNumber: 1, distractorsVi: ["Vẽ tranh sơn dầu", "Đan lát len", "Sưu tầm tem cổ"] },
            { id: "g7_w2", word: "Ice-skating", phonetic: "/ˈaɪsˌskeɪ.tɪŋ/", meaningVi: "Môn trượt băng nghệ thuật", exampleEn: "She goes ice-skating at weekends.", exampleVi: "Cô ấy đi trượt băng vào cuối tuần.", grade: 7, unitNumber: 1, distractorsVi: ["Bơi lội tự do", "Leo núi mạo hiểm", "Trượt patin đường phố"] }
          ],
          grammarLesson: GRAMMAR_DATABASE[7][0],
          bestScore: 1620,
          isCompleted: true
        },
        {
          id: "g7_u2",
          grade: 7,
          unitNumber: 2,
          title: "Unit 2: Healthy Living",
          description: "Sức khỏe tuổi mới lớn, dị ứng và chế độ ăn cân bằng",
          difficulty: "Trung cấp",
          words: [
            { id: "g7_w3", word: "Allergy", phonetic: "/ˈæl.ə.dʒi/", meaningVi: "Bệnh dị ứng thời tiết / thực phẩm", exampleEn: "He has a seafood allergy.", exampleVi: "Anh ấy bị dị ứng hải sản.", grade: 7, unitNumber: 2, distractorsVi: ["Cảm cúm thông thường", "Đau đầu dữ dội", "Sốt phát ban"] },
            { id: "g7_w4", word: "Sunburn", phonetic: "/ˈsʌn.bɜːn/", meaningVi: "Vết cháy nắng, bỏng nắng", exampleEn: "Wear sunscreen to prevent sunburn.", exampleVi: "Thoa kem chống nắng để tránh cháy nắng.", grade: 7, unitNumber: 2, distractorsVi: ["Nổi mề đay", "Vết trầy xước", "Cảm lạnh"] }
          ],
          grammarLesson: GRAMMAR_DATABASE[7][1],
          bestScore: 0,
          isCompleted: false
        }
      ]
    }
  ],
  8: [
    {
      id: "sub_grade_8",
      grade: 8,
      title: "Tiếng Anh Lớp 8",
      subtitle: "Nông thôn, thế hệ tuổi teen và phong tục tập quán",
      iconCategory: "school",
      units: [
        {
          id: "g8_u1",
          grade: 8,
          unitNumber: 1,
          title: "Unit 1: Life in the Countryside",
          description: "Cuộc sống đồng quê thanh bình, vụ mùa và chăn nuôi",
          difficulty: "Trung cấp",
          words: [
            { id: "g8_w1", word: "Harvest", phonetic: "/ˈhɑː.vɪst/", meaningVi: "Vụ thu hoạch mùa màng", exampleEn: "Farmers work hard during harvest time.", exampleVi: "Bà con nông dân làm việc chăm chỉ trong mùa gặt.", grade: 8, unitNumber: 1, distractorsVi: ["Mùa gieo hạt giống", "Mùa cấy lúa", "Mùa mưa lũ"] },
            { id: "g8_w2", word: "Paddy field", phonetic: "/ˈpæd.i fiːld/", meaningVi: "Cánh đồng lúa bạt ngàn", exampleEn: "Children fly kites near the paddy field.", exampleVi: "Trẻ em thả diều gần cánh đồng lúa.", grade: 8, unitNumber: 1, distractorsVi: ["Vườn cây ăn trái", "Nương rẫy bậc thang", "Khu rừng nguyên sinh"] },
            { id: "g8_w3", word: "Peaceful", phonetic: "/ˈpiːs.fəl/", meaningVi: "Yên bình, thanh thản", exampleEn: "The countryside is quiet and peaceful.", exampleVi: "Vùng quê thật yên tĩnh và thanh bình.", grade: 8, unitNumber: 1, distractorsVi: ["Ồn ào náo nhiệt", "Nguy hiểm rình rập", "Tấp nập đông đúc"] }
          ],
          grammarLesson: GRAMMAR_DATABASE[8][0],
          bestScore: 1750,
          isCompleted: true
        },
        {
          id: "g8_u2",
          grade: 8,
          unitNumber: 2,
          title: "Unit 2: Teenagers & Pressure",
          description: "Áp lực học tập, diễn đàn và mạng xã hội",
          difficulty: "Trung cấp",
          words: [
            { id: "g8_w4", word: "Peer pressure", phonetic: "/ˈpɪə ˌpreʃ.ər/", meaningVi: "Áp lực từ bạn bè đồng trang lứa", exampleEn: "She faces peer pressure to fit in.", exampleVi: "Cô ấy đối mặt với áp lực đồng trang lứa để hòa nhập.", grade: 8, unitNumber: 2, distractorsVi: ["Kỳ vọng từ thầy cô", "Bất đồng gia đình", "Áp lực tài chính"] },
            { id: "g8_w5", word: "Overcome", phonetic: "/ˌəʊ.vəˈkʌm/", meaningVi: "Vượt qua khó khăn, nghịch cảnh", exampleEn: "He overcame stress with music.", exampleVi: "Cậu ấy đã vượt qua căng thẳng nhờ âm nhạc.", grade: 8, unitNumber: 2, distractorsVi: ["Đầu hàng thất bại", "Tránh né vấn đề", "Tạo thêm rắc rối"] }
          ],
          grammarLesson: GRAMMAR_DATABASE[8][1],
          bestScore: 0,
          isCompleted: false
        }
      ]
    }
  ],
  9: [
    {
      id: "sub_grade_9",
      grade: 9,
      title: "Tiếng Anh Lớp 9",
      subtitle: "Làng nghề truyền thống, đô thị và chuẩn bị thi vào 10",
      iconCategory: "school",
      units: [
        {
          id: "g9_u1",
          grade: 9,
          unitNumber: 1,
          title: "Unit 1: Local Community",
          description: "Nghệ nhân, làng nghề thủ công mỹ nghệ và bảo tồn văn hóa",
          difficulty: "Trung cấp",
          words: [
            { id: "g9_w1", word: "Artisan", phonetic: "/ˌɑː.tɪˈzæn/", meaningVi: "Nghệ nhân tay nghề cao", exampleEn: "The artisan carved a wooden statue.", exampleVi: "Nghệ nhân đã tạc một pho tượng gỗ.", grade: 9, unitNumber: 1, distractorsVi: ["Người mua hàng rong", "Hướng dẫn viên", "Khách du lịch"] },
            { id: "g9_w2", word: "Handicraft", phonetic: "/ˈhæn.dɪ.krɑːft/", meaningVi: "Đồ thủ công mỹ nghệ tinh xảo", exampleEn: "Bat Trang is famous for handicrafts.", exampleVi: "Bát Tràng nổi tiếng với các sản phẩm thủ công.", grade: 9, unitNumber: 1, distractorsVi: ["Hàng công nghiệp nặng", "Đồ điện tử thông minh", "Nông sản tươi"] },
            { id: "g9_w3", word: "Preserve", phonetic: "/prɪˈzɜːv/", meaningVi: "Gìn giữ, bảo tồn giá trị truyền thống", exampleEn: "We must preserve our traditions.", exampleVi: "Chúng ta phải gìn giữ truyền thống của mình.", grade: 9, unitNumber: 1, distractorsVi: ["Bỏ quên lãng phí", "Thương mại hóa thô bạo", "Thay đổi hoàn toàn"] }
          ],
          grammarLesson: GRAMMAR_DATABASE[9][0],
          bestScore: 1880,
          isCompleted: true
        },
        {
          id: "g9_u2",
          grade: 9,
          unitNumber: 2,
          title: "Unit 2: City Life",
          description: "Đô thị hiện đại, chi phí sinh hoạt và tắc nghẽn",
          difficulty: "Trung cấp",
          words: [
            { id: "g9_w4", word: "Metropolitan", phonetic: "/ˌmet.rəˈpɒl.ɪ.tən/", meaningVi: "Thuộc vùng đô thị lớn, thủ phủ", exampleEn: "Living in a metropolitan area has pros and cons.", exampleVi: "Sống ở vùng đại đô thị có cả ưu và nhược điểm.", grade: 9, unitNumber: 2, distractorsVi: ["Vùng nông thôn hẻo lánh", "Vùng duyên hải", "Khu bảo tồn hoang dã"] },
            { id: "g9_w5", word: "Affordable", phonetic: "/əˈfɔː.də.bəl/", meaningVi: "Giá cả phải chăng, vừa túi tiền", exampleEn: "They found an affordable apartment.", exampleVi: "Họ đã tìm được một căn hộ vừa túi tiền.", grade: 9, unitNumber: 2, distractorsVi: ["Đắt đỏ xa xỉ", "Cực kỳ khan hiếm", "Chất lượng kém"] }
          ],
          grammarLesson: GRAMMAR_DATABASE[9][1],
          bestScore: 0,
          isCompleted: false
        }
      ]
    }
  ],
  10: [
    {
      id: "sub_grade_10",
      grade: 10,
      title: "Tiếng Anh Lớp 10",
      subtitle: "Gia đình, môi trường sống, âm nhạc và cộng đồng",
      iconCategory: "school",
      units: [
        {
          id: "g10_u1",
          grade: 10,
          unitNumber: 1,
          title: "Unit 1: Family Life",
          description: "Trụ cột gia đình, phân chia việc nhà và lối sống gắn kết",
          difficulty: "Trung cấp",
          words: [
            { id: "g10_w1", word: "Breadwinner", phonetic: "/ˈbredˌwɪn.ər/", meaningVi: "Trụ cột kiếm tiền nuôi gia đình", exampleEn: "His father is the sole breadwinner.", exampleVi: "Bố của cậu ấy là trụ cột kiếm tiền duy nhất.", grade: 10, unitNumber: 1, distractorsVi: ["Người nội trợ quán xuyến", "Con một trong nhà", "Người họ hàng xa"] },
            { id: "g10_w2", word: "Homemaker", phonetic: "/ˈhəʊmˌmeɪ.kər/", meaningVi: "Người làm nội trợ chăm sóc tổ ấm", exampleEn: "Both parents can share homemaker duties.", exampleVi: "Cả bố và mẹ đều có thể chia sẻ việc nội trợ.", grade: 10, unitNumber: 1, distractorsVi: ["Trụ cột tài chính", "Khách viếng thăm", "Người làm thuê"] },
            { id: "g10_w3", word: "Heavy lifting", phonetic: "/ˌhev.i ˈlɪf.tɪŋ/", meaningVi: "Công việc nặng nhọc, mang vác", exampleEn: "He helps his mother with the heavy lifting.", exampleVi: "Anh giúp mẹ những việc nặng nhọc.", grade: 10, unitNumber: 1, distractorsVi: ["Nấu nướng nhẹ nhàng", "Rửa chén bát", "Quét dọn phòng khách"] }
          ],
          grammarLesson: GRAMMAR_DATABASE[10][0],
          bestScore: 1920,
          isCompleted: true
        },
        {
          id: "g10_u2",
          grade: 10,
          unitNumber: 2,
          title: "Unit 2: Humans and Environment",
          description: "Lối sống xanh, bảo vệ sinh thái và cắt giảm rác thải",
          difficulty: "Trung cấp",
          words: [
            { id: "g10_w4", word: "Eco-friendly", phonetic: "/ˌiː.kəʊˈfrend.li/", meaningVi: "Thân thiện với môi trường tự nhiên", exampleEn: "They use eco-friendly paper bags.", exampleVi: "Họ sử dụng túi giấy thân thiện môi trường.", grade: 10, unitNumber: 2, distractorsVi: ["Độc hại khôn lường", "Khó phân hủy sinh học", "Lãng phí tài nguyên"] },
            { id: "g10_w5", word: "Carbon footprint", phonetic: "/ˌkɑː.bən ˈfʊt.prɪnt/", meaningVi: "Dấu chân carbon, lượng khí thải", exampleEn: "Biking helps reduce your carbon footprint.", exampleVi: "Đi xe đạp giúp cắt giảm dấu chân carbon.", grade: 10, unitNumber: 2, distractorsVi: ["Nguồn nước ngầm", "Năng lượng hóa thạch", "Nhiệt độ toàn cầu"] }
          ],
          grammarLesson: GRAMMAR_DATABASE[10][1],
          bestScore: 0,
          isCompleted: false
        },
        {
          id: "g10_u3",
          grade: 10,
          unitNumber: 3,
          title: "Unit 3: Music & Arts",
          description: "Thần tượng âm nhạc, giai điệu và nhạc cụ truyền thống",
          difficulty: "Trung cấp",
          words: [
            { id: "g10_w6", word: "Talented", phonetic: "/ˈtæl.ən.tɪd/", meaningVi: "Có tài năng bẩm sinh, tài hoa", exampleEn: "She is a talented pianist.", exampleVi: "Cô ấy là một nghệ sĩ dương cầm tài năng.", grade: 10, unitNumber: 3, distractorsVi: ["Kém cỏi", "Bình thường", "Thiếu kiên nhẫn"] },
            { id: "g10_w7", word: "Audience", phonetic: "/ˈɔː.di.əns/", meaningVi: "Khán giả theo dõi buổi biểu diễn", exampleEn: "The audience clapped enthusiastically.", exampleVi: "Khán giả đã vỗ tay tán thưởng nồng nhiệt.", grade: 10, unitNumber: 3, distractorsVi: ["Ban giám khảo", "Nhạc công", "Đạo diễn"] }
          ],
          grammarLesson: GRAMMAR_DATABASE[10][2],
          bestScore: 0,
          isCompleted: false
        }
      ]
    }
  ],
  11: [
    {
      id: "sub_grade_11",
      grade: 11,
      title: "Tiếng Anh Lớp 11",
      subtitle: "Sức khỏe dẻo dai, khoảng cách thế hệ và đô thị thông minh",
      iconCategory: "school",
      units: [
        {
          id: "g11_u1",
          grade: 11,
          unitNumber: 1,
          title: "Unit 1: A Long and Healthy Life",
          description: "Tuổi thọ, hệ miễn dịch, chế độ dinh dưỡng và thói quen lành mạnh",
          difficulty: "Nâng cao",
          words: [
            { id: "g11_w1", word: "Longevity", phonetic: "/lɒnˈdʒev.ə.ti/", meaningVi: "Tuổi thọ, sự sống lâu dài", exampleEn: "Exercise contributes to longevity.", exampleVi: "Tập thể dục góp phần kéo dài tuổi thọ.", grade: 11, unitNumber: 1, distractorsVi: ["Căn bệnh mãn tính", "Sự suy giảm trí nhớ", "Thể trạng suy nhược"] },
            { id: "g11_w2", word: "Immune system", phonetic: "/ɪˈmjuːn ˌsɪs.təm/", meaningVi: "Hệ miễn dịch phòng chống bệnh tật", exampleEn: "Vitamin C strengthens the immune system.", exampleVi: "Vitamin C tăng cường hệ miễn dịch.", grade: 11, unitNumber: 1, distractorsVi: ["Hệ bài tiết cơ thể", "Hệ tuần hoàn máu", "Hệ hô hấp"] },
            { id: "g11_w3", word: "Nutrient", phonetic: "/ˈnjuː.tri.ənt/", meaningVi: "Chất dinh dưỡng vi lượng & đa lượng", exampleEn: "Vegetables are rich in nutrients.", exampleVi: "Rau củ rất giàu chất dinh dưỡng.", grade: 11, unitNumber: 1, distractorsVi: ["Độc tố tích tụ", "Calo rỗng có hại", "Chất tạo màu nhân tạo"] }
          ],
          grammarLesson: GRAMMAR_DATABASE[11][0],
          bestScore: 2100,
          isCompleted: true
        },
        {
          id: "g11_u2",
          grade: 11,
          unitNumber: 2,
          title: "Unit 2: The Generation Gap",
          description: "Bất đồng quan điểm, giờ giới nghiêm và sự thấu hiểu",
          difficulty: "Nâng cao",
          words: [
            { id: "g11_w4", word: "Curfew", phonetic: "/ˈkɜː.fjuː/", meaningVi: "Giờ giới nghiêm buộc phải về nhà", exampleEn: "I must be home before the 10 PM curfew.", exampleVi: "Tôi phải về nhà trước giờ giới nghiêm 10 giờ tối.", grade: 11, unitNumber: 2, distractorsVi: ["Kỳ nghỉ cuối tuần", "Quy định trang phục", "Lịch trực nhật lớp"] },
            { id: "g11_w5", word: "Conflict", phonetic: "/ˈkɒn.flɪkt/", meaningVi: "Xung đột, mâu thuẫn tranh chấp", exampleEn: "Open communication resolves family conflict.", exampleVi: "Giao tiếp cởi mở giúp giải quyết mâu thuẫn gia đình.", grade: 11, unitNumber: 2, distractorsVi: ["Sự đồng thuận tuyệt đối", "Tình cảm gắn bó", "Thỏa hiệp hài hòa"] }
          ],
          grammarLesson: GRAMMAR_DATABASE[11][1],
          bestScore: 0,
          isCompleted: false
        },
        {
          id: "g11_u3",
          grade: 11,
          unitNumber: 3,
          title: "Unit 3: Cities of the Future",
          description: "Đô thị tương lai, cảm biến thông minh và hạ tầng hiện đại",
          difficulty: "Nâng cao",
          words: [
            { id: "g11_w6", word: "Infrastructure", phonetic: "/ˈɪn.frəˌstrʌk.tʃər/", meaningVi: "Cơ sở hạ tầng đô thị", exampleEn: "The city invested in green infrastructure.", exampleVi: "Thành phố đầu tư vào hạ tầng xanh.", grade: 11, unitNumber: 3, distractorsVi: ["Ô nhiễm môi trường", "Khu ổ chuột", "Ùn tắc kinh hoàng"] },
            { id: "g11_w7", word: "Pedestrian", phonetic: "/pəˈdes.tri.ən/", meaningVi: "Người đi bộ trên đường", exampleEn: "Pedestrian zones reduce traffic jams.", exampleVi: "Phố đi bộ giúp giảm ùn tắc giao thông.", grade: 11, unitNumber: 3, distractorsVi: ["Người lái xe tải", "Khách đi tàu điện", "Tài xế xe buýt"] }
          ],
          grammarLesson: GRAMMAR_DATABASE[11][2],
          bestScore: 0,
          isCompleted: false
        }
      ]
    }
  ],
  12: [
    {
      id: "sub_grade_12",
      grade: 12,
      title: "Tiếng Anh Lớp 12 & Ôn Thi Tốt Nghiệp",
      subtitle: "Chuyện đời danh nhân, hội nhập văn hóa và đề thi HSG",
      iconCategory: "school",
      units: [
        {
          id: "g12_u1",
          grade: 12,
          unitNumber: 1,
          title: "Unit 1: Life Stories & Perseverance",
          description: "Những tấm gương cống hiến, ý chí kiên định và thành tựu",
          difficulty: "Nâng cao HSG",
          words: [
            { id: "g12_w1", word: "Dedicate", phonetic: "/ˈded.ɪ.keɪt/", meaningVi: "Cống hiến, tận tụy hy sinh", exampleEn: "He dedicated his life to education.", exampleVi: "Ông đã cống hiến cả cuộc đời cho giáo dục.", grade: 12, unitNumber: 1, distractorsVi: ["Từ chối tham gia", "Kiếm lợi nhuận riêng", "Phá bỏ truyền thống"] },
            { id: "g12_w2", word: "Distinguished", phonetic: "/dɪˈstɪŋ.ɡwɪʃt/", meaningVi: "Kiệt xuất, lỗi lạc, ưu tú xuất chúng", exampleEn: "She is a distinguished scientist.", exampleVi: "Bà là một nhà khoa học kiệt xuất.", grade: 12, unitNumber: 1, distractorsVi: ["Bình thường mờ nhạt", "Thất bại thảm hại", "Độc tài bảo thủ"] },
            { id: "g12_w3", word: "Persevere", phonetic: "/ˌpɜː.sɪˈvɪər/", meaningVi: "Kiên trì theo đuổi mục tiêu", exampleEn: "They persevered through every hardship.", exampleVi: "Họ đã kiên trì vượt qua mọi gian khó.", grade: 12, unitNumber: 1, distractorsVi: ["Bỏ cuộc buông xuôi", "Do dự thoái thác", "Nản chí nửa chừng"] }
          ],
          grammarLesson: GRAMMAR_DATABASE[12][0],
          bestScore: 2350,
          isCompleted: true
        },
        {
          id: "g12_u2",
          grade: 12,
          unitNumber: 2,
          title: "Unit 2: A Multicultural World",
          description: "Đa văn hóa, sự hội nhập và giữ gìn bản sắc",
          difficulty: "Nâng cao HSG",
          words: [
            { id: "g12_w4", word: "Assimilation", phonetic: "/əˌsɪm.ɪˈleɪ.ʃən/", meaningVi: "Sự đồng hóa văn hóa", exampleEn: "Immigrants adapt without total assimilation.", exampleVi: "Người nhập cư thích nghi mà không bị đồng hóa hoàn toàn.", grade: 12, unitNumber: 2, distractorsVi: ["Sự cô lập cách ly", "Xung đột chủng tộc", "Sự bảo thủ cực đoan"] },
            { id: "g12_w5", word: "Heritage", phonetic: "/ˈher.ɪ.tɪdʒ/", meaningVi: "Di sản văn hóa truyền đời", exampleEn: "Folk music is our treasured heritage.", exampleVi: "Âm nhạc dân gian là di sản quý báu của chúng ta.", grade: 12, unitNumber: 2, distractorsVi: ["Trào lưu nhất thời", "Công nghệ mới xuất hiện", "Tài sản nợ nần"] }
          ],
          grammarLesson: GRAMMAR_DATABASE[12][1],
          bestScore: 0,
          isCompleted: false
        },
        {
          id: "g12_u3",
          grade: 12,
          unitNumber: 3,
          title: "Unit 3: Green Living & Eco-Action",
          description: "Đa dạng sinh học, thảm họa sinh thái và năng lượng sạch",
          difficulty: "Nâng cao HSG",
          words: [
            { id: "g12_w6", word: "Catastrophic", phonetic: "/ˌkæt.əˈstrɒf.ɪk/", meaningVi: "Thảm họa, thảm khốc tàn khốc", exampleEn: "A catastrophic storm damaged the coast.", exampleVi: "Cơn bão thảm khốc đã tàn phá vùng bờ biển.", grade: 12, unitNumber: 3, distractorsVi: ["Nhẹ nhàng thoáng qua", "Có lợi cho sinh vật", "Bình yên vô hại"] },
            { id: "g12_w7", word: "Biodiversity", phonetic: "/ˌbaɪ.əʊ.daɪˈvɜː.sə.ti/", meaningVi: "Đa dạng sinh học của các loài", exampleEn: "Protecting biodiversity ensures food security.", exampleVi: "Bảo vệ đa dạng sinh học đảm bảo an ninh lương thực.", grade: 12, unitNumber: 3, distractorsVi: ["Ô nhiễm kim loại nặng", "Sự suy giảm dân số", "Khai thác cạn kiệt"] }
          ],
          grammarLesson: GRAMMAR_DATABASE[12][2],
          bestScore: 1800,
          isCompleted: false
        }
      ]
    }
  ]
};

export const DEFAULT_ASSIGNMENTS = [
  {
    id: "seed_assign_1",
    teacherUid: "to_tieng_anh_lp",
    teacherName: "Tổ Chuyên Môn Tiếng Anh (THPT Lương Phú)",
    grade: 10,
    title: "Kiểm tra 15p Từ vựng & Ngữ pháp Unit 1-2",
    description: "Ôn tập Family Life và Humans & Environment cho học sinh lớp 10",
    questions: [
      {
        id: "q1",
        question: "My father is the ________; he works hard to earn money for our family.",
        options: ["breadwinner", "homemaker", "chore", "routine"],
        correctIndex: 0,
        explanation: "'Breadwinner' nghĩa là trụ cột gia đình, người kiếm thu nhập chính."
      },
      {
        id: "q2",
        question: "We should adopt an ________ lifestyle to protect the environment.",
        options: ["eco-friendly", "destructive", "harmful", "ordinary"],
        correctIndex: 0,
        explanation: "'Eco-friendly' có nghĩa là thân thiện với môi trường."
      },
      {
        id: "q3",
        question: "Look! The children ________ football in the school playground.",
        options: ["play", "are playing", "played", "have played"],
        correctIndex: 1,
        explanation: "Có từ nhận biết 'Look!' nên chia ở thì hiện tại tiếp diễn (are playing)."
      }
    ],
    createdAt: Date.now() - 86400000
  },
  {
    id: "seed_assign_2",
    teacherUid: "to_tieng_anh_lp",
    teacherName: "Tổ Chuyên Môn Tiếng Anh (THPT Lương Phú)",
    grade: 11,
    title: "Khảo sát Từ vựng Unit 1-2: Healthy Life & Generation Gap",
    description: "Kiểm tra kiến thức từ vựng nâng cao và mạo từ Lớp 11",
    questions: [
      {
        id: "q4",
        question: "Regular exercise strengthens the ________ system against viruses.",
        options: ["immune", "digestive", "nervous", "circulatory"],
        correctIndex: 0,
        explanation: "'Immune system' là hệ miễn dịch bảo vệ cơ thể."
      },
      {
        id: "q5",
        question: "Her parents set a strict 10 PM ________ for every weekday.",
        options: ["curfew", "deadline", "timetable", "schedule"],
        correctIndex: 0,
        explanation: "'Curfew' nghĩa là giờ giới nghiêm ban đêm."
      }
    ],
    createdAt: Date.now() - 43200000
  },
  {
    id: "seed_assign_3",
    teacherUid: "to_tieng_anh_lp",
    teacherName: "Tổ Chuyên Môn Tiếng Anh (THPT Lương Phú)",
    grade: 12,
    title: "Luyện đề HSG & Ôn Thi Tốt Nghiệp: Advanced Vocab & Inversion",
    description: "Bộ câu hỏi trắc nghiệm từ vựng nâng cao kỳ thi Chuyên & HSG",
    questions: [
      {
        id: "q6",
        question: "The professor gave a ________ explanation that everyone could easily understand.",
        options: ["lucid", "ambiguous", "vague", "ephemeral"],
        correctIndex: 0,
        explanation: "'Lucid' nghĩa là rõ ràng, minh bạch, dễ hiểu."
      },
      {
        id: "q7",
        question: "Seldom ________ such a brilliant performance in English debate.",
        options: ["I have seen", "have I seen", "did I saw", "I saw"],
        correctIndex: 1,
        explanation: "Đảo ngữ với phó từ phủ định 'Seldom': Seldom + have + S + V3 (have I seen)."
      }
    ],
    createdAt: Date.now() - 21600000
  }
];
