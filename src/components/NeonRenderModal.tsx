import React from 'react';
import { useApp } from '../context/AppContext';
import {
  Database,
  Cloud,
  CheckCircle,
  AlertTriangle,
  Server,
  Terminal,
  ShieldCheck,
  X,
  ExternalLink,
  RefreshCw
} from 'lucide-react';

interface NeonRenderModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const NeonRenderModal: React.FC<NeonRenderModalProps> = ({ isOpen, onClose }) => {
  const { neonStatus, refreshNeonStatus } = useApp();

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/75 backdrop-blur-sm animate-fade-in">
      <div className="w-full max-w-lg rounded-3xl bg-[#1E2D40] border border-[#00E5FF]/40 shadow-2xl p-6 sm:p-7 space-y-5 text-white max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-[#00E5FF]/20 text-[#00E5FF] flex items-center justify-center">
              <Database size={22} />
            </div>
            <div>
              <h3 className="text-base sm:text-lg font-black text-white">
                Hạ Tầng Neon DB & Render Cloud
              </h3>
              <p className="text-xs text-[#778DA9]">
                Cơ sở dữ liệu PostgreSQL không máy chủ & Máy chủ Web Render
              </p>
            </div>
          </div>

          <button
            onClick={onClose}
            className="p-1.5 rounded-xl bg-[#131F2E] text-[#778DA9] hover:text-white cursor-pointer"
          >
            <X size={18} />
          </button>
        </div>

        {/* Neon Status Card */}
        <div className="p-4 rounded-2xl bg-[#131F2E] border border-[#27384E] space-y-3">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className="text-xs font-bold text-[#00E5FF] flex items-center gap-1.5">
                <Database size={15} />
                <span>Neon PostgreSQL (neon.tech)</span>
              </span>
            </div>
            <button
              onClick={() => refreshNeonStatus()}
              className="text-[11px] text-[#778DA9] hover:text-[#00E5FF] flex items-center gap-1 cursor-pointer"
              title="Làm mới trạng thái"
            >
              <RefreshCw size={12} />
              <span>Kiểm tra lại</span>
            </button>
          </div>

          <div className="flex items-start gap-2.5 p-3 rounded-xl bg-[#0D1B2A] border border-[#27384E]">
            {neonStatus?.isConnected ? (
              <CheckCircle size={18} className="text-[#06D6A0] shrink-0 mt-0.5" />
            ) : (
              <AlertTriangle size={18} className="text-[#FFD166] shrink-0 mt-0.5" />
            )}
            <div className="text-xs space-y-1">
              <p className="font-semibold text-white">
                {neonStatus?.isConnected
                  ? 'Đã kết nối Neon PostgreSQL đám mây trực tiếp'
                  : 'Sẵn sàng kết nối Neon PostgreSQL'}
              </p>
              <p className="text-[#778DA9] leading-relaxed">
                {neonStatus?.message || 'Chế độ lưu trữ kép tự động đồng bộ.'}
              </p>
            </div>
          </div>

          <div className="text-[11px] text-[#ADB5BD] space-y-1 bg-[#1E2D40] p-2.5 rounded-xl font-mono">
            <div className="text-[#00E5FF] font-sans font-bold text-xs mb-1">Cấu hình biến môi trường DATABASE_URL:</div>
            <div className="text-[#778DA9] break-all">
              DATABASE_URL=postgresql://[user]:[password]@[endpoint].neon.tech/[dbname]?sslmode=require
            </div>
          </div>
        </div>

        {/* Render Cloud Guide Card */}
        <div className="p-4 rounded-2xl bg-[#131F2E] border border-[#27384E] space-y-3">
          <div className="flex items-center gap-2 text-xs font-bold text-[#9D4EDD]">
            <Cloud size={16} />
            <span>Triển khai Web lên Render (render.com)</span>
          </div>

          <div className="space-y-2 text-xs text-[#ADB5BD]">
            <div className="flex items-center gap-2">
              <Server size={14} className="text-[#00E5FF] shrink-0" />
              <span>Đã có tệp <code>render.yaml</code> cấu hình tự động cho Render Web Service.</span>
            </div>
            <div className="flex items-center gap-2">
              <Terminal size={14} className="text-[#FFD166] shrink-0" />
              <span>Lệnh chạy Build: <code>npm install && npm run build</code></span>
            </div>
            <div className="flex items-center gap-2">
              <Terminal size={14} className="text-[#06D6A0] shrink-0" />
              <span>Lệnh Khởi Động: <code>npm start</code> (Express Full-Stack + API)</span>
            </div>
          </div>
        </div>

        {/* Demo Accounts Removed Confirmation */}
        <div className="p-4 rounded-2xl bg-[#06D6A0]/10 border border-[#06D6A0]/40 flex items-start gap-3 text-xs text-[#06D6A0]">
          <ShieldCheck size={20} className="shrink-0 mt-0.5 text-[#06D6A0]" />
          <div>
            <p className="font-bold text-white">Đã loại bỏ 100% tài khoản demo</p>
            <p className="text-[#ADB5BD] mt-0.5 leading-relaxed">
              Tất cả tài khoản học sinh mẫu, giáo viên ảo và dữ liệu giả lập đã được xóa bỏ hoàn toàn. Bảng vàng và lịch sử làm bài chỉ lưu trữ các tài khoản thực tế được đăng ký bởi học sinh và giáo viên trường THPT Lương Phú.
            </p>
          </div>
        </div>

        <button
          onClick={onClose}
          className="w-full py-2.5 rounded-xl bg-[#00E5FF] text-[#0D1B2A] font-extrabold text-xs hover:bg-[#38bdf8] transition-all cursor-pointer shadow-md shadow-[#00E5FF]/20"
        >
          Đóng Cửa Sổ
        </button>
      </div>
    </div>
  );
};
