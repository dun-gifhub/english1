# Tap Hunter English - THPT Lương Phú

Ứng dụng học tiếng Anh phản xạ **Tap Hunter** phân cấp từ Lớp 6 - 12 chuẩn Bộ GD&ĐT với 2 nhánh:
- **Giáo viên**: Ra đề thi (thủ công & trích xuất ngân hàng đề THPTQG, HSG, Chuyên lớp 10, Cambridge/ĐGNL), soạn từ mới và ngữ pháp đồng bộ lên hệ thống.
- **Học sinh**: Game phản xạ đấu từ Tap Hunt (rơi tự do, combo điểm, âm thanh phát âm chuẩn), làm bài kiểm tra trắc nghiệm nhận XP, luyện chuyên đề ngữ pháp.
- **Từ điển thông minh song ngữ**: Tra cứu Anh - Việt & Việt - Anh, phát âm IPA & Text-To-Speech, phân tích hình vị tự động cho mọi từ mới.
- **Phòng học & Bạn bè**: Kết bạn theo mã `LP-xxxx`, mở phòng học trực tuyến và phòng luyện phản xạ chung mã 6 số.
- **Bảng vàng vinh danh**: Xếp hạng theo tháng và toàn thời gian, tự động tính khối lớp và niên khóa theo năm học mới (tháng 9 hàng năm).

## Công nghệ sử dụng
- **React 19 + TypeScript + Vite**
- **Tailwind CSS v4**
- **Web Audio API & Web Speech API** cho hiệu ứng âm thanh và phát âm từ vựng
- **Firebase Firestore & Authentication** đồng bộ dữ liệu thời gian thực
- **Canvas Confetti** cho hiệu ứng vinh danh và hoàn thành bài thi

## Khởi chạy dự án
```bash
npm install
npm run dev
```
Dev server lắng nghe tại `http://localhost:3000`.

