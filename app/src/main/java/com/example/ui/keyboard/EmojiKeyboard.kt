package com.example.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class EmojiCategory(
    val name: String,
    val icon: String,
    val list: List<String>
)

object EmojiData {
    val categories = listOf(
        EmojiCategory(
            name = "Faces",
            icon = "😀",
            list = listOf(
                "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😊", "😇",
                "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗", "😙", "😚",
                "😋", "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🥸",
                "🤩", "🥳", "😏", "😒", "😞", "😔", "😟", "😕", "🙁", "☹️",
                "😣", "😖", "😫", "😩", "🥺", "😢", "😭", "😤", "😠", "😡",
                "🤬", "🤯", "😳", "🥵", "🥶", "😱", "😨", "😰", "😥", "😓",
                "🤖", "👽", "👾", "💩", "🤡", "👹", "👺", "👻", "💀", "☠️",
                "🤫", "🤭", "🤔", "😬", "🤥", "🫥", "🫠", "🫣", "🫡", "🥱"
            )
        ),
        EmojiCategory(
            name = "Gestures",
            icon = "👍",
            list = listOf(
                "👋", "🤚", "🖐️", "✋", "🖖", "👌", "🤌", "🤏", "✌️", "🤞",
                "🤟", "🤘", "🤙", "👈", "👉", "👆", "🖕", "👇", "☝️", "👍",
                "👎", "✊", "👊", "🤛", "🤜", "👏", "🙌", "👐", "🤲", "🤝",
                "🙏", "✍️", "💅", "🤳", "💪", "🦾", "👂", "🦻", "👃", "🧠",
                "🫀", "🫁", "🦷", "👁️", "👀", "🗣️", "👤", "👥", "🫂"
            )
        ),
        EmojiCategory(
            name = "Animals",
            icon = "🐶",
            list = listOf(
                "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯",
                "🦁", "🐮", "🐷", "🐽", "🐸", "🐵", "🙊", "🙉", "🙈", "🐒",
                "🐔", "🐧", "🐦", "🐤", "🐣", "🐥", "🦆", "🦅", "🦉", "🦇",
                "🐺", "🐗", "🐴", "🦄", "🐝", "🪱", "🐛", "🦋", "🐌", "🐞",
                "🐜", "🦟", "🕷️", "🕸️", "Scorpion", "🐢", "🐍", "🦎", "🐙", "🦑",
                "🦞", "🦀", "🐡", "🐠", "🐟", "🐬", "🐳", "🐋", "🦈", "🐊"
            )
        ),
        EmojiCategory(
            name = "Food",
            icon = "🍎",
            list = listOf(
                "🍏", "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐",
                "🍒", "🍑", "🥭", "🍍", "🥥", "🥝", "🍅", "🍆", "🥑", "🥦",
                "🥬", "🥒", "🌶️", "🫑", "🌽", "🥕", "🫒", "🧄", "🧅", "🍄",
                "🥜", "🌰", "🍞", "🥐", "🥖", "🫓", "🥨", "🥯", "🥞", " waffle",
                "🧀", "🍖", "🥩", "🍔", "🍟", "🍕", "🌭", "🥪", "🌮", "🌯",
                "🥚", "🍳", "🥘", "🍲", "🥣", "🥗", "🍿", "バター", "🧂", "🥫"
            )
        ),
        EmojiCategory(
            name = "Travel",
            icon = "🚗",
            list = listOf(
                "🚗", "🚕", "🚙", "🚌", "🚎", "🏎️", "🚓", "🚒", "🚑", "🚐",
                "🛻", "🚚", "🚛", "🚜", "🛵", "🚲", "🛴", "🚏", "🚂", "🚈",
                "🚄", "🚀", "🛸", "🚁", "✈️", "🗺️", "🏔️", "🌋", "🏖️", "⛺",
                "🏠", "🏢", "⛪", "🏨", "🏰", "🗼", "🗽", "🏟️", "🎡", "🎢"
            )
        ),
        EmojiCategory(
            name = "Objects",
            icon = "💡",
            list = listOf(
                "⌚", "📱", "📲", "💻", "⌨️", "🖥️", "🖨️", "🖱️", "📷", "📽️",
                "🎙️", "🔭", "🔬", "📡", "🕯️", "💡", "🔦", "🏮", "📕", "📗",
                "📘", "📙", "📚", "📓", "📝", "✉️", "📦", "✏️", "✒️", "📁",
                "💼", "📅", "📊", "📌", "🔒", "🔑", "🔨", "🛡️", "💊", "🧲"
            )
        ),
        EmojiCategory(
            name = "Symbols",
            icon = "❤️",
            list = listOf(
                "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔",
                "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟", "☮️",
                "✝️", "☪️", "🕉️", "☸️", "🏳️‍🌈", "🏳️‍⚧️", "💯", "💢", "💥", "💫",
                "🌀", "💤", "⚠️", "⛔", "🔇", "🔔", "⭐", "🌟", "🔥", "✨"
            )
        )
    )

    val skinTones = listOf(
        "" to "Default",
        "🏻" to "Light",
        "🏼" to "Medium-Light",
        "🏽" to "Medium",
        "🏾" to "Medium-Dark",
        "🏿" to "Dark"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmojiKeyboard(
    textColor: Color,
    accentColor: Color,
    keyColor: Color,
    onEmojiSelected: (String) -> Unit,
    onSwitchToAlphabet: () -> Unit,
    onBackspace: () -> Unit,
    onSwitchKeyboard: (() -> Unit)? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    var selectedSkinToneModifier by remember { mutableStateOf("") }

    // History list in SharedPreferences/memory
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("emoji_prefs", android.content.Context.MODE_PRIVATE) }
    val recentEmojis = remember {
        mutableStateListOf<String>().apply {
            val list = sharedPrefs.getString("recent_emojis", "😁,👍,❤️,🔥,😊,🎉,😂,✨") ?: ""
            addAll(list.split(",").filter { it.isNotEmpty() })
        }
    }

    fun addEmojiToRecent(emoji: String) {
        if (recentEmojis.contains(emoji)) {
            recentEmojis.remove(emoji)
        }
        recentEmojis.add(0, emoji)
        if (recentEmojis.size > 20) {
            recentEmojis.removeLast()
        }
        sharedPrefs.edit().putString("recent_emojis", recentEmojis.joinToString(",")).apply()
    }

    // Filter emojis
    val allEmojis = remember { EmojiData.categories.flatMap { it.list } }
    val filteredEmojis = remember(searchQuery) {
        if (searchQuery.isEmpty()) {
            emptyList()
        } else {
            allEmojis.filter { emoji ->
                emoji.contains(searchQuery) || searchQuery.length <= 1
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(270.dp)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Search & Skin Tone Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Tiny Glass Edit Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .weight(0.6f)
                    .height(44.dp),
                placeholder = { Text("Search Emojis...", fontSize = 11.sp, color = textColor.copy(alpha = 0.5f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = textColor, modifier = Modifier.size(14.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = textColor, modifier = Modifier.size(14.dp))
                        }
                    }
                },
                textStyle = LocalTextStyle.current.copy(fontSize = 11.sp, color = textColor),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = textColor.copy(alpha = 0.2f),
                    focusedContainerColor = keyColor,
                    unfocusedContainerColor = keyColor.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Skin Tone Selector Shelf
            Row(
                modifier = Modifier
                    .weight(0.4f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(keyColor.copy(alpha = 0.5f))
                    .padding(2.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                EmojiData.skinTones.forEach { (modifier, label) ->
                    val isSelected = selectedSkinToneModifier == modifier
                    val displayEmoji = "✋" + modifier
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) accentColor else Color.Transparent)
                            .clickable { selectedSkinToneModifier = modifier },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayEmoji,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Category Shelf Top Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Recent Tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedCategoryIndex == -1 && searchQuery.isEmpty()) accentColor else keyColor.copy(alpha = 0.3f))
                    .clickable {
                        selectedCategoryIndex = -1
                        searchQuery = ""
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("🕒", fontSize = 13.sp)
            }

            EmojiData.categories.forEachIndexed { idx, cat ->
                val isSelected = selectedCategoryIndex == idx && searchQuery.isEmpty()
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(2.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) accentColor else keyColor.copy(alpha = 0.3f))
                        .clickable {
                            selectedCategoryIndex = idx
                            searchQuery = ""
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(cat.icon, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Grid Display
        val activeList = remember(selectedCategoryIndex, searchQuery, reactiveListUpdateCounter(recentEmojis)) {
            when {
                searchQuery.isNotEmpty() -> filteredEmojis
                selectedCategoryIndex == -1 -> recentEmojis.toList()
                else -> EmojiData.categories[selectedCategoryIndex].list
            }
        }

        if (activeList.isEmpty() && searchQuery.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No emojis match '$searchQuery'",
                    color = textColor.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 36.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(activeList) { baseEmoji ->
                    // Skin tone modification logic
                    val modifiedEmoji = remember(baseEmoji, selectedSkinToneModifier) {
                        try {
                            if (selectedSkinToneModifier.isNotEmpty() && isSkinToneSupported(baseEmoji)) {
                                baseEmoji + selectedSkinToneModifier
                            } else {
                                baseEmoji
                            }
                        } catch (e: Exception) {
                            baseEmoji
                        }
                    }

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(keyColor.copy(alpha = 0.3f))
                            .clickable {
                                addEmojiToRecent(baseEmoji)
                                onEmojiSelected(modifiedEmoji)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = modifiedEmoji,
                            fontSize = 22.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Bottom Accessory Row (Essential for easy text typing and quick toggle back to alphabets!)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // ABC Switch
            Box(
                modifier = Modifier
                    .weight(1.5f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(keyColor.copy(alpha = 0.65f))
                    .clickable { onSwitchToAlphabet() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "ABC",
                    color = textColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Space
            Box(
                modifier = Modifier
                    .weight(3.5f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(keyColor.copy(alpha = 0.45f))
                    .clickable { onEmojiSelected(" ") },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Space",
                    color = textColor.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Delete key
            Box(
                modifier = Modifier
                    .weight(1.5f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(keyColor.copy(alpha = 0.65f))
                    .clickable { onBackspace() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⌫",
                    color = textColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Globe IME input switcher key (only if onSwitchKeyboard is set)
            if (onSwitchKeyboard != null) {
                Box(
                    modifier = Modifier
                        .weight(1.0f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(keyColor.copy(alpha = 0.65f))
                        .clickable { onSwitchKeyboard() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Switch Keyboard",
                        tint = textColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// Helpers
@Composable
fun reactiveListUpdateCounter(list: List<Any>): Int {
    var count by remember { mutableStateOf(0) }
    LaunchedEffect(list.size) { count++ }
    return count
}

fun isSkinToneSupported(emoji: String): Boolean {
    val list = listOf("👋", "🤚", "🖐️", "✋", "🖖", "👌", "🤌", "🤏", "✌️", "🤞", "🤟", "🤘", "🤙", "👈", "👉", "👆", "🖕", "👇", "☝️", "👍", "👎", "✊", "👊", "🤛", "🤜", "👏", "🙌", "👐", "🤲", "🤝", "🙏", "✍️", "💅", "🤳", "💪")
    return list.contains(emoji) || emoji.any { it.isLetter() || it.code in 0x1F440..0x1F490 }
}
