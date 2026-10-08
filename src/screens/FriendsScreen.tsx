import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import {
  Users,
  UserPlus,
  PhoneCall,
  Copy,
  Check,
  Video,
  Sparkles,
  Zap,
  ArrowRight,
  ShieldCheck,
  Circle
} from 'lucide-react';

interface FriendsScreenProps {
  onOpenCallRoom: () => void;
}

export const FriendsScreen: React.FC<FriendsScreenProps> = ({ onOpenCallRoom }) => {
  const {
    currentUser,
    friends,
    activeRoom,
    friendMessage,
    addFriendByCode,
    clearFriendMessage,
    createRoom,
    joinRoom
  } = useApp();

  const [inputCode, setInputCode] = useState('');
  const [joinCode, setJoinCode] = useState('');
  const [copiedCode, setCopiedCode] = useState(false);

  const handleCopyCode = () => {
    if (!currentUser?.friendCode) return;
    navigator.clipboard.writeText(currentUser.friendCode);
    setCopiedCode(true);
    setTimeout(() => setCopiedCode(false), 2000);
  };

  const handleAddFriend = (e: React.FormEvent) => {
    e.preventDefault();
    if (!inputCode.trim()) return;
    addFriendByCode(inputCode.trim());
    setInputCode('');
  };

  const handleCreateRoom = () => {
    const hostName = currentUser?.displayName || 'Thợ Săn Lương Phú';
    createRoom(hostName);
    onOpenCallRoom();
  };

  const handleJoinRoom = (e: React.FormEvent) => {
    e.preventDefault();
    if (!joinCode.trim()) return;
    const userName = currentUser?.displayName || 'Thợ Săn Lương Phú';
    const success = joinRoom(joinCode.trim(), userName);
    if (success) {
      setJoinCode('');
      onOpenCallRoom();
    }
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6 pb-20">
      {/* Header & Your Friend Code */}
      <div className="p-6 rounded-3xl bg-[#1B263B] border border-[#27384E] shadow-xl flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <span className="text-[10px] font-extrabold uppercase tracking-widest text-[#00E5FF] block">
            Cộng Đồng Thợ Săn Lương Phú
          </span>
          <h2 className="text-xl font-black text-white mt-0.5">
            Bạn Bè & Phòng Học Phản Xạ
          </h2>
          <p className="text-xs text-[#778DA9]">
            Kết bạn cùng trường và tham gia phòng học trực tuyến cùng luyện từ vựng
          </p>
        </div>

        <div className="flex items-center gap-2 p-2 rounded-2xl bg-[#131F2E] border border-[#2D3F56]">
          <span className="text-xs text-[#778DA9] pl-2">Mã của bạn:</span>
          <span className="font-mono text-sm font-black text-[#00E5FF]">
            {currentUser?.friendCode || 'LP-2026'}
          </span>
          <button
            onClick={handleCopyCode}
            className="p-1.5 rounded-xl bg-[#27384E] hover:bg-[#344966] text-white transition-colors cursor-pointer"
            title="Sao chép mã"
          >
            {copiedCode ? <Check size={14} className="text-[#06D6A0]" /> : <Copy size={14} />}
          </button>
        </div>
      </div>

      {/* Status banner */}
      {friendMessage && (
        <div className="p-3.5 rounded-2xl bg-[#1E2D40] border border-[#00E5FF] text-xs font-bold text-white flex items-center justify-between">
          <span>{friendMessage}</span>
          <button onClick={clearFriendMessage} className="text-[#778DA9] hover:text-white">✕</button>
        </div>
      )}

      {/* Active Call Room Alert if exists */}
      {activeRoom && (
        <div className="p-5 rounded-3xl bg-linear-to-r from-[#1B263B] via-[#0E3A42] to-[#1B263B] border border-[#00E5FF] shadow-xl flex items-center justify-between gap-4 flex-wrap">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-[#00E5FF]/20 text-[#00E5FF] flex items-center justify-center animate-pulse">
              <PhoneCall size={20} />
            </div>
            <div>
              <h4 className="text-sm font-bold text-white">{activeRoom.title}</h4>
              <p className="text-xs text-[#00E5FF]">Mã phòng: #{activeRoom.roomCode} • Đang diễn ra</p>
            </div>
          </div>

          <button
            onClick={onOpenCallRoom}
            className="px-4 py-2 rounded-xl text-xs font-extrabold bg-[#00E5FF] text-[#0D1B2A] hover:bg-[#38bdf8] transition-all flex items-center gap-1.5 shadow-md shadow-[#00E5FF]/20 cursor-pointer"
          >
            <span>Vào Lại Phòng</span>
            <ArrowRight size={14} />
          </button>
        </div>
      )}

      {/* Room Actions Deck: Create Room & Join Room */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {/* Create Room Card */}
        <div className="p-5 rounded-3xl bg-[#1B263B] border border-[#27384E] space-y-3">
          <div className="w-10 h-10 rounded-xl bg-[#00E5FF]/20 text-[#00E5FF] flex items-center justify-center">
            <Video size={20} />
          </div>
          <h3 className="text-base font-bold text-white">Tạo Phòng Luyện Phản Xạ</h3>
          <p className="text-xs text-[#778DA9] leading-relaxed">
            Mở phòng học nhóm trực tuyến, cấp mã 6 chữ số để bạn bè cùng tham gia đấu từ và trò chuyện.
          </p>
          <button
            onClick={handleCreateRoom}
            className="w-full py-2.5 rounded-xl text-xs font-extrabold bg-[#00E5FF] text-[#0D1B2A] hover:bg-[#38bdf8] transition-all cursor-pointer shadow-md shadow-[#00E5FF]/20"
          >
            Tạo Phòng Mới Ngay
          </button>
        </div>

        {/* Join Room Card */}
        <div className="p-5 rounded-3xl bg-[#1B263B] border border-[#27384E] space-y-3">
          <div className="w-10 h-10 rounded-xl bg-[#9D4EDD]/20 text-[#9D4EDD] flex items-center justify-center">
            <Users size={20} />
          </div>
          <h3 className="text-base font-bold text-white">Vào Phòng Bằng Mã</h3>
          <p className="text-xs text-[#778DA9] leading-relaxed">
            Nhập mã phòng do bạn bè hoặc thầy cô chia sẻ để tham gia lớp luyện tập.
          </p>
          <form onSubmit={handleJoinRoom} className="flex gap-2">
            <input
              type="text"
              value={joinCode}
              onChange={(e) => setJoinCode(e.target.value)}
              placeholder="Nhập mã phòng (vd: 123456)"
              className="flex-1 px-3 py-2 text-xs rounded-xl bg-[#131F2E] border border-[#27384E] text-white focus:outline-hidden focus:border-[#9D4EDD]"
            />
            <button
              type="submit"
              className="px-4 py-2 rounded-xl text-xs font-extrabold bg-[#9D4EDD] text-white hover:bg-[#a855f7] transition-all cursor-pointer"
            >
              Tham Gia
            </button>
          </form>
        </div>
      </div>

      {/* Add Friend Input Bar */}
      <div className="p-5 rounded-3xl bg-[#1B263B] border border-[#27384E] space-y-3">
        <h4 className="text-sm font-bold text-white flex items-center gap-2">
          <UserPlus size={16} className="text-[#00E5FF]" />
          <span>Kết Bạn Mới Theo Mã</span>
        </h4>
        <form onSubmit={handleAddFriend} className="flex gap-2">
          <input
            type="text"
            value={inputCode}
            onChange={(e) => setInputCode(e.target.value)}
            placeholder="Nhập mã bạn bè (Ví dụ: LP-8899)"
            className="flex-1 px-3.5 py-2.5 text-xs rounded-xl bg-[#131F2E] border border-[#27384E] text-white focus:outline-hidden focus:border-[#00E5FF]"
          />
          <button
            type="submit"
            className="px-5 py-2.5 rounded-xl text-xs font-bold bg-[#00E5FF] text-[#0D1B2A] hover:bg-[#38bdf8] transition-all cursor-pointer"
          >
            Thêm Bạn
          </button>
        </form>
      </div>

      {/* Friends List */}
      <div className="space-y-3">
        <h4 className="text-sm font-bold text-white flex items-center justify-between">
          <span>Danh Sách Bạn Bè ({friends.length})</span>
          <span className="text-xs text-[#06D6A0] flex items-center gap-1 font-semibold">
            <Circle size={8} className="fill-[#06D6A0] text-[#06D6A0]" />
            <span>{friends.filter((f) => f.isOnline).length} Đang Online</span>
          </span>
        </h4>

        {friends.length === 0 ? (
          <div className="p-8 text-center rounded-2xl bg-[#1B263B] border border-[#27384E] space-y-2">
            <Users size={36} className="mx-auto text-[#778DA9] opacity-60" />
            <h5 className="text-sm font-bold text-white">Chưa Có Bạn Bè Trong Danh Sách</h5>
            <p className="text-xs text-[#778DA9] max-w-sm mx-auto">
              Hệ thống đã dọn sạch toàn bộ tài khoản demo. Hãy chia sẻ Mã Bạn Bè của bạn hoặc nhập mã của bạn học ở ô bên trên để kết nối luyện tập!
            </p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
            {friends.map((friend) => (
              <div
                key={friend.uid}
                className="p-4 rounded-2xl bg-[#1B263B] border border-[#27384E] hover:border-[#2D3F56] transition-all flex items-center justify-between gap-3"
              >
                <div className="flex items-center gap-3">
                  <div
                    className="w-10 h-10 rounded-xl flex items-center justify-center font-black text-sm text-[#0D1B2A] shrink-0"
                    style={{ backgroundColor: friend.avatarColor || '#00E5FF' }}
                  >
                    {friend.displayName.charAt(0)}
                  </div>

                  <div>
                    <div className="flex items-center gap-1.5">
                      <span className="text-sm font-bold text-white">{friend.displayName}</span>
                      <Circle
                        size={8}
                        className={friend.isOnline ? 'fill-[#06D6A0] text-[#06D6A0]' : 'fill-[#778DA9] text-[#778DA9]'}
                      />
                    </div>
                    <p className="text-[11px] text-[#778DA9]">{friend.currentActivity}</p>
                  </div>
                </div>

                <span className="text-xs font-mono font-bold text-[#00E5FF] px-2 py-1 rounded-lg bg-[#131F2E]">
                  {friend.friendCode}
                </span>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
