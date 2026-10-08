import React, { useState } from 'react';
import { Lock, Eye, EyeOff, X, ShieldAlert } from 'lucide-react';

interface TeacherPasscodeModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

export const TeacherPasscodeModal: React.FC<TeacherPasscodeModalProps> = ({
  isOpen,
  onClose,
  onSuccess
}) => {
  const [passcode, setPasscode] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (passcode.trim() === 'giaovien2026') {
      setError(null);
      setPasscode('');
      onSuccess();
    } else {
      setError('Mã xác thực không chính xác! Vui lòng nhập đúng mã giáo viên được cấp.');
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/75 backdrop-blur-xs">
      <div className="relative w-full max-w-md p-6 rounded-2xl bg-[#1E2D40] border border-[#9D4EDD]/40 shadow-2xl shadow-[#9D4EDD]/10">
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-[#778DA9] hover:text-white transition-colors"
        >
          <X size={20} />
        </button>

        <div className="flex items-center gap-3 mb-4">
          <div className="p-2.5 rounded-xl bg-[#9D4EDD]/20 text-[#9D4EDD]">
            <Lock size={22} />
          </div>
          <div>
            <h3 className="text-lg font-bold text-white">Xác Thực Quyền Giáo Viên</h3>
            <p className="text-xs text-[#778DA9]">Khu vực dành riêng cho Thầy/Cô giáo</p>
          </div>
        </div>

        <p className="mb-5 text-sm leading-relaxed text-[#ADB5BD]">
          Để chuyển sang nhánh <span className="font-semibold text-[#9D4EDD]">Giáo viên</span> (ra đề thi, soạn từ mới và chuyên đề ngữ pháp Lớp 6 - 12), bạn cần nhập mã bảo mật được cấp.
        </p>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block mb-1.5 text-xs font-semibold text-[#778DA9]">
              Mã xác thực giáo viên
            </label>
            <div className="relative">
              <input
                type={showPassword ? 'text' : 'password'}
                value={passcode}
                onChange={(e) => {
                  setPasscode(e.target.value);
                  setError(null);
                }}
                placeholder="Nhập mã bí mật..."
                autoFocus
                className="w-full px-4 py-3 text-sm text-white rounded-xl bg-[#131F2E] border border-[#2D3F56] focus:outline-hidden focus:border-[#9D4EDD] focus:ring-1 focus:ring-[#9D4EDD] transition-all pr-10"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-[#778DA9] hover:text-white"
              >
                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
            {error && (
              <div className="flex items-center gap-1.5 mt-2 text-xs text-[#EF476F]">
                <ShieldAlert size={14} />
                <span>{error}</span>
              </div>
            )}
          </div>

          <div className="flex gap-3 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 py-2.5 px-4 rounded-xl text-sm font-semibold text-[#778DA9] hover:text-white hover:bg-[#27384E] transition-all"
            >
              Hủy
            </button>
            <button
              type="submit"
              className="flex-1 py-2.5 px-4 rounded-xl text-sm font-bold text-white bg-linear-to-r from-[#9D4EDD] to-[#7B2CBF] hover:from-[#A855F7] hover:to-[#9333EA] shadow-md shadow-[#9D4EDD]/25 transition-all"
            >
              Xác Nhận Quyền GV
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
