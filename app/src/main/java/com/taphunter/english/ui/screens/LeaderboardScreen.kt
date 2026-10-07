package com.taphunter.english.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taphunter.english.data.models.LeaderboardEntry
import com.taphunter.english.ui.theme.*

@Composable
fun LeaderboardScreen(
    entries: List<LeaderboardEntry>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("leaderboard_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Navy800),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GoldYellow, PurpleNeon)))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = GoldYellow,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "BẢNG VÀNG THỢ SĂN TỪ VỰNG",
                        color = GoldYellow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Top Học Sinh Giỏi & Chuyên Anh Toàn Quốc",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Leaderboard List
        items(entries) { entry ->
            val isTop1 = entry.rank == 1
            val isTop2 = entry.rank == 2
            val isTop3 = entry.rank == 3
            val isUser = entry.name.contains("Bạn")

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
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
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
                            Text(
                                text = entry.name,
                                color = if (isUser) CyanAccent else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Cấp ${entry.level}",
                                    color = SlateBlue,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "• ${entry.badge}",
                                    color = GoldYellow,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Text(
                        text = "${entry.score} PTS",
                        color = if (isTop1) GoldYellow else CyanAccent,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}
