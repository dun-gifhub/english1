import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import { loginWithGoogle } from '../services/firebase';
import { apiService } from '../services/apiService';
import {
  User,
  School,
  Sparkles,
  CheckCircle,
  ShieldCheck,
  LogIn,
  AlertCircle,
  KeyRound,
  GraduationCap,
  Lock
} from 'lucide-react';
import { UserRole } from '../types';

export const AuthScreen: React.FC = () => {
  const { registerOrUpdateAccount, setCurrentUser } = useApp();

  const [isRegisterMode, setIsRegisterMode] = useState(true);
  const [role, setRole] = useState<UserRole>('STUDENT');
  const [name, setName] = useState('');
  const [className, setClassName] = useState('10A1');
  const [baseGrade, setBaseGrade] = useState(10);
  const [academicYear, setAcademicYear] = useState(2026);
  const [pin, setPin] = useState('');
  const [teacherPasscode, setTeacherPasscode] = useState('');

  // Login form state
  const [loginIdentifier, setLoginIdentifier] = useState('');
  const [loginPin, setLoginPin] = useState('');

  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  const handleRegisterSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) {
      setError('Vui lòng nhập Họ tên hoặc Tên tài khoản thật của bạn!');
      return;
    }

    if (role === 'TEACHER') {
      if (teacherPasscode.trim().toUpperCase() !== 'LP2026') {
        setError('Mã xác thực Giáo viên không chính xác. Vui lòng liên hệ Tổ Chuyên Môn THPT Lương Phú (Mã: LP2026).');
        return;
      }
    }

    setError(null);
    setIsSubmitting(true);

    try {
      // Register with backend (Neon PostgreSQL) with fallback to local
      const serverUser = await apiService.registerUser({
        name: name.trim(),
        customClassName: role === 'TEACHER' ? 'Tổ Ngoại Ngữ' : className.trim(),
        baseGrade,
        registeredAcademicYear: academicYear,
        role,
        pin: pin.trim()
      });

      if (serverUser) {
        setCurrentUser(serverUser);
      } else {
        // Fallback local registration
        registerOrUpdateAccount(
          name.trim(),
          role === 'TEACHER' ? 'Tổ Ngoại Ngữ' : className.trim(),
          baseGrade,
          academicYear,
          role
        );
      }
    } catch (err: any) {
      setError(err.message || 'Lỗi khi tạo tài khoản. Vui lòng thử lại!');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleLoginSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!loginIdentifier.trim()) {
      setError('Vui lòng nhập Tên tài khoản hoặc Mã bạn bè (VD: LP-XXXX)!');
      return;
    }

    setError(null);
    setIsSubmitting(true);

    try {
      // Attempt login via API / Neon
      const loggedIn = await apiService.loginUser(loginIdentifier.trim(), loginPin.trim());
      if (loggedIn) {
        setCurrentUser(loggedIn);
        return;
      }

      // Check local storage accounts
      const savedUserStr = localStorage.getItem('tap_hunter_user_profile_v2');
      if (savedUserStr) {
        const saved = JSON.parse(savedUserStr);
        if (
          saved.displayName?.toLowerCase() === loginIdentifier.trim().toLowerCase() ||
          saved.friendCode?.toLowerCase() === loginIdentifier.trim().toLowerCase()
        ) {
          setCurrentUser(saved);
          return;
        }
      }

      setError('Không tìm thấy tài khoản tương ứng. Vui lòng chuyển sang tab Đăng Ký Tài Khoản mới.');
    } catch (err: any) {
      setError(err.message || 'Đăng nhập không thành công.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleGoogleSignIn = async () => {
    setIsSubmitting(true);
    setError(null);
    try {
      const res = await loginWithGoogle();
      if (res) {
        registerOrUpdateAccount(res.displayName, '10A1', 10, 2026, 'STUDENT');
      }
    } catch (err: unknown) {
      console.warn('Google Sign-In failed:', err);
      setError('Đăng nhập Google thất bại hoặc cửa sổ bị đóng. Bạn hãy sử dụng biểu mẫu Đăng Ký bên trên.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-4 bg-linear-to-b from-[#0D1B2A] via-[#0F2B1D]/40 to-[#0D1B2A]">
      <div className="w-full max-w-md p-6 sm:p-8 rounded-3xl bg-[#1E2D40] border border-[#2E7D32]/50 shadow-2xl shadow-[#2E7D32]/10 space-y-6">
        {/* School Logo & Title */}
        <div className="text-center space-y-2">
          <div className="w-20 h-20 mx-auto rounded-full bg-white p-1 border-2 border-[#FFD166] shadow-xl flex items-center justify-center overflow-hidden">
            <img
              src="/favicon.png"
              alt="THPT Lương Phú"
              className="w-full h-full object-contain"
              onError={(e) => {
                const target = e.target as HTMLImageElement;
                target.src = '/ic_app_logo.jpg';
              }}
            />
          </div>

          <div>
            <span className="text-xs font-black uppercase tracking-widest text-[#FFD166] block">
              TRƯỜNG THPT LƯƠNG PHÚ
            </span>
            <h1 className="text-2xl font-black text-white tracking-tight mt-0.5">
              Tap Hunter English
            </h1>
            <p className="text-xs text-[#778DA9]">
              Cơ sở dữ liệu đám mây Neon PostgreSQL & Render Cloud
            </p>
          </div>
        </div>

        {/* Tab Switcher: Đăng Ký vs Đăng Nhập */}
        <div className="flex p-1 rounded-2xl bg-[#131F2E] border border-[#27384E]">
          <button
            onClick={() => {
              setIsRegisterMode(true);
              setError(null);
              setSuccessMsg(null);
            }}
            className={`flex-1 py-2 px-3 rounded-xl text-xs font-bold transition-all cursor-pointer ${
              isRegisterMode
                ? 'bg-[#00E5FF] text-[#0D1B2A] shadow-md shadow-[#00E5FF]/20'
                : 'text-[#ADB5BD] hover:text-white'
            }`}
          >
            Đăng Ký Tài Khoản Thật
          </button>
          <button
            onClick={() => {
              setIsRegisterMode(false);
              setError(null);
              setSuccessMsg(null);
            }}
            className={`flex-1 py-2 px-3 rounded-xl text-xs font-bold transition-all cursor-pointer ${
              !isRegisterMode
                ? 'bg-[#00E5FF] text-[#0D1B2A] shadow-md shadow-[#00E5FF]/20'
                : 'text-[#ADB5BD] hover:text-white'
            }`}
          >
            Đăng Nhập
          </button>
        </div>

        {error && (
          <div className="p-3 rounded-xl bg-[#EF476F]/15 border border-[#EF476F]/40 text-xs text-[#EF476F] flex items-center gap-2">
            <AlertCircle size={16} className="shrink-0" />
            <span>{error}</span>
          </div>
        )}

        {successMsg && (
          <div className="p-3 rounded-xl bg-[#06D6A0]/15 border border-[#06D6A0]/40 text-xs text-[#06D6A0] flex items-center gap-2">
            <CheckCircle size={16} className="shrink-0" />
            <span>{successMsg}</span>
          </div>
        )}

        {isRegisterMode ? (
          /* REAL REGISTRATION FORM */
          <form onSubmit={handleRegisterSubmit} className="space-y-4">
            {/* Role selection */}
            <div>
              <label className="block text-xs font-semibold text-[#778DA9] mb-1.5">
                Vai trò người dùng
              </label>
              <div className="grid grid-cols-2 gap-2">
                <button
                  type="button"
                  onClick={() => setRole('STUDENT')}
                  className={`py-2 px-3 rounded-xl text-xs font-bold border transition-all flex items-center justify-center gap-1.5 cursor-pointer ${
                    role === 'STUDENT'
                      ? 'bg-[#00E5FF]/20 border-[#00E5FF] text-[#00E5FF]'
                      : 'bg-[#131F2E] border-[#27384E] text-[#778DA9] hover:text-white'
                  }`}
                >
                  <User size={14} />
                  <span>Học Sinh</span>
                </button>
                <button
                  type="button"
                  onClick={() => setRole('TEACHER')}
                  className={`py-2 px-3 rounded-xl text-xs font-bold border transition-all flex items-center justify-center gap-1.5 cursor-pointer ${
                    role === 'TEACHER'
                      ? 'bg-[#9D4EDD]/20 border-[#9D4EDD] text-[#9D4EDD]'
                      : 'bg-[#131F2E] border-[#27384E] text-[#778DA9] hover:text-white'
                  }`}
                >
                  <GraduationCap size={14} />
                  <span>Giáo Viên</span>
                </button>
              </div>
            </div>

            {/* Teacher Passcode if teacher role */}
            {role === 'TEACHER' && (
              <div className="p-3.5 rounded-2xl bg-[#9D4EDD]/10 border border-[#9D4EDD]/40 space-y-2">
                <div className="flex items-center gap-2 text-xs font-bold text-[#9D4EDD]">
                  <KeyRound size={15} />
                  <span>Mã Xác Thực Giáo Viên THPT Lương Phú</span>
                </div>
                <input
                  type="password"
                  required
                  value={teacherPasscode}
                  onChange={(e) => setTeacherPasscode(e.target.value)}
                  placeholder="Nhập mã giáo viên (Mặc định: LP2026)"
                  className="w-full px-3 py-2 rounded-xl bg-[#131F2E] border border-[#9D4EDD]/50 text-white text-xs focus:outline-hidden"
                />
              </div>
            )}

            <div>
              <label className="block text-xs font-semibold text-[#778DA9] mb-1">
                {role === 'TEACHER' ? 'Họ và tên Giáo viên *' : 'Họ và tên / Nickname Học sinh *'}
              </label>
              <div className="relative">
                <User className="absolute left-3.5 top-1/2 -translate-y-1/2 text-[#00E5FF]" size={16} />
                <input
                  type="text"
                  required
                  value={name}
                  onChange={(e) => {
                    setName(e.target.value);
                    setError(null);
                  }}
                  placeholder={role === 'TEACHER' ? 'Thầy/Cô Nguyễn Văn A' : 'Ví dụ: Nguyễn Minh Đức'}
                  className="w-full pl-10 pr-4 py-2.5 rounded-xl bg-[#131F2E] border border-[#27384E] text-white text-xs sm:text-sm focus:outline-hidden focus:border-[#00E5FF]"
                />
              </div>
            </div>

            {role === 'STUDENT' && (
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-[#778DA9] mb-1">
                    Tên Lớp (VD: 10A1)
                  </label>
                  <div className="relative">
                    <School className="absolute left-3.5 top-1/2 -translate-y-1/2 text-[#FFD166]" size={16} />
                    <input
                      type="text"
                      value={className}
                      onChange={(e) => setClassName(e.target.value)}
                      placeholder="10A1"
                      className="w-full pl-10 pr-3 py-2.5 rounded-xl bg-[#131F2E] border border-[#27384E] text-white text-xs sm:text-sm focus:outline-hidden focus:border-[#FFD166]"
                    />
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-[#778DA9] mb-1">
                    Khối Lớp
                  </label>
                  <select
                    value={baseGrade}
                    onChange={(e) => setBaseGrade(Number(e.target.value))}
                    className="w-full px-3 py-2.5 rounded-xl bg-[#131F2E] border border-[#27384E] text-white text-xs sm:text-sm focus:outline-hidden focus:border-[#00E5FF]"
                  >
                    {[6, 7, 8, 9, 10, 11, 12].map((g) => (
                      <option key={g} value={g}>Lớp {g}</option>
                    ))}
                  </select>
                </div>
              </div>
            )}

            <div>
              <label className="block text-xs font-semibold text-[#778DA9] mb-1">
                Mã PIN / Mật khẩu bảo vệ tài khoản (Tùy chọn)
              </label>
              <div className="relative">
                <Lock className="absolute left-3.5 top-1/2 -translate-y-1/2 text-[#06D6A0]" size={16} />
                <input
                  type="password"
                  value={pin}
                  onChange={(e) => setPin(e.target.value)}
                  placeholder="Nhập 4-6 số để bảo vệ tài khoản"
                  className="w-full pl-10 pr-4 py-2.5 rounded-xl bg-[#131F2E] border border-[#27384E] text-white text-xs sm:text-sm focus:outline-hidden focus:border-[#06D6A0]"
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={isSubmitting}
              className="w-full py-3 px-4 rounded-xl bg-[#2E7D32] hover:bg-[#388e3c] text-white font-extrabold text-sm transition-all flex items-center justify-center gap-2 cursor-pointer shadow-lg shadow-[#2E7D32]/25 disabled:opacity-50"
            >
              <CheckCircle size={18} />
              <span>{isSubmitting ? 'Đang tạo tài khoản...' : 'Tạo Tài Khoản & Bắt Đầu'}</span>
            </button>
          </form>
        ) : (
          /* REAL LOGIN FORM (NO DEMO ACCOUNTS) */
          <form onSubmit={handleLoginSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold text-[#778DA9] mb-1">
                Tên tài khoản hoặc Mã Bạn Bè (VD: LP-XXXX) *
              </label>
              <div className="relative">
                <User className="absolute left-3.5 top-1/2 -translate-y-1/2 text-[#00E5FF]" size={16} />
                <input
                  type="text"
                  required
                  value={loginIdentifier}
                  onChange={(e) => setLoginIdentifier(e.target.value)}
                  placeholder="Nhập tên của bạn hoặc mã bạn bè"
                  className="w-full pl-10 pr-4 py-2.5 rounded-xl bg-[#131F2E] border border-[#27384E] text-white text-xs sm:text-sm focus:outline-hidden focus:border-[#00E5FF]"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-[#778DA9] mb-1">
                Mã PIN bảo vệ (Nếu đã đặt khi đăng ký)
              </label>
              <div className="relative">
                <Lock className="absolute left-3.5 top-1/2 -translate-y-1/2 text-[#06D6A0]" size={16} />
                <input
                  type="password"
                  value={loginPin}
                  onChange={(e) => setLoginPin(e.target.value)}
                  placeholder="Nhập mã PIN"
                  className="w-full pl-10 pr-4 py-2.5 rounded-xl bg-[#131F2E] border border-[#27384E] text-white text-xs sm:text-sm focus:outline-hidden focus:border-[#06D6A0]"
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={isSubmitting}
              className="w-full py-3 px-4 rounded-xl bg-[#00E5FF] hover:bg-[#38bdf8] text-[#0D1B2A] font-extrabold text-sm transition-all flex items-center justify-center gap-2 cursor-pointer shadow-lg shadow-[#00E5FF]/20 disabled:opacity-50"
            >
              <LogIn size={18} />
              <span>{isSubmitting ? 'Đang kiểm tra...' : 'Đăng Nhập'}</span>
            </button>

            <div className="relative my-4">
              <div className="absolute inset-0 flex items-center">
                <div className="w-full border-t border-[#27384E]" />
              </div>
              <div className="relative flex justify-center text-xs">
                <span className="bg-[#1E2D40] px-3 text-[#778DA9]">hoặc</span>
              </div>
            </div>

            <button
              type="button"
              onClick={handleGoogleSignIn}
              disabled={isSubmitting}
              className="w-full py-2.5 px-4 rounded-xl bg-[#131F2E] hover:bg-[#27384E] text-white font-bold text-xs transition-all border border-[#27384E] flex items-center justify-center gap-2 cursor-pointer disabled:opacity-50"
            >
              <LogIn size={15} />
              <span>Đăng Nhập Bằng Google</span>
            </button>
          </form>
        )}
      </div>
    </div>
  );
};
