import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import {
  Trophy,
  Crown,
  Medal,
  Award,
  Flame,
  Zap,
  Calendar,
  Clock
} from 'lucide-react';

export const LeaderboardScreen: React.FC = () => {
  const { monthlyLeaderboard, allTimeLeaderboard, currentUser } = useApp();
  const [isMonthly, setIsMonthly] = useState(true);

  const activeEntries = isMonthly ? monthlyLeaderboard : allTimeLeaderboard;
  const top1 = activeEntries[0];
  const top2 = activeEntries[1];
  const top3 = activeEntries[2];
  const rest = activeEntries.slice(3);

  return (
    <div className="max-w-4xl mx-auto space-y-6 pb-20">
      {/* Header Banner */}
      <div className="p-6 rounded-3xl bg-[#1B263B] border border-[#FFD166]/40 shadow-xl flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <span className="text-[10px] font-extrabold uppercase tracking-widest text-[#FFD166] block">
            Vinh Danh Học Sinh Xuất Sắc
          </span>
          <h2 className="text-xl font-black text-white mt-0.5">
            Bảng Vàng THPT Lương Phú
          </h2>
          <p className="text-xs text-[#778DA9]">
            Xếp hạng điểm săn từ vựng phản xạ và làm bài thi tự động tính theo năm học
          </p>
        </div>

        {/* Tab switch between Monthly & All-time */}
        <div className="flex p-1 rounded-2xl bg-[#131F2E] border border-[#27384E]">
          <button
            onClick={() => setIsMonthly(true)}
            className={`px-4 py-2 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 cursor-pointer ${
              isMonthly
                ? 'bg-[#FFD166] text-[#0D1B2A] shadow-md shadow-[#FFD166]/20'
                : 'text-[#ADB5BD] hover:text-white'
            }`}
          >
            <Calendar size={14} />
            <span>Tháng Này</span>
          </button>
          <button
            onClick={() => setIsMonthly(false)}
            className={`px-4 py-2 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 cursor-pointer ${
              !isMonthly
                ? 'bg-[#00E5FF] text-[#0D1B2A] shadow-md shadow-[#00E5FF]/20'
                : 'text-[#ADB5BD] hover:text-white'
            }`}
          >
            <Clock size={14} />
            <span>Toàn Thời Gian</span>
          </button>
        </div>
      </div>

      {/* Empty State when no real users scored yet */}
      {activeEntries.length === 0 ? (
        <div className="p-12 text-center rounded-3xl bg-[#1B263B] border border-[#27384E] space-y-3">
          <Trophy size={48} className="mx-auto text-[#FFD166] opacity-60" />
          <h3 className="text-lg font-bold text-white">Chưa Có Dữ Liệu Bảng Vàng</h3>
          <p className="text-xs text-[#778DA9] max-w-md mx-auto leading-relaxed">
            Hệ thống đã loại bỏ toàn bộ tài khoản demo. Hãy bắt đầu lượt thi đấu săn từ vựng hoặc hoàn thành bài thi trắc nghiệm để ghi danh đầu tiên trên bảng vàng THPT Lương Phú!
          </p>
        </div>
      ) : (
        <>
          {/* Top 3 Podium */}
          {top1 && (
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 pt-4">
              {/* Rank 2 (Silver) */}
              {top2 && (
                <div className="order-2 md:order-1 p-5 rounded-3xl bg-[#1B263B] border border-[#ADB5BD]/40 text-center flex flex-col justify-between shadow-lg relative">
                  <span className="absolute -top-3.5 left-1/2 -translate-x-1/2 px-3 py-0.5 rounded-full bg-[#ADB5BD] text-[#0D1B2A] text-xs font-extrabold flex items-center gap-1">
                    <Medal size={13} />
                    <span>#2 Á Quân</span>
                  </span>

                  <div className="pt-3">
                    <div
                      className="w-14 h-14 mx-auto rounded-2xl flex items-center justify-center font-black text-lg text-[#0D1B2A] shadow-md"
                      style={{ backgroundColor: top2.avatarColor || '#ADB5BD' }}
                    >
                      {top2.name.charAt(0)}
                    </div>
                    <h4 className="text-base font-bold text-white mt-2.5 truncate">{top2.name}</h4>
                    <p className="text-xs text-[#00E5FF] font-semibold">{top2.className}</p>
                    <span className="inline-block px-2 py-0.5 rounded-md bg-[#131F2E] text-[10px] text-[#778DA9] mt-1">
                      Cấp {top2.level}
                    </span>
                  </div>

                  <div className="mt-4 pt-3 border-t border-[#27384E]">
                    <span className="text-xs text-[#778DA9] block">Điểm tích lũy</span>
                    <span className="text-xl font-black text-white">
                      {(isMonthly ? top2.monthlyScore : top2.score).toLocaleString()}
                    </span>
                  </div>
                </div>
              )}

              {/* Rank 1 (Gold) */}
              <div className="order-1 md:order-2 p-6 rounded-3xl bg-linear-to-b from-[#1E2D40] via-[#2A2338] to-[#1E2D40] border-2 border-[#FFD166] text-center flex flex-col justify-between shadow-2xl shadow-[#FFD166]/15 relative scale-102">
                <span className="absolute -top-4 left-1/2 -translate-x-1/2 px-4 py-1 rounded-full bg-[#FFD166] text-[#0D1B2A] text-xs font-black flex items-center gap-1.5 shadow-md">
                  <Crown size={15} />
                  <span>#1 QUÁN QUÂN</span>
                </span>

                <div className="pt-3">
                  <div
                    className="w-18 h-18 mx-auto rounded-2xl flex items-center justify-center font-black text-2xl text-[#0D1B2A] shadow-xl shadow-[#FFD166]/20 border-2 border-white"
                    style={{ backgroundColor: top1.avatarColor || '#FFD166' }}
                  >
                    {top1.name.charAt(0)}
                  </div>
                  <h3 className="text-lg font-black text-white mt-3 truncate">{top1.name}</h3>
                  <p className="text-xs text-[#FFD166] font-bold">{top1.className}</p>
                  <span className="inline-block px-2.5 py-0.5 rounded-md bg-[#FFD166]/20 text-[11px] font-bold text-[#FFD166] mt-1.5 border border-[#FFD166]/30">
                    {top1.badge} • Cấp {top1.level}
                  </span>
                </div>

                <div className="mt-4 pt-3 border-t border-[#27384E]">
                  <span className="text-xs text-[#778DA9] block">Điểm vô địch</span>
                  <span className="text-2xl font-black text-[#FFD166]">
                    {(isMonthly ? top1.monthlyScore : top1.score).toLocaleString()}
                  </span>
                </div>
              </div>

              {/* Rank 3 (Bronze) */}
              {top3 && (
                <div className="order-3 p-5 rounded-3xl bg-[#1B263B] border border-[#CD7F32]/50 text-center flex flex-col justify-between shadow-lg relative">
                  <span className="absolute -top-3.5 left-1/2 -translate-x-1/2 px-3 py-0.5 rounded-full bg-[#CD7F32] text-white text-xs font-extrabold flex items-center gap-1">
                    <Medal size={13} />
                    <span>#3 Hạng Ba</span>
                  </span>

                  <div className="pt-3">
                    <div
                      className="w-14 h-14 mx-auto rounded-2xl flex items-center justify-center font-black text-lg text-[#0D1B2A] shadow-md"
                      style={{ backgroundColor: top3.avatarColor || '#CD7F32' }}
                    >
                      {top3.name.charAt(0)}
                    </div>
                    <h4 className="text-base font-bold text-white mt-2.5 truncate">{top3.name}</h4>
                    <p className="text-xs text-[#00E5FF] font-semibold">{top3.className}</p>
                    <span className="inline-block px-2 py-0.5 rounded-md bg-[#131F2E] text-[10px] text-[#778DA9] mt-1">
                      Cấp {top3.level}
                    </span>
                  </div>

                  <div className="mt-4 pt-3 border-t border-[#27384E]">
                    <span className="text-xs text-[#778DA9] block">Điểm tích lũy</span>
                    <span className="text-xl font-black text-white">
                      {(isMonthly ? top3.monthlyScore : top3.score).toLocaleString()}
                    </span>
                  </div>
                </div>
              )}
            </div>
          )}

          {/* Rest of the Leaderboard Table */}
          <div className="p-5 rounded-3xl bg-[#1B263B] border border-[#27384E] space-y-3">
            <h4 className="text-xs font-bold text-[#778DA9] uppercase tracking-wider">
              Toàn Bộ Bảng Xếp Hạng ({activeEntries.length} Thợ Săn)
            </h4>

            <div className="space-y-2">
              {activeEntries.map((entry) => {
                const isMe = currentUser && entry.name.toLowerCase().includes((currentUser.displayName || '').toLowerCase());
                return (
                  <div
                    key={entry.rank}
                    className={`p-3.5 rounded-2xl border flex items-center justify-between gap-3 transition-all ${
                      isMe
                        ? 'bg-[#00E5FF]/10 border-[#00E5FF] shadow-md shadow-[#00E5FF]/10'
                        : 'bg-[#131F2E] border-[#27384E] hover:border-[#2D3F56]'
                    }`}
                  >
                    <div className="flex items-center gap-3">
                      <span
                        className={`w-7 h-7 rounded-full flex items-center justify-center font-black text-xs shrink-0 ${
                          entry.rank === 1
                            ? 'bg-[#FFD166] text-[#0D1B2A]'
                            : entry.rank === 2
                            ? 'bg-[#ADB5BD] text-[#0D1B2A]'
                            : entry.rank === 3
                            ? 'bg-[#CD7F32] text-white'
                            : 'bg-[#1E2D40] text-[#778DA9]'
                        }`}
                      >
                        {entry.rank}
                      </span>

                      <div>
                        <div className="flex items-center gap-1.5">
                          <span className="text-sm font-bold text-white">{entry.name}</span>
                          {isMe && (
                            <span className="px-1.5 py-0.2 rounded-md bg-[#00E5FF] text-[#0D1B2A] text-[10px] font-black">
                              BẠN
                            </span>
                          )}
                        </div>
                        <p className="text-xs text-[#778DA9]">
                          {entry.className} • <span className="text-[#00E5FF]">Cấp {entry.level}</span>
                        </p>
                      </div>
                    </div>

                    <div className="text-right">
                      <span className="text-sm font-black text-white block">
                        {(isMonthly ? entry.monthlyScore : entry.score).toLocaleString()} pts
                      </span>
                      <span className="text-[10px] text-[#FFD166] font-semibold">{entry.badge}</span>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </>
      )}
    </div>
  );
};
