package com.taphunter.english.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.taphunter.english.data.models.DictionaryEntry
import com.taphunter.english.data.repository.EnglishRepository
import com.taphunter.english.ui.theme.*
import java.util.Locale

@Composable
fun DictionaryScreen(
    englishRepository: EnglishRepository,
    onWordSelectedForPractice: (String) -> Unit
) {
    val context = LocalContext.current

    // Initialize TextToSpeech for pronunciation
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    var isTtsReady by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                isTtsReady = true
            }
        }
        tts = ttsInstance
        onDispose {
            ttsInstance.stop()
            ttsInstance.shutdown()
        }
    }

    fun speak(text: String, isEnglish: Boolean) {
        if (!isTtsReady || text.isBlank()) return
        tts?.language = if (isEnglish) Locale.US else Locale("vi", "VN")
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "dict_tts")
    }

    var inputText by remember { mutableStateOf("") }
    var isEnglishToVietnamese by remember { mutableStateOf(true) }
    var savedWords by remember { mutableStateOf<Set<String>>(emptySet()) }
    var activeTab by remember { mutableIntStateOf(0) } // 0: Dịch & Tra Cứu, 1: Từ Đã Lưu

    // Matching dictionary entries
    val searchResults = remember(inputText, isEnglishToVietnamese) {
        englishRepository.searchDictionary(inputText, isEnglishToVietnamese)
    }

    val bestMatchEntry = remember(searchResults, inputText) {
        if (inputText.isBlank()) null
        else searchResults.firstOrNull()
    }

    // Dynamic Translation output (like Google Translate)
    val translationOutput = remember(inputText, isEnglishToVietnamese, bestMatchEntry) {
        val trimmed = inputText.trim()
        if (trimmed.isBlank()) {
            ""
        } else if (bestMatchEntry != null) {
            if (isEnglishToVietnamese) bestMatchEntry.meaningVi else bestMatchEntry.wordEn
        } else {
            val fallback = com.taphunter.english.data.repository.ComprehensiveDictionary.synthesizeEntry(trimmed, isEnglishToVietnamese)
            if (isEnglishToVietnamese) fallback.meaningVi else fallback.wordEn
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dictionary_screen")
            .background(Navy900)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Google Translate-like Language Switcher Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Navy800),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.horizontalGradient(listOf(CyanAccent, GoldYellow))
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Source Language
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (isEnglishToVietnamese) "Tiếng Anh (EN)" else "Tiếng Việt (VI)",
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Swap Button in the center (Google style)
                IconButton(
                    onClick = {
                        isEnglishToVietnamese = !isEnglishToVietnamese
                        if (translationOutput.isNotBlank() && !translationOutput.startsWith("Bản dịch:")) {
                            inputText = translationOutput
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Navy700)
                        .testTag("swap_languages_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Hoán đổi ngôn ngữ",
                        tint = GoldYellow,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Target Language
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (isEnglishToVietnamese) "Tiếng Việt (VI)" else "Tiếng Anh (EN)",
                        color = GoldYellow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.End
                    )
                }
            }
        }

        // Sub-tabs (Dịch / Tra từ vs Từ đã lưu)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                label = { Text("Tra Từ & Dịch Như GG") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyanAccent,
                    selectedLabelColor = Navy900
                )
            )

            FilterChip(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                label = { Text("Đã Lưu (${savedWords.size})") },
                leadingIcon = {
                    Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GoldYellow,
                    selectedLabelColor = Navy900
                )
            )
        }

        if (activeTab == 0) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Source Input Card (Google Translate Box)
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("source_input_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(DarkBorder, DarkBorder))
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isEnglishToVietnamese) "Văn bản tiếng Anh" else "Văn bản tiếng Việt",
                                    color = SlateBlue,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                if (inputText.isNotEmpty()) {
                                    IconButton(
                                        onClick = { inputText = "" },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Xoá văn bản",
                                            tint = SlateBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            // Big Text Area
                            OutlinedTextField(
                                value = inputText,
                                onValueChange = { inputText = it },
                                placeholder = {
                                    Text(
                                        text = if (isEnglishToVietnamese) "Nhập từ hoặc câu tiếng Anh (VD: resilience, dedication, hello)..." else "Nhập từ hoặc câu tiếng Việt (VD: kiên cường, trường học, cống hiến)...",
                                        fontSize = 14.sp,
                                        color = SlateBlue
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("dictionary_search_input"),
                                minLines = 2,
                                maxLines = 4,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )

                            // Quick actions row: Speaker TTS, Paste, Character count
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    // Pronounce Source Text
                                    IconButton(
                                        onClick = { speak(inputText, isEnglishToVietnamese) },
                                        enabled = inputText.isNotBlank(),
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = "Phát âm văn bản gốc",
                                            tint = if (inputText.isNotBlank()) CyanAccent else SlateBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    // Paste from clipboard
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = clipboard.primaryClip
                                            if (clip != null && clip.itemCount > 0) {
                                                val text = clip.getItemAt(0).text?.toString() ?: ""
                                                inputText = text
                                            }
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentPaste,
                                            contentDescription = "Dán",
                                            tint = SlateBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = "${inputText.length}/500",
                                    color = SlateBlue,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // 2. Google Translate Result Card (Target Translation Box)
                if (inputText.isNotBlank()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("translation_result_card"),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Navy800),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.horizontalGradient(listOf(Color(0xFF2E7D32), CyanAccent))
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = if (isEnglishToVietnamese) "Bản dịch tiếng Việt" else "Bản dịch tiếng Anh",
                                    color = GoldYellow,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = translationOutput,
                                    color = TextPrimary,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 26.sp
                                )

                                if (bestMatchEntry != null && isEnglishToVietnamese) {
                                    Text(
                                        text = bestMatchEntry.phonetic,
                                        color = GoldYellow,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 4.dp))

                                // Action Tools (Pronounce Result, Copy, Star, Share)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        // Speak translated text
                                        IconButton(
                                            onClick = { speak(translationOutput, !isEnglishToVietnamese) },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.VolumeUp,
                                                contentDescription = "Phát âm kết quả dịch",
                                                tint = GoldYellow,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        // Copy translated text
                                        IconButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val clip = ClipData.newPlainText("Bản dịch", translationOutput)
                                                clipboard.setPrimaryClip(clip)
                                                Toast.makeText(context, "Đã sao chép bản dịch!", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Sao chép",
                                                tint = CyanAccent,
                                                modifier = Modifier.size(19.dp)
                                            )
                                        }

                                        // Bookmark / Star
                                        val wordToSave = bestMatchEntry?.wordEn ?: inputText.trim()
                                        val isSaved = savedWords.contains(wordToSave)
                                        IconButton(
                                            onClick = {
                                                savedWords = if (isSaved) savedWords - wordToSave else savedWords + wordToSave
                                                Toast.makeText(
                                                    context,
                                                    if (isSaved) "Đã bỏ lưu từ" else "Đã lưu vào danh sách từ vựng!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isSaved) Icons.Default.Star else Icons.Default.StarBorder,
                                                contentDescription = "Lưu từ",
                                                tint = if (isSaved) GoldYellow else SlateBlue,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    // Share button
                                    IconButton(
                                        onClick = {
                                            val sendIntent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(Intent.EXTRA_TEXT, "$inputText ➔ $translationOutput")
                                                type = "text/plain"
                                            }
                                            context.startActivity(Intent.createChooser(sendIntent, "Chia sẻ từ vựng"))
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Chia sẻ",
                                            tint = SlateBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. Detailed Dictionary Section (Definitions, Parts of speech, Examples)
                if (bestMatchEntry != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkCard)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "CHI TIẾT TỪ ĐIỂN",
                                        color = CyanAccent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Navy700)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = bestMatchEntry.partOfSpeech.uppercase(),
                                            color = GoldYellow,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                if (bestMatchEntry.exampleEn.isNotBlank()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Navy800)
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = "Ví dụ câu:",
                                                color = SlateBlue,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            IconButton(
                                                onClick = { speak(bestMatchEntry.exampleEn, true) },
                                                modifier = Modifier.size(20.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.VolumeUp,
                                                    contentDescription = "Nghe câu ví dụ",
                                                    tint = CyanAccent,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = "“${bestMatchEntry.exampleEn}”",
                                            color = TextPrimary,
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp,
                                            fontWeight = FontWeight.Medium
                                        )

                                        Text(
                                            text = "➜ ${bestMatchEntry.exampleVi}",
                                            color = GoldYellow,
                                            fontSize = 12.sp,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }

                                if (bestMatchEntry.synonyms.isNotEmpty()) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("Từ đồng nghĩa:", color = SlateBlue, fontSize = 12.sp)
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            bestMatchEntry.synonyms.forEach { syn ->
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(Navy700)
                                                        .clickable {
                                                            inputText = syn
                                                            isEnglishToVietnamese = true
                                                        }
                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Text(syn, color = CyanAccent, fontSize = 11.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Suggested words list (Quick tap to translate)
                item {
                    Text(
                        text = if (inputText.isBlank()) "Từ vựng THPT Lương Phú hay gặp:" else "Gợi ý từ liên quan:",
                        color = SlateBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                items(searchResults.take(8)) { entry ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                inputText = if (isEnglishToVietnamese) entry.wordEn else entry.meaningVi
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCard)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = entry.wordEn,
                                    color = CyanAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = entry.meaningVi,
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                            IconButton(onClick = { speak(entry.wordEn, true) }) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    tint = SlateBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Saved Words Tab
            if (savedWords.isEmpty()) {
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
                        Icon(Icons.Default.BookmarkBorder, contentDescription = null, tint = SlateBlue, modifier = Modifier.size(44.dp))
                        Text("Chưa có từ nào được lưu!", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Bấm vào biểu tượng ngôi sao để lưu lại các từ cần ôn luyện.", color = SlateBlue, fontSize = 12.sp, textAlign = TextAlign.Center)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(savedWords.toList()) { word ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkCard)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(word, color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Row {
                                    IconButton(onClick = { speak(word, true) }) {
                                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = CyanAccent)
                                    }
                                    IconButton(onClick = { savedWords = savedWords - word }) {
                                        Icon(Icons.Default.Delete, contentDescription = null, tint = RedDanger)
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

