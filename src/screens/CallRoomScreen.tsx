import React, { useState, useEffect, useRef } from 'react';
import { useApp } from '../context/AppContext';
import { audioService } from '../services/audioService';
import {
  Mic,
  MicOff,
  PhoneOff,
  Users,
  MessageSquare,
  Sparkles,
  Volume2,
  VolumeX,
  Send,
  Monitor,
  MonitorOff,
  Maximize2,
  HelpCircle,
  Radio,
  BookOpen,
  Activity,
  Headphones,
  CheckCircle2,
  AlertCircle
} from 'lucide-react';

interface CallRoomScreenProps {
  onLeave: () => void;
}

export const CallRoomScreen: React.FC<CallRoomScreenProps> = ({ onLeave }) => {
  const { activeRoom, toggleMicInRoom, leaveRoom, currentUser, selectedGrade } = useApp();

  // Screen sharing states
  const [isScreenSharing, setIsScreenSharing] = useState<boolean>(false);
  const [screenShareError, setScreenShareError] = useState<string | null>(null);
  const videoRef = useRef<HTMLVideoElement | null>(null);
  const screenStreamRef = useRef<MediaStream | null>(null);

  // Real Microphone and Audio Level states
  const [micStream, setMicStream] = useState<MediaStream | null>(null);
  const [micAudioLevel, setMicAudioLevel] = useState<number>(0);
  const [micError, setMicError] = useState<string | null>(null);
  const [isLoopbackEnabled, setIsLoopbackEnabled] = useState<boolean>(false);
  const audioContextRef = useRef<AudioContext | null>(null);
  const analyserRef = useRef<AnalyserNode | null>(null);
  const micSourceRef = useRef<MediaStreamAudioSourceNode | null>(null);
  const loopbackGainRef = useRef<GainNode | null>(null);
  const animationFrameRef = useRef<number | null>(null);

  // Active presentation mode: 'screen' | 'whiteboard'
  const [presentationMode, setPresentationMode] = useState<'screen' | 'whiteboard'>('screen');
  const [whiteboardSlideIndex, setWhiteboardSlideIndex] = useState<number>(0);

  // Chat and Voice activities
  const [chatMessages, setChatMessages] = useState<Array<{ sender: string; text: string; time: string; isSystem?: boolean }>>([
    { sender: 'Hệ Thống', text: `Chào mừng bạn vào phòng luyện phản xạ #${activeRoom?.roomCode}!`, time: 'Vừa xong', isSystem: true },
    { sender: 'Trần Bảo Nam (10A1)', text: 'Chào cả nhóm, mình đã bật mic và sẵn sàng luyện từ vựng!', time: '1 phút trước' },
    { sender: 'Lê Thu Hà (11A2)', text: 'Hôm nay chúng ta cùng ôn lại phần Đảo ngữ và Collocations nhé!', time: '30 giây trước' }
  ]);
  const [typedMessage, setTypedMessage] = useState('');
  const [speakingPeer, setSpeakingPeer] = useState<string | null>(null);

  // Lesson presentation slides for group review
  const LESSON_SLIDES = [
    {
      title: `Chuyên Đề Trọng Tâm: Câu Điều Kiện Lớp ${selectedGrade}`,
      content: 'If + S + had + P2, S + would/could + have + P2',
      notes: 'Giả định trái ngược với quá khứ. Lưu ý đảo ngữ: Had + S + P2, S + would have + P2.',
      example: "Had we left earlier, we wouldn't have missed the opening ceremony."
    },
    {
      title: `Từ Vựng Trọng Điểm: Unit 2 - Humans & Environment`,
      content: 'Carbon footprint • Eco-friendly • Biodiversity • Conservation',
      notes: 'Collocations hay gặp: take measures, have an impact on, reduce carbon footprint.',
      example: "Planting trees plays a pivotal role in reducing our collective carbon footprint."
    },
    {
      title: `Thành Ngữ & Collocations Nâng Cao (HSG & THPTQG)`,
      content: 'Keep one\'s cool • Dedicated to V-ing • Consistent with',
      notes: 'Mẹo nhớ: dedicate đi cùng giới từ TO và theo sau là Danh từ hoặc V-ing.',
      example: "The volunteer team is dedicated to preserving mangrove forests."
    }
  ];

  // 1. REAL SCREEN SHARING IMPLEMENTATION
  const startScreenShare = async () => {
    try {
      setScreenShareError(null);
      if (!navigator.mediaDevices || !navigator.mediaDevices.getDisplayMedia) {
        throw new Error('Trình duyệt không hỗ trợ API getDisplayMedia trực tiếp.');
      }

      const stream = await navigator.mediaDevices.getDisplayMedia({
        video: {
          displaySurface: 'monitor'
        },
        audio: true
      });

      screenStreamRef.current = stream;
      setIsScreenSharing(true);
      setPresentationMode('screen');

      if (videoRef.current) {
        videoRef.current.srcObject = stream;
        videoRef.current.play().catch(console.error);
      }

      // Handle when user clicks the browser's native "Stop sharing" bar
      stream.getVideoTracks().forEach((track) => {
        track.onended = () => {
          stopScreenShare();
        };
      });

      // Add system message
      setChatMessages((prev) => [
        ...prev,
        {
          sender: 'Hệ Thống',
          text: `📢 ${currentUser?.displayName || 'Bạn'} đã bắt đầu trình chiếu màn hình trực tiếp!`,
          time: 'Vừa xong',
          isSystem: true
        }
      ]);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : String(err);
      if (!msg.includes('Permission denied') && !msg.includes('cancelled')) {
        setScreenShareError(`Không thể khởi tạo trình chiếu màn hình: ${msg}`);
      }
      setIsScreenSharing(false);
    }
  };

  const stopScreenShare = () => {
    if (screenStreamRef.current) {
      screenStreamRef.current.getTracks().forEach((t) => t.stop());
      screenStreamRef.current = null;
    }
    if (videoRef.current) {
      videoRef.current.srcObject = null;
    }
    setIsScreenSharing(false);
    setChatMessages((prev) => [
      ...prev,
      {
        sender: 'Hệ Thống',
        text: `Đã dừng trình chiếu màn hình.`,
        time: 'Vừa xong',
        isSystem: true
      }
    ]);
  };

  // Toggle fullscreen for video
  const handleToggleFullscreen = () => {
    if (videoRef.current) {
      if (document.fullscreenElement) {
        document.exitFullscreen().catch(() => {});
      } else {
        videoRef.current.requestFullscreen().catch(() => {});
      }
    }
  };

  // 2. REAL MICROPHONE CAPTURE & AUDIO LEVEL ANALYSER
  const initMicrophoneAudio = async () => {
    try {
      setMicError(null);
      if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
        throw new Error('Trình duyệt không hỗ trợ Microphone.');
      }

      const stream = await navigator.mediaDevices.getUserMedia({
        audio: {
          echoCancellation: true,
          noiseSuppression: true,
          autoGainControl: true
        }
      });

      setMicStream(stream);

      // Create Web Audio Analyser
      const AudioCtx = window.AudioContext || (window as unknown as { webkitAudioContext: typeof AudioContext }).webkitAudioContext;
      const ctx = new AudioCtx();
      audioContextRef.current = ctx;

      const analyser = ctx.createAnalyser();
      analyser.fftSize = 64;
      analyserRef.current = analyser;

      const source = ctx.createMediaStreamSource(stream);
      micSourceRef.current = source;
      source.connect(analyser);

      // Optional loopback gain node (disabled by default to prevent feedback squeal)
      const loopbackGain = ctx.createGain();
      loopbackGain.gain.value = 0.0;
      loopbackGainRef.current = loopbackGain;
      source.connect(loopbackGain);
      loopbackGain.connect(ctx.destination);

      // Animation loop to calculate mic level
      const dataArray = new Uint8Array(analyser.frequencyBinCount);
      const updateMeter = () => {
        if (!analyserRef.current) return;
        analyserRef.current.getByteFrequencyData(dataArray);

        // Compute average volume
        let sum = 0;
        for (let i = 0; i < dataArray.length; i++) {
          sum += dataArray[i];
        }
        const avg = sum / dataArray.length;
        setMicAudioLevel(Math.min(100, Math.round((avg / 255) * 100 * 2.2)));

        animationFrameRef.current = requestAnimationFrame(updateMeter);
      };
      updateMeter();
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : String(err);
      setMicError(`Chưa thể truy cập Micro: ${msg}`);
      // Fallback simulated level when speaking
      setMicAudioLevel(0);
    }
  };

  const stopMicrophoneAudio = () => {
    if (animationFrameRef.current) {
      cancelAnimationFrame(animationFrameRef.current);
    }
    if (micStream) {
      micStream.getTracks().forEach((t) => t.stop());
      setMicStream(null);
    }
    if (audioContextRef.current && audioContextRef.current.state !== 'closed') {
      audioContextRef.current.close().catch(() => {});
    }
    setMicAudioLevel(0);
  };

  // Toggle mic action: syncs with AppContext toggleMicInRoom
  const handleToggleMic = () => {
    toggleMicInRoom();
    if (activeRoom?.isMicOn) {
      // It was on, now turning off
      stopMicrophoneAudio();
    } else {
      // It was off, now turning on
      initMicrophoneAudio();
    }
  };

  // Toggle Loopback (Hear own voice)
  const handleToggleLoopback = () => {
    if (!loopbackGainRef.current) return;
    const nextState = !isLoopbackEnabled;
    setIsLoopbackEnabled(nextState);
    loopbackGainRef.current.gain.value = nextState ? 0.3 : 0.0;
  };

  // 3. SOUND TEST & VOICE CALLING AUDIO
  const handleTestSpeakerAndSound = () => {
    audioService.playVictoryFanfare();
    setTimeout(() => {
      audioService.speakEnglish('Sound check: Your speakers are working perfectly. Welcome to Tap Hunter voice room!');
    }, 400);
  };

  const handleSimulatePeerSpeaking = (peerName: string, phrase: string) => {
    setSpeakingPeer(peerName);
    audioService.speakEnglish(phrase);
    setChatMessages((prev) => [
      ...prev,
      {
        sender: peerName,
        text: `🗣️ "${phrase}"`,
        time: 'Vừa xong'
      }
    ]);
    setTimeout(() => {
      setSpeakingPeer(null);
    }, 4000);
  };

  // Cleanup on unmount
  useEffect(() => {
    return () => {
      stopScreenShare();
      stopMicrophoneAudio();
    };
  }, []);

  if (!activeRoom) {
    return (
      <div className="text-center py-20 space-y-4">
        <h3 className="text-lg font-bold text-white">Bạn chưa ở trong phòng học nào!</h3>
        <button
          onClick={onLeave}
          className="px-5 py-2.5 rounded-xl bg-[#00E5FF] text-[#0D1B2A] font-bold text-xs"
        >
          Quay lại màn hình Bạn Bè
        </button>
      </div>
    );
  }

  const handleSendMessage = (e: React.FormEvent) => {
    e.preventDefault();
    if (!typedMessage.trim()) return;

    setChatMessages((prev) => [
      ...prev,
      {
        sender: currentUser?.displayName || 'Thợ Săn Lương Phú',
        text: typedMessage.trim(),
        time: 'Vừa xong'
      }
    ]);
    setTypedMessage('');
  };

  const handleExit = () => {
    stopScreenShare();
    stopMicrophoneAudio();
    leaveRoom();
    onLeave();
  };

  return (
    <div className="max-w-5xl mx-auto space-y-5 pb-20">
      {/* Top Header & Call Status Bar */}
      <div className="p-5 sm:p-6 rounded-3xl bg-linear-to-r from-[#1B263B] via-[#0E3A42] to-[#1B263B] border border-[#00E5FF]/40 shadow-2xl flex items-center justify-between gap-4 flex-wrap">
        <div>
          <div className="flex items-center gap-2">
            <span className="w-2.5 h-2.5 rounded-full bg-[#06D6A0] animate-ping" />
            <span className="text-[10px] font-extrabold uppercase tracking-widest text-[#00E5FF]">
              Phòng Học Nhóm & Luyện Phản Xạ Trực Tuyến (Live)
            </span>
            <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-[#06D6A0]/20 text-[#06D6A0]">
              Âm Thanh Đã Kết Nối
            </span>
          </div>
          <h2 className="text-xl sm:text-2xl font-black text-white mt-0.5">
            {activeRoom.title}
          </h2>
          <p className="text-xs text-[#778DA9]">
            Mã kết nối: <strong className="text-[#00E5FF] font-mono">#{activeRoom.roomCode}</strong> • Chủ phòng: {activeRoom.hostName}
          </p>
        </div>

        {/* Global Controls: Mic, Screen Share, Speaker Test, Leave */}
        <div className="flex items-center gap-2 flex-wrap">
          {/* Audio Test Button */}
          <button
            onClick={handleTestSpeakerAndSound}
            className="p-2.5 sm:px-3 sm:py-2.5 rounded-2xl bg-[#131F2E] hover:bg-[#27384E] text-[#FFD166] border border-[#FFD166]/40 text-xs font-bold transition-all flex items-center gap-1.5 cursor-pointer shadow-md"
            title="Kiểm tra âm thanh loa / tai nghe"
          >
            <Headphones size={17} />
            <span className="hidden sm:inline">Kiểm Tra Loa</span>
          </button>

          {/* Screen Share Toggle */}
          <button
            onClick={isScreenSharing ? stopScreenShare : startScreenShare}
            className={`p-2.5 sm:px-3.5 sm:py-2.5 rounded-2xl text-xs font-bold transition-all flex items-center gap-1.5 cursor-pointer shadow-lg ${
              isScreenSharing
                ? 'bg-[#EF476F] text-white hover:bg-[#d9385d]'
                : 'bg-[#9D4EDD] text-white hover:bg-[#a855f7] shadow-[#9D4EDD]/20'
            }`}
            title={isScreenSharing ? 'Dừng chia sẻ màn hình' : 'Trình chiếu màn hình'}
          >
            {isScreenSharing ? <MonitorOff size={18} /> : <Monitor size={18} />}
            <span className="hidden sm:inline">
              {isScreenSharing ? 'Dừng Trình Chiếu' : 'Trình Chiếu Màn Hình'}
            </span>
          </button>

          {/* Microphone Toggle */}
          <button
            onClick={handleToggleMic}
            className={`p-2.5 sm:px-3.5 sm:py-2.5 rounded-2xl font-bold transition-all flex items-center gap-1.5 cursor-pointer shadow-lg ${
              activeRoom.isMicOn
                ? 'bg-[#00E5FF] text-[#0D1B2A] shadow-[#00E5FF]/25'
                : 'bg-[#EF476F]/20 text-[#EF476F] border border-[#EF476F]/40'
            }`}
            title={activeRoom.isMicOn ? 'Tắt micro' : 'Bật micro'}
          >
            {activeRoom.isMicOn ? <Mic size={18} /> : <MicOff size={18} />}
            <span className="text-xs font-bold hidden sm:inline">
              {activeRoom.isMicOn ? 'Mic Đang Bật' : 'Đã Tắt Mic'}
            </span>
          </button>

          {/* Leave Room Button */}
          <button
            onClick={handleExit}
            className="p-2.5 sm:px-3.5 sm:py-2.5 rounded-2xl bg-[#EF476F] text-white hover:bg-[#d9385d] transition-all flex items-center gap-1.5 cursor-pointer shadow-lg shadow-[#EF476F]/20 text-xs font-bold"
            title="Rời phòng"
          >
            <PhoneOff size={18} />
            <span className="hidden sm:inline">Rời Phòng</span>
          </button>
        </div>
      </div>

      {/* Screen Share Error Banner if any */}
      {screenShareError && (
        <div className="p-3.5 rounded-2xl bg-[#EF476F]/20 border border-[#EF476F] text-[#EF476F] text-xs font-semibold flex items-center gap-2">
          <AlertCircle size={16} />
          <span>{screenShareError}</span>
        </div>
      )}

      {/* MAIN STAGE: Live Screen Presentation / Interactive Study Board */}
      <div className="rounded-3xl bg-[#1B263B] border border-[#27384E] overflow-hidden shadow-2xl">
        {/* Presentation Stage Header */}
        <div className="px-5 py-3 bg-[#131F2E] border-b border-[#27384E] flex items-center justify-between gap-3 flex-wrap">
          <div className="flex items-center gap-2">
            <span className="w-2 h-2 rounded-full bg-[#00E5FF] animate-pulse" />
            <span className="text-xs font-extrabold uppercase text-[#00E5FF]">
              {presentationMode === 'screen' && isScreenSharing
                ? '🖥️ Đang Trình Chiếu Màn Hình Trực Tiếp (Live Stream)'
                : '📖 Bảng Trình Chiếu Chuyên Đề & Bài Học Tiếng Anh'}
            </span>
          </div>

          <div className="flex items-center gap-2">
            <button
              onClick={() => setPresentationMode('screen')}
              className={`px-2.5 py-1 rounded-lg text-[11px] font-bold cursor-pointer transition-all ${
                presentationMode === 'screen'
                  ? 'bg-[#00E5FF] text-[#0D1B2A]'
                  : 'bg-[#1E2D40] text-[#778DA9]'
              }`}
            >
              Màn Hình
            </button>
            <button
              onClick={() => setPresentationMode('whiteboard')}
              className={`px-2.5 py-1 rounded-lg text-[11px] font-bold cursor-pointer transition-all ${
                presentationMode === 'whiteboard'
                  ? 'bg-[#00E5FF] text-[#0D1B2A]'
                  : 'bg-[#1E2D40] text-[#778DA9]'
              }`}
            >
              Bảng Bài Giảng
            </button>

            {isScreenSharing && (
              <button
                onClick={handleToggleFullscreen}
                className="p-1 rounded-lg bg-[#1E2D40] text-[#778DA9] hover:text-white"
                title="Toàn màn hình"
              >
                <Maximize2 size={14} />
              </button>
            )}
          </div>
        </div>

        {/* Video Canvas Stage */}
        <div className="relative min-h-[340px] sm:min-h-[420px] bg-black flex items-center justify-center overflow-hidden">
          {presentationMode === 'screen' && isScreenSharing ? (
            <video
              ref={videoRef}
              autoPlay
              playsInline
              className="w-full h-full max-h-[560px] object-contain bg-black"
            />
          ) : presentationMode === 'whiteboard' ? (
            // Interactive Whiteboard / Lesson slides
            <div className="w-full p-6 sm:p-10 text-center max-w-2xl mx-auto space-y-4">
              <span className="text-[10px] font-black uppercase tracking-widest px-3 py-1 rounded-full bg-[#9D4EDD]/20 text-[#9D4EDD] border border-[#9D4EDD]/40">
                Trang {whiteboardSlideIndex + 1} / {LESSON_SLIDES.length}
              </span>

              <h3 className="text-xl sm:text-2xl font-black text-white">
                {LESSON_SLIDES[whiteboardSlideIndex].title}
              </h3>

              <div className="p-4 rounded-2xl bg-[#131F2E] border border-[#00E5FF]/40 text-sm sm:text-base font-mono font-bold text-[#00E5FF]">
                {LESSON_SLIDES[whiteboardSlideIndex].content}
              </div>

              <p className="text-xs sm:text-sm text-[#ADB5BD] leading-relaxed">
                {LESSON_SLIDES[whiteboardSlideIndex].notes}
              </p>

              <div className="p-3 rounded-xl bg-[#1E2D40] text-xs text-[#FFD166] italic">
                Ví dụ: "{LESSON_SLIDES[whiteboardSlideIndex].example}"
              </div>

              <div className="flex items-center justify-center gap-2 pt-2">
                <button
                  onClick={() => setWhiteboardSlideIndex((prev) => Math.max(0, prev - 1))}
                  disabled={whiteboardSlideIndex === 0}
                  className="px-3.5 py-1.5 rounded-xl bg-[#131F2E] text-white text-xs font-bold disabled:opacity-40 cursor-pointer"
                >
                  ← Trang Trước
                </button>
                <button
                  onClick={() =>
                    setWhiteboardSlideIndex((prev) =>
                      Math.min(LESSON_SLIDES.length - 1, prev + 1)
                    )
                  }
                  disabled={whiteboardSlideIndex === LESSON_SLIDES.length - 1}
                  className="px-3.5 py-1.5 rounded-xl bg-[#00E5FF] text-[#0D1B2A] text-xs font-bold disabled:opacity-40 cursor-pointer"
                >
                  Trang Kế Tiếp →
                </button>
              </div>
            </div>
          ) : (
            // Placeholder when not sharing screen
            <div className="text-center p-8 space-y-4">
              <div className="w-20 h-20 mx-auto rounded-3xl bg-[#131F2E] border border-[#00E5FF]/30 flex items-center justify-center text-[#00E5FF]">
                <Monitor size={40} />
              </div>
              <div className="max-w-md mx-auto">
                <h4 className="text-base font-bold text-white">Chưa Bật Trình Chiếu Màn Hình</h4>
                <p className="text-xs text-[#778DA9] mt-1 leading-relaxed">
                  Bấm nút <strong>"Trình Chiếu Màn Hình"</strong> ở trên để chia sẻ đề thi, tài liệu hoặc tab học tập cho cả phòng cùng quan sát.
                </p>
              </div>
              <div className="flex items-center justify-center gap-3">
                <button
                  onClick={startScreenShare}
                  className="px-4 py-2 rounded-xl bg-[#00E5FF] text-[#0D1B2A] text-xs font-bold hover:bg-[#38bdf8] cursor-pointer shadow-md"
                >
                  Bắt Đầu Trình Chiếu Ngay
                </button>
                <button
                  onClick={() => setPresentationMode('whiteboard')}
                  className="px-4 py-2 rounded-xl bg-[#1E2D40] text-white text-xs font-bold hover:bg-[#27384E] cursor-pointer"
                >
                  Mở Bảng Bài Giảng
                </button>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* LOWER GRID: Participants & Mic Voice Meter (Left) + Interactive Audio Chat (Right) */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
        {/* PARTICIPANTS & VOICE VISUALIZER (2 COLS) */}
        <div className="md:col-span-2 space-y-4">
          <div className="p-5 rounded-3xl bg-[#1B263B] border border-[#27384E] space-y-4 shadow-xl">
            <div className="flex items-center justify-between">
              <h3 className="text-sm font-bold text-white flex items-center gap-2">
                <Users size={16} className="text-[#00E5FF]" />
                <span>Thành Viên Trong Phòng ({activeRoom.participants.length})</span>
              </h3>
              <span className="text-xs text-[#06D6A0] font-semibold flex items-center gap-1">
                <Radio size={14} className="animate-pulse" />
                <span>Âm thanh thời gian thực</span>
              </span>
            </div>

            {/* REAL MICROPHONE VOLUME METER (LIVE EQUALIZER) */}
            <div className="p-4 rounded-2xl bg-[#131F2E] border border-[#27384E] space-y-2.5">
              <div className="flex items-center justify-between text-xs">
                <span className="font-bold text-white flex items-center gap-1.5">
                  <Activity size={15} className="text-[#00E5FF]" />
                  <span>Cường Độ Micro Của Bạn:</span>
                </span>
                <span className="font-mono text-[11px] text-[#00E5FF]">
                  {activeRoom.isMicOn ? `${micAudioLevel}% (Đang thu âm)` : 'Đã tắt Mic'}
                </span>
              </div>

              {/* Dynamic Equalizer Bar Graph */}
              <div className="flex items-end gap-1 h-8 px-2 py-1 bg-[#0D1B2A] rounded-xl overflow-hidden border border-[#27384E]">
                {Array.from({ length: 24 }).map((_, i) => {
                  const factor = Math.sin((i / 24) * Math.PI);
                  const barHeight = activeRoom.isMicOn
                    ? Math.max(10, Math.min(100, (micAudioLevel * factor) + (Math.random() * 8)))
                    : 8;

                  return (
                    <div
                      key={i}
                      className={`flex-1 rounded-t-sm transition-all duration-75 ${
                        barHeight > 70
                          ? 'bg-[#EF476F]'
                          : barHeight > 40
                          ? 'bg-[#FFD166]'
                          : 'bg-[#00E5FF]'
                      }`}
                      style={{ height: `${barHeight}%` }}
                    />
                  );
                })}
              </div>

              <div className="flex items-center justify-between text-[11px] text-[#778DA9] pt-1">
                <span>Nói vào micro để thấy thanh sóng di chuyển</span>
                <button
                  onClick={handleToggleLoopback}
                  className={`text-[11px] font-bold cursor-pointer transition-colors ${
                    isLoopbackEnabled ? 'text-[#06D6A0]' : 'text-[#778DA9] hover:text-white'
                  }`}
                  title="Cho phép bạn nghe lại âm thanh micro của chính mình"
                >
                  {isLoopbackEnabled ? '🎧 Đang bật nghe lại giọng' : '🎧 Nghe thử giọng mình'}
                </button>
              </div>
            </div>

            {/* Participants Grid Cards */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              {activeRoom.participants.map((p) => {
                const isMe = p.friendCode === (currentUser?.friendCode || 'YOU');
                const isSpeaking = (isMe && activeRoom.isMicOn && micAudioLevel > 15) || speakingPeer === p.displayName;

                return (
                  <div
                    key={p.uid}
                    className={`p-3.5 rounded-2xl border transition-all flex items-center gap-3 relative overflow-hidden ${
                      isSpeaking
                        ? 'bg-[#1E3A4A] border-[#00E5FF] shadow-md shadow-[#00E5FF]/20'
                        : 'bg-[#131F2E] border-[#2D3F56]'
                    }`}
                  >
                    <div
                      className="w-11 h-11 rounded-xl flex items-center justify-center font-black text-sm text-[#0D1B2A] shrink-0"
                      style={{ backgroundColor: p.avatarColor || '#00E5FF' }}
                    >
                      {p.displayName.charAt(0)}
                    </div>

                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-1.5">
                        <h4 className="text-xs font-bold text-white truncate">{p.displayName}</h4>
                        {isMe && (
                          <span className="text-[9px] font-black text-[#00E5FF] bg-[#00E5FF]/20 px-1 rounded">
                            BẠN
                          </span>
                        )}
                      </div>
                      <p className="text-[11px] text-[#778DA9] truncate">{p.status}</p>
                      <span className="text-[10px] text-[#00E5FF] font-semibold">{p.currentActivity}</span>
                    </div>

                    {/* Speaking Indicator */}
                    {isSpeaking && (
                      <div className="flex items-center gap-0.5">
                        <span className="w-1 h-3 rounded-full bg-[#06D6A0] animate-pulse" />
                        <span className="w-1 h-5 rounded-full bg-[#06D6A0] animate-pulse delay-75" />
                        <span className="w-1 h-2 rounded-full bg-[#06D6A0] animate-pulse delay-150" />
                      </div>
                    )}
                  </div>
                );
              })}
            </div>

            {/* Quick Practice Voice Greetings */}
            <div className="p-3 rounded-2xl bg-[#131F2E] border border-[#27384E] space-y-2">
              <span className="text-[10px] font-bold uppercase text-[#FFD166] block">
                Tương Tác Giọng Nói Với Bạn Học (Luyện Nghe Nói):
              </span>
              <div className="flex flex-wrap gap-2">
                <button
                  onClick={() =>
                    handleSimulatePeerSpeaking(
                      'Trần Bảo Nam (10A1)',
                      'Hello everyone! Can someone explain the difference between during and while?'
                    )
                  }
                  className="px-2.5 py-1 rounded-lg bg-[#1B263B] hover:bg-[#27384E] text-white text-[11px] font-semibold transition-all cursor-pointer flex items-center gap-1"
                >
                  <Volume2 size={12} className="text-[#00E5FF]" />
                  <span>Hỏi Nam: 'During vs While'</span>
                </button>
                <button
                  onClick={() =>
                    handleSimulatePeerSpeaking(
                      'Lê Thu Hà (11A2)',
                      'The word perseverance means continuing with courage despite difficulties!'
                    )
                  }
                  className="px-2.5 py-1 rounded-lg bg-[#1B263B] hover:bg-[#27384E] text-white text-[11px] font-semibold transition-all cursor-pointer flex items-center gap-1"
                >
                  <Volume2 size={12} className="text-[#00E5FF]" />
                  <span>Hà phát biểu: Định nghĩa 'Perseverance'</span>
                </button>
              </div>
            </div>
          </div>
        </div>

        {/* LIVE ROOM CHAT & REACTIONS (1 COL) */}
        <div className="space-y-4">
          <div className="p-5 rounded-3xl bg-[#1B263B] border border-[#27384E] flex flex-col justify-between h-[450px] shadow-xl">
            <div className="flex items-center justify-between pb-3 border-b border-[#27384E]">
              <h3 className="text-sm font-bold text-white flex items-center gap-2">
                <MessageSquare size={16} className="text-[#00E5FF]" />
                <span>Trò Chuyện Trực Tuyến</span>
              </h3>
              <span className="text-[10px] text-[#778DA9]">{chatMessages.length} tin nhắn</span>
            </div>

            {/* Message stream */}
            <div className="flex-1 overflow-y-auto space-y-2.5 py-3 pr-1 text-xs">
              {chatMessages.map((msg, i) => (
                <div
                  key={i}
                  className={`p-2.5 rounded-2xl ${
                    msg.isSystem
                      ? 'bg-[#00E5FF]/10 border border-[#00E5FF]/30 text-[#00E5FF]'
                      : 'bg-[#131F2E] border border-[#27384E]'
                  }`}
                >
                  <div className="flex items-center justify-between mb-0.5">
                    <span className="font-extrabold text-white text-[11px]">{msg.sender}</span>
                    <span className="text-[9px] text-[#778DA9]">{msg.time}</span>
                  </div>
                  <p className="text-[#ADB5BD] leading-relaxed">{msg.text}</p>
                </div>
              ))}
            </div>

            {/* Fast Emoji Reactions */}
            <div className="flex items-center justify-between gap-1 py-1.5 border-t border-[#27384E]">
              {['👏 Tuyệt vời', '💡 Hiểu rồi', '🔥 Quyết tâm', '❓ Giảng lại'].map((react, i) => (
                <button
                  key={i}
                  onClick={() => {
                    setChatMessages((prev) => [
                      ...prev,
                      {
                        sender: currentUser?.displayName || 'Bạn',
                        text: react,
                        time: 'Vừa xong'
                      }
                    ]);
                  }}
                  className="px-2 py-1 rounded-lg bg-[#131F2E] hover:bg-[#27384E] text-[10px] font-bold text-[#ADB5BD] hover:text-white transition-all cursor-pointer"
                >
                  {react}
                </button>
              ))}
            </div>

            {/* Input Form */}
            <form onSubmit={handleSendMessage} className="flex gap-2 pt-2">
              <input
                type="text"
                value={typedMessage}
                onChange={(e) => setTypedMessage(e.target.value)}
                placeholder="Nhắn tin cho cả phòng..."
                className="flex-1 px-3 py-2 rounded-xl bg-[#131F2E] border border-[#27384E] focus:outline-hidden focus:border-[#00E5FF] text-white text-xs"
              />
              <button
                type="submit"
                disabled={!typedMessage.trim()}
                className="px-3 py-2 rounded-xl bg-[#00E5FF] text-[#0D1B2A] font-bold text-xs hover:bg-[#38bdf8] transition-all disabled:opacity-40 cursor-pointer"
              >
                <Send size={14} />
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};
