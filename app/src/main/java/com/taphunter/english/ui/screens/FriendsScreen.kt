package com.taphunter.english.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taphunter.english.data.models.CallRoom
import com.taphunter.english.data.models.Friend
import com.taphunter.english.data.models.UserProfile
import com.taphunter.english.ui.theme.*

@Composable
fun FriendsScreen(
    user: UserProfile?,
    friends: List<Friend>,
    activeRoom: CallRoom?,
    statusMessage: String?,
    onAddFriend: (String) -> Unit,
    onCreateRoom: () -> Unit,
    onJoinRoom: (String) -> Unit,
    onOpenCallRoom: () -> Unit,
    onClearMessage: () -> Unit
) {
    val context = LocalContext.current
    var friendCodeInput by remember { mutableStateOf("") }
    var roomCodeInput by remember { mutableStateOf("") }
    var showQrDialog by remember { mutableStateOf(false) }

    LaunchedEffect(statusMessage) {
        if (statusMessage != null) {
            Toast.makeText(context, statusMessage, Toast.LENGTH_SHORT).show()
            onClearMessage()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("friends_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // My Friend Code Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("my_friend_code_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Navy800),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CyanAccent, GoldYellow)))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "MÃ KẾT BẠN CỦA BẠN",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = user?.friendCode ?: "TAP-1000",
                                color = GoldYellow,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilledTonalIconButton(
                                onClick = { showQrDialog = true },
                                colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Navy700),
                                modifier = Modifier.testTag("open_qr_button")
                            ) {
                                Icon(imageVector = Icons.Default.QrCode, contentDescription = "Xem QR", tint = CyanAccent)
                            }

                            FilledTonalIconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Friend Code", user?.friendCode ?: "")
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Đã sao chép mã!", Toast.LENGTH_SHORT).show()
                                },
                                colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Navy700),
                                modifier = Modifier.testTag("copy_my_code_button")
                            ) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Sao chép", tint = GoldYellow)
                            }
                        }
                    }

                    Text(
                        text = "Chia sẻ mã hoặc quét QR để bạn bè cùng lớp kết bạn và đấu phản xạ từ vựng.",
                        color = SlateBlue,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Room Hub (Create or Join Call & Study Room)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("room_hub_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Groups, contentDescription = null, tint = CyanAccent)
                        Text(
                            text = "Phòng Học Nhóm & Call Room",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    if (activeRoom != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Navy700)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Đang trong: ${activeRoom.title}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Mã phòng: #${activeRoom.roomCode} • ${activeRoom.participants.size} bạn", color = GreenSuccess, fontSize = 12.sp)
                                }
                                Button(
                                    onClick = onOpenCallRoom,
                                    colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("enter_active_room_button")
                                ) {
                                    Text("Vào Phòng", color = Navy900, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    onCreateRoom()
                                    onOpenCallRoom()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("create_room_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Navy900)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Tạo Phòng", color = Navy900, fontWeight = FontWeight.Bold)
                            }

                            Row(
                                modifier = Modifier.weight(1.2f),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedTextField(
                                    value = roomCodeInput,
                                    onValueChange = { if (it.length <= 6) roomCodeInput = it },
                                    placeholder = { Text("Mã phòng", fontSize = 12.sp) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f).height(48.dp).testTag("room_code_input"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = CyanAccent,
                                        unfocusedBorderColor = DarkBorder
                                    )
                                )

                                FilledIconButton(
                                    onClick = {
                                        if (roomCodeInput.isNotBlank()) {
                                            onJoinRoom(roomCodeInput)
                                            onOpenCallRoom()
                                            roomCodeInput = ""
                                        }
                                    },
                                    modifier = Modifier.size(48.dp).testTag("join_room_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = GoldYellow)
                                ) {
                                    Icon(imageVector = Icons.Default.Login, contentDescription = "Tham gia", tint = Navy900)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Friend Input
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = friendCodeInput,
                        onValueChange = { friendCodeInput = it.uppercase() },
                        placeholder = { Text("Nhập mã bạn bè (VD: TAP-8841)", fontSize = 13.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("add_friend_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = DarkBorder
                        )
                    )

                    Button(
                        onClick = {
                            if (friendCodeInput.isNotBlank()) {
                                onAddFriend(friendCodeInput)
                                friendCodeInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(52.dp).testTag("confirm_add_friend_button")
                    ) {
                        Text("Kết Bạn", color = Navy900, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Friends List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bạn Bè & Trạng Thái (${friends.size})",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${friends.count { it.isOnline }} Online",
                    color = GreenSuccess,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Friends Items
        items(friends) { friend ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("friend_item_${friend.friendCode}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkBorder, DarkBorder)))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(friend.avatarColor)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = friend.displayName.take(1).uppercase(),
                                    color = Navy900,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                            // Online Dot
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(if (friend.isOnline) GreenSuccess else SlateBlue)
                                    .align(Alignment.BottomEnd)
                                    .border(2.dp, Navy900, CircleShape)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = friend.displayName,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = friend.currentActivity,
                                color = if (friend.isOnline) CyanAccent else SlateBlue,
                                fontSize = 12.sp
                            )
                            Text(
                                text = friend.friendCode,
                                color = GoldYellow,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = {
                                Toast.makeText(context, "Đã gửi lời mời thách đấu tới ${friend.displayName}!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("invite_friend_${friend.friendCode}")
                        ) {
                            Icon(imageVector = Icons.Default.SportsEsports, contentDescription = "Thách đấu", tint = GoldYellow)
                        }
                    }
                }
            }
        }
    }

    // QR Code Dialog
    if (showQrDialog) {
        AlertDialog(
            onDismissRequest = { showQrDialog = false },
            confirmButton = {
                TextButton(onClick = { showQrDialog = false }) {
                    Text("Đóng", color = CyanAccent)
                }
            },
            title = {
                Text(
                    text = "Mã QR Của Bạn",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = null,
                                tint = Navy900,
                                modifier = Modifier.size(120.dp)
                            )
                            Text(
                                text = user?.friendCode ?: "TAP-1000",
                                color = Navy900,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                    Text(
                        text = "Quét để kết bạn ngay trong Tap Hunter",
                        color = SlateBlue,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            },
            containerColor = DarkCard
        )
    }
}
