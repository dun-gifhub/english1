package com.taphunter.english.data.repository

import com.taphunter.english.data.models.CallRoom
import com.taphunter.english.data.models.Friend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FriendsRepository {
    private val _friends = MutableStateFlow<List<Friend>>(emptyList())
    val friends: StateFlow<List<Friend>> = _friends.asStateFlow()

    private val _activeRoom = MutableStateFlow<CallRoom?>(null)
    val activeRoom: StateFlow<CallRoom?> = _activeRoom.asStateFlow()

    private val _friendMessage = MutableStateFlow<String?>(null)
    val friendMessage: StateFlow<String?> = _friendMessage.asStateFlow()

    fun addFriendByCode(code: String): Boolean {
        val trimmed = code.trim().uppercase()
        if (trimmed.length < 6) {
            _friendMessage.value = "Mã kết bạn không hợp lệ (Ví dụ: LP-1234)"
            return false
        }
        if (_friends.value.any { it.friendCode.equals(trimmed, ignoreCase = true) }) {
            _friendMessage.value = "Bạn đã có người bạn này trong danh sách!"
            return false
        }

        val newFriend = Friend(
            uid = "u_${System.currentTimeMillis()}",
            friendCode = trimmed,
            displayName = "Bạn học $trimmed",
            status = "Học sinh THPT Lương Phú",
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
                Friend("host", "YOU", hostName, "Trưởng phòng", true, "Đang chủ trì", 0xFF00E5FF)
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
            hostName = "Chủ phòng #$trimmed",
            participants = listOf(
                Friend("me", "YOU", userName, "Thành viên", true, "Vừa tham gia", 0xFF00E5FF)
            )
        )
        _activeRoom.value = room
        return true
    }

    fun leaveRoom() {
        _activeRoom.value = null
    }
}
