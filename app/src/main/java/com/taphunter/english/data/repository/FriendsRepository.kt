package com.taphunter.english.data.repository

import com.taphunter.english.data.models.CallRoom
import com.taphunter.english.data.models.Friend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FriendsRepository {
    private val _friends = MutableStateFlow<List<Friend>>(
        listOf(
            Friend(
                uid = "u_duc",
                friendCode = "TAP-8841",
                displayName = "Minh Đức (Chuyên Anh)",
                status = "Đang leo rank HSG",
                isOnline = true,
                currentActivity = "Đang chơi Unit 1: Idioms",
                avatarColor = 0xFF00E5FF
            ),
            Friend(
                uid = "u_haianh",
                friendCode = "TAP-3912",
                displayName = "Hải Anh",
                status = "Trong phòng gọi nhóm",
                isOnline = true,
                currentActivity = "Phòng Gọi #7890",
                avatarColor = 0xFFFFD166
            ),
            Friend(
                uid = "u_baonam",
                friendCode = "TAP-6520",
                displayName = "Bảo Nam",
                status = "Sẵn sàng săn từ",
                isOnline = true,
                currentActivity = "Online - Đang đợi thách đấu",
                avatarColor = 0xFF06D6A0
            ),
            Friend(
                uid = "u_phuong",
                friendCode = "TAP-1129",
                displayName = "Thu Phương",
                status = "Nghỉ ngơi",
                isOnline = false,
                currentActivity = "Offline 15 phút trước",
                avatarColor = 0xFF9D4EDD
            )
        )
    )
    val friends: StateFlow<List<Friend>> = _friends.asStateFlow()

    private val _activeRoom = MutableStateFlow<CallRoom?>(null)
    val activeRoom: StateFlow<CallRoom?> = _activeRoom.asStateFlow()

    private val _friendMessage = MutableStateFlow<String?>(null)
    val friendMessage: StateFlow<String?> = _friendMessage.asStateFlow()

    fun addFriendByCode(code: String): Boolean {
        val trimmed = code.trim().uppercase()
        if (trimmed.length < 6) {
            _friendMessage.value = "Mã kết bạn không hợp lệ (Ví dụ: TAP-1234)"
            return false
        }
        if (_friends.value.any { it.friendCode.equals(trimmed, ignoreCase = true) }) {
            _friendMessage.value = "Bạn đã có người bạn này trong danh sách!"
            return false
        }

        val newFriend = Friend(
            uid = "u_${System.currentTimeMillis()}",
            friendCode = trimmed,
            displayName = "Thợ Săn $trimmed",
            status = "Vừa kết bạn",
            isOnline = true,
            currentActivity = "Online",
            avatarColor = 0xFFFF70A6
        )
        _friends.value = listOf(newFriend) + _friends.value
        _friendMessage.value = "Đã kết bạn thành công với $trimmed!"
        return true
    }

    fun clearMessage() {
        _friendMessage.value = null
    }

    fun createRoom(hostName: String): CallRoom {
        val roomCode = (100000..999999).random().toString()
        val room = CallRoom(
            roomId = "room_${System.currentTimeMillis()}",
            roomCode = roomCode,
            title = "Phòng Học & Luyện Phản Xạ #$roomCode",
            hostName = hostName,
            participants = listOf(
                Friend("host", "YOU", hostName, "Trưởng phòng", true, "Đang chủ trì", 0xFF00E5FF),
                Friend("f1", "TAP-8841", "Minh Đức", "Thành viên", true, "Đang nghe", 0xFFFFD166),
                Friend("f2", "TAP-3912", "Hải Anh", "Thành viên", true, "Đang phát biểu", 0xFF06D6A0)
            )
        )
        _activeRoom.value = room
        return room
    }

    fun joinRoom(code: String, userName: String): Boolean {
        val trimmed = code.trim()
        if (trimmed.length < 4) {
            _friendMessage.value = "Mã phòng phải có từ 4 đến 6 chữ số!"
            return false
        }
        val room = CallRoom(
            roomId = "room_$trimmed",
            roomCode = trimmed,
            title = "Phòng Học Nhóm HSG #$trimmed",
            hostName = "Minh Đức (Chuyên Anh)",
            participants = listOf(
                Friend("f1", "TAP-8841", "Minh Đức (Chủ phòng)", "Online", true, "Đang nói", 0xFF00E5FF),
                Friend("me", "YOU", userName, "Online", true, "Vừa tham gia", 0xFFFFD166),
                Friend("f2", "TAP-3912", "Hải Anh", "Online", true, "Sẵn sàng", 0xFF06D6A0)
            )
        )
        _activeRoom.value = room
        return true
    }

    fun leaveRoom() {
        _activeRoom.value = null
    }
}
