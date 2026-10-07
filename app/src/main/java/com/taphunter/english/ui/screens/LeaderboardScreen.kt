package com.taphunter.english.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taphunter.english.data.models.LeaderboardEntry
import com.taphunter.english.data.models.UserProfile
import com.taphunter.english.ui.theme.*

@Composable
fun LeaderboardScreen(
    monthlyEntries: List<LeaderboardEntry>,
    allTimeEntries: List<LeaderboardEntry>,
    currentUser: UserProfile?
) {
    var isMonthlyTab by remember { mutableStateOf(true) }

    val calendar = remember { java.util.Calendar.getInstance() }
    val currentMonth = remember { calendar.get(java.util.Calendar.MONTH) + 1 }
    val currentYear = remember { calendar.get(java.util.Calendar.YEAR) }
    val maxDay = remember { calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH) }
    val currentDay = remember { calendar.get(java.util.Calendar.DAY_OF_MONTH) }
    val daysRemaining = remember { maxOf(1, maxDay - currentDay) }

    val currentEntries = if (isMonthlyTab) monthlyEntries else allTimeEntries

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("leaderboard_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Navy800),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(GoldYellow, Color(0xFF2E7D32)))
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = GoldYellow,
                        modifier = Modifier.size(44.dp)
                    )
                    Text(
                        text = "BẢNG VÀNG THỢ SĂN LƯƠNG PHÚ",
                        color = GoldYellow,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Vinh Danh Học Sinh Xuất Sắc THPT Lương Phú",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Monthly Reset Countdown Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Navy700),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GoldYellow.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = GoldYellow,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Chu kỳ xếp hạng: Tự động reset mỗi tháng",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Bảng xếp hạng Tháng $currentMonth/$currentYear sẽ tự động làm mới về 0 điểm sau $daysRemaining ngày nữa.",
                            color = SlateBlue,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // Tab Switcher (Tháng này vs Toàn thời gian)
        item {
            TabRow(
                selectedTabIndex = if (isMonthlyTab) 0 else 1,
                containerColor = Navy800,
                contentColor = CyanAccent,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = isMonthlyTab,
                    onClick = { isMonthlyTab = true },
                    text = {
                        Text(
                            "Tháng Này (T$currentMonth/$currentYear)",
                            fontWeight = if (isMonthlyTab) FontWeight.Bold else FontWeight.Normal,
                            color = if (isMonthlyTab) CyanAccent else SlateBlue,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = !isMonthlyTab,
                    onClick = { isMonthlyTab = false },
                    text = {
                        Text(
                            "Toàn Thời Gian",
                            fontWeight = if (!isMonthlyTab) FontWeight.Bold else FontWeight.Normal,
                            color = if (!isMonthlyTab) CyanAccent else SlateBlue,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        // Empty state
        if (currentEntries.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "Chưa có điểm xếp hạng trong chu kỳ này!",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Hãy tham gia game 'Đấu Từ Phản Xạ' hoặc làm bài kiểm tra để ghi điểm và vinh danh trên Bảng Vàng!",
                            color = SlateBlue,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            // Leaderboard Entries
            items(currentEntries) { entry ->
                val isTop1 = entry.rank == 1
                val isTop2 = entry.rank == 2
                val isTop3 = entry.rank == 3
                val isUser = currentUser != null && entry.name == currentUser.displayName

                val rankColor = when {
                    isTop1 -> GoldYellow
                    isTop2 -> Color(0xFFC0C0C0)
                    isTop3 -> Color(0xFFCD7F32)
                    else -> SlateBlue
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("leaderboard_item_${entry.rank}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUser) Navy700 else DarkCard
                    ),
                    border = if (isUser) {
                        CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CyanAccent, GoldYellow)))
                    } else null
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
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(rankColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "#${entry.rank}",
                                    color = rankColor,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = entry.name,
                                        color = if (isUser) CyanAccent else TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    if (isUser) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(CyanAccent.copy(alpha = 0.2f))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text("Bạn", color = CyanAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = entry.className,
                                        color = GoldYellow,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "• Cấp ${entry.level}",
                                        color = SlateBlue,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "• ${entry.badge}",
                                        color = CyanAccent,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        val displayPoints = if (isMonthlyTab) entry.monthlyScore else entry.score
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "$displayPoints PTS",
                                color = if (isTop1) GoldYellow else CyanAccent,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = if (isMonthlyTab) "Điểm tháng" else "Kỷ lục",
                                color = SlateBlue,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

