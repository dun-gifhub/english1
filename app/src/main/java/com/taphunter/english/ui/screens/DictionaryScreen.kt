package com.taphunter.english.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.taphunter.english.data.models.DictionaryEntry
import com.taphunter.english.data.repository.EnglishRepository
import com.taphunter.english.ui.theme.*

@Composable
fun DictionaryScreen(
    englishRepository: EnglishRepository,
    onWordSelectedForPractice: (String) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var isEnglishToVietnamese by remember { mutableStateOf(true) }

    val results = remember(searchQuery, isEnglishToVietnamese) {
        englishRepository.searchDictionary(searchQuery, isEnglishToVietnamese)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dictionary_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Mode Header & Direction Switcher
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Navy800),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CyanAccent, GoldYellow)))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Translate, contentDescription = null, tint = CyanAccent)
                        Text(
                            text = "Từ Điển Song Ngữ Lớp 6 - 12",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    // Toggle Direction
                    FilterChip(
                        selected = true,
                        onClick = { isEnglishToVietnamese = !isEnglishToVietnamese },
                        label = {
                            Text(
                                text = if (isEnglishToVietnamese) "Anh ➔ Việt" else "Việt ➔ Anh",
                                fontWeight = FontWeight.Bold,
                                color = Navy900
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = null,
                                tint = Navy900,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldYellow
                        ),
                        modifier = Modifier.testTag("toggle_dictionary_direction")
                    )
                }

                // Search Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = if (isEnglishToVietnamese) "Nhập từ tiếng Anh (VD: resilient, breadwinner...)" else "Nhập từ tiếng Việt (VD: kiên cường, trụ cột...)",
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Tìm kiếm", tint = CyanAccent)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Xóa", tint = SlateBlue)
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dictionary_search_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface
                    )
                )
            }
        }

        // Result Count Banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (searchQuery.isBlank()) "Từ vựng trọng tâm thường gặp" else "Tìm thấy ${results.size} kết quả",
                color = SlateBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = if (isEnglishToVietnamese) "Chế độ: Anh - Việt" else "Chế độ: Việt - Anh",
                color = CyanAccent,
                fontSize = 12.sp
            )
        }

        // Results List
        if (results.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.SearchOff, contentDescription = null, tint = SlateBlue, modifier = Modifier.size(48.dp))
                    Text(
                        text = "Không tìm thấy từ \"$searchQuery\"",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Hãy thử đổi hướng tra hoặc kiểm tra lại chính tả.",
                        color = SlateBlue,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("dictionary_result_list"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(results) { entry ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dict_card_${entry.wordEn}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkBorder, DarkBorder)))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Word & Phonetic & Part of speech
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = entry.wordEn,
                                        color = CyanAccent,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Navy700)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = entry.partOfSpeech,
                                            color = GoldYellow,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Dictionary Word", "${entry.wordEn}: ${entry.meaningVi}")
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Đã sao chép từ ${entry.wordEn}!", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Sao chép", tint = SlateBlue, modifier = Modifier.size(18.dp))
                                }
                            }

                            // Phonetic
                            Text(
                                text = entry.phonetic,
                                color = GoldYellow,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )

                            // Vietnamese Meaning
                            Text(
                                text = "➜ ${entry.meaningVi}",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            // Example Sentence
                            if (entry.exampleEn.isNotBlank()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Navy800)
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Ví dụ: ${entry.exampleEn}",
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        lineHeight = 17.sp
                                    )
                                    Text(
                                        text = "Dịch: ${entry.exampleVi}",
                                        color = SlateBlue,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            // Synonyms
                            if (entry.synonyms.isNotEmpty()) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Đồng nghĩa:",
                                        color = SlateBlue,
                                        fontSize = 12.sp
                                    )
                                    entry.synonyms.forEach { syn ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Navy700)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(syn, color = TextSecondary, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
