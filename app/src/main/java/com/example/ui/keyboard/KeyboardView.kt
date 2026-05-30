package com.example.ui.keyboard

import android.view.KeyEvent
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable
import android.media.AudioManager
import android.view.SoundEffectConstants
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.data.ClipboardItem
import com.example.data.GlassTheme
import com.example.data.PredictionEngine
import com.example.data.ThemeEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class KeyboardMode {
    ALPHABET, NUMERIC, SYMBOLS, EMOJI
}

@Composable
fun KeyboardView(
    theme: GlassTheme,
    layoutName: String = "QWERTY",
    keyHeightDp: Int = 54,
    cornerRadiusDp: Int = 12,
    blurIntensity: Float = 15f,
    glassOpacity: Float = 0.35f,
    reflectionIntensity: Float = 0.6f,
    shadowDepth: Float = 4f,
    fontFamilyStr: String = "System",
    isHapticEnabled: Boolean = true,
    hapticStrength: Int = 50,
    isSoundEnabled: Boolean = false,
    isAutoCapEnabled: Boolean = true,
    isAutoSpaceEnabled: Boolean = true,
    clipboardItems: List<ClipboardItem> = emptyList(),
    onKeyPress: (String) -> Unit,
    onBackspace: () -> Unit,
    onSpacebarDragged: (Int) -> Unit,
    onActionPressed: () -> Unit,
    onClipSelected: (String) -> Unit,
    onClipDeleted: (ClipboardItem) -> Unit,
    onClipPinned: (ClipboardItem) -> Unit,
    onOpenSettings: () -> Unit = {},
    onSwitchKeyboard: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var mode by remember { mutableStateOf(KeyboardMode.ALPHABET) }
    var isShiftActive by remember { mutableStateOf(false) }
    var isShiftDoubleClicked by remember { mutableStateOf(false) }

    // Floating UI Accent selection
    var showAccentPopupForChar by remember { mutableStateOf<String?>(null) }
    var accentPopupX by remember { mutableStateOf(0f) }
    var accentPopupY by remember { mutableStateOf(0f) }

    // Dynamic smart suggestions based on text history
    var lastTypedWord by remember { mutableStateOf("") }
    val smartSuggestions = remember(lastTypedWord) {
        PredictionEngine.getPredictions(lastTypedWord)
    }

    val coroutineScope = rememberCoroutineScope()
    val font = when (fontFamilyStr) {
        "Serif" -> FontFamily.Serif
        "Monospace" -> FontFamily.Monospace
        "Sans-Serif" -> FontFamily.SansSerif
        else -> FontFamily.Default
    }

    // Dynamic scale helper
    val sizeScale = 1.0f

    // Action execution helpers
    fun triggerHeuristics(char: String) {
        // Sound and Haptics
        if (isHapticEnabled) {
            val vibrator = context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? android.os.Vibrator
            if (vibrator != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    val strength = (hapticStrength * 2.55f).toInt().coerceIn(1, 255)
                    vibrator.vibrate(android.os.VibrationEffect.createOneShot(15, strength))
                } else {
                    vibrator.vibrate(15)
                }
            }
        }
        if (isSoundEnabled) {
            val am = context.getSystemService(android.content.Context.AUDIO_SERVICE) as? android.media.AudioManager
            am?.playSoundEffect(android.view.SoundEffectConstants.CLICK)
        }

        // Logic
        onKeyPress(char)

        // Track last typed word for suggestions
        if (char == " " || char.any { it.isWhitespace() }) {
            lastTypedWord = ""
        } else {
            lastTypedWord += char
        }

        // Shift auto-disable
        if (!isShiftDoubleClicked && isShiftActive) {
            isShiftActive = false
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Frosted Glassmorphic background layer (Isolating blur to prevent blurring active characters & text keys)
        Box(
            modifier = Modifier
                .matchParentSize()
                .glassmorphicContainer(
                    cornerRadius = cornerRadiusDp.dp,
                    glassColor = theme.glassColor,
                    glassOpacity = glassOpacity,
                    reflectionIntensity = reflectionIntensity,
                    shadowDepth = shadowDepth,
                    blurIntensity = blurIntensity
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 2.dp)
        ) {
            
            // 1. Suggestion & Clipboard Shelf (Lush Dual-row Pinned Hub)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Clipboard Manager expansion toggle icon
                IconButton(
                    onClick = {
                        // Quick click sets clipboard shelf
                        mode = if (mode == KeyboardMode.ALPHABET) KeyboardMode.ALPHABET else KeyboardMode.ALPHABET
                    },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = "Clipboard Hub",
                        tint = theme.accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Suggestion Stream Horizontal Scrolling Row
                LazyRow(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Check if clipboard has items and show latest clip preview as first card
                    if (clipboardItems.isNotEmpty()) {
                        val topClip = clipboardItems.first()
                        item {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(theme.keyColor.copy(alpha = 0.4f))
                                    .clickable {
                                        onClipSelected(topClip.content)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ContentPaste, contentDescription = "Paste", tint = theme.accentColor, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (topClip.content.length > 20) topClip.content.take(18) + "..." else topClip.content,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = theme.textColor,
                                        fontFamily = font
                                    )
                                }
                            }
                        }
                    }

                    // Standard words generated dynamically
                    items(smartSuggestions) { suggestion ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(theme.keyColor.copy(alpha = 0.2f))
                                .clickable {
                                    triggerHeuristics("$suggestion ")
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = suggestion,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = theme.textColor,
                                fontFamily = font
                            )
                        }
                    }
                }

                // Gear shortcut to Settings Panel
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Open Settings",
                        tint = theme.textColor.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Clipboard Bar items drawer sheet integrated natively
            if (clipboardItems.isNotEmpty() && !lastTypedWord.contains(Regex("[a-zA-Z]"))) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(clipboardItems.take(5)) { clip ->
                        Box(
                            modifier = Modifier
                                .height(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(theme.keyColor.copy(alpha = 0.35f))
                                .pointerInput(clip) {
                                    detectTapGestures(
                                        onLongPress = {
                                            onClipPinned(clip)
                                        },
                                        onTap = {
                                            onClipSelected(clip.content)
                                        }
                                    )
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (clip.isPinned) {
                                    Icon(Icons.Filled.PushPin, contentDescription = "Pinned", tint = theme.accentColor, modifier = Modifier.size(10.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = if (clip.content.length > 25) clip.content.take(23) + "..." else clip.content,
                                    fontSize = 11.sp,
                                    color = theme.textColor,
                                    fontFamily = font
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(modifier = Modifier.clickable { onClipDeleted(clip) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(10.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Separator Line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(theme.glassBorderColor.copy(alpha = 0.15f))
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 2. Main Keyboard Plate (Alphabet, Symbols, Emoji, Numeric)
            AnimatedContent(
                targetState = mode,
                transitionSpec = {
                    fadeIn(animationSpec = spring(stiffness = 300f)) togetherWith
                            fadeOut(animationSpec = spring(stiffness = 300f))
                }
            ) { activeMode ->
                when (activeMode) {
                    KeyboardMode.EMOJI -> {
                        EmojiKeyboard(
                            textColor = theme.textColor,
                            accentColor = theme.accentColor,
                            keyColor = theme.keyColor,
                            onEmojiSelected = { triggerHeuristics(it) },
                            onSwitchToAlphabet = { mode = KeyboardMode.ALPHABET },
                            onBackspace = onBackspace,
                            onSwitchKeyboard = onSwitchKeyboard
                        )
                    }
                    KeyboardMode.NUMERIC -> {
                        NumericSymbolsPlate(
                            isShiftActive = isShiftActive,
                            theme = theme,
                            keyHeight = keyHeightDp.dp,
                            cornerRadius = cornerRadiusDp.dp,
                            font = font,
                            onKeyClick = { triggerHeuristics(it) },
                            onBackspace = onBackspace,
                            onToggleMode = { mode = it },
                            onSwitchKeyboard = onSwitchKeyboard
                        )
                    }
                    KeyboardMode.SYMBOLS -> {
                        NumericSymbolsPlate(
                            isShiftActive = true, // Force secondary page symbols
                            theme = theme,
                            keyHeight = keyHeightDp.dp,
                            cornerRadius = cornerRadiusDp.dp,
                            font = font,
                            onKeyClick = { triggerHeuristics(it) },
                            onBackspace = onBackspace,
                            onToggleMode = { mode = it },
                            onSwitchKeyboard = onSwitchKeyboard
                        )
                    }
                    KeyboardMode.ALPHABET -> {
                        AlphabetLayoutPlate(
                            layoutName = layoutName,
                            isShiftActive = isShiftActive,
                            theme = theme,
                            keyHeight = keyHeightDp.dp,
                            cornerRadius = cornerRadiusDp.dp,
                            font = font,
                            onKeyClick = { triggerHeuristics(it) },
                            onBackspace = {
                                if (isHapticEnabled) {
                                    val vibrator = context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? android.os.Vibrator
                                    vibrator?.vibrate(10)
                                }
                                onBackspace()
                            },
                            onShiftClick = {
                                if (!isShiftActive) {
                                    isShiftActive = true
                                    isShiftDoubleClicked = false
                                } else if (!isShiftDoubleClicked) {
                                    isShiftDoubleClicked = true
                                } else {
                                    isShiftActive = false
                                    isShiftDoubleClicked = false
                                }
                            },
                            onToggleMode = { mode = it },
                            onSpacebarDragged = onSpacebarDragged,
                            onActionPressed = onActionPressed,
                            onSwitchKeyboard = onSwitchKeyboard
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AlphabetLayoutPlate(
    layoutName: String,
    isShiftActive: Boolean,
    theme: GlassTheme,
    keyHeight: Dp,
    cornerRadius: Dp,
    font: FontFamily,
    onKeyClick: (String) -> Unit,
    onBackspace: () -> Unit,
    onShiftClick: () -> Unit,
    onToggleMode: (KeyboardMode) -> Unit,
    onSpacebarDragged: (Int) -> Unit,
    onActionPressed: () -> Unit,
    onSwitchKeyboard: (() -> Unit)? = null
) {
    // Letters based on configuration layouts
    val rows = remember(layoutName) {
        when (layoutName.uppercase()) {
            "AZERTY" -> listOf(
                listOf("a", "z", "e", "u", "r", "t", "y", "i", "o", "p"),
                listOf("q", "s", "d", "f", "g", "h", "j", "k", "l", "m"),
                listOf("w", "x", "c", "v", "b", "n")
            )
            "QWERTZ" -> listOf(
                listOf("q", "w", "e", "r", "t", "z", "u", "i", "o", "p"),
                listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
                listOf("y", "x", "c", "v", "b", "n", "m")
            )
            "DVORAK" -> listOf(
                listOf("q", "j", "f", "g", "c", "r", "l", "p", "y"),
                listOf("a", "o", "e", "u", "i", "d", "h", "t", "n", "s"),
                listOf("z", "x", "m", "w", "v", "b", "k")
            )
            else -> listOf( // QWERTY Defaults
                listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
                listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
                listOf("z", "x", "c", "v", "b", "n", "m")
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            rows[0].forEach { char ->
                val letterCap = if (isShiftActive) char.uppercase() else char
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(keyHeight)
                        .glassmorphicClickableKey(
                            cornerRadius = cornerRadius,
                            keyColor = theme.keyColor,
                            testTagStr = "key_$char"
                        ) {
                            onKeyClick(letterCap)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = letterCap,
                        color = theme.textColor,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = font
                    )
                }
            }
        }

        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(0.95f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            rows[1].forEach { char ->
                val letterCap = if (isShiftActive) char.uppercase() else char
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(keyHeight)
                        .glassmorphicClickableKey(
                            cornerRadius = cornerRadius,
                            keyColor = theme.keyColor,
                            testTagStr = "key_$char"
                        ) {
                            onKeyClick(letterCap)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = letterCap,
                        color = theme.textColor,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = font
                    )
                }
            }
        }

        // Row 3
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Shift Key
            Box(
                modifier = Modifier
                    .weight(1.3f)
                    .height(keyHeight)
                    .glassmorphicClickableKey(
                        cornerRadius = cornerRadius,
                        keyColor = if (isShiftActive) theme.accentColor else theme.keyColor.copy(alpha = 0.6f),
                        activeColor = theme.accentColor,
                        testTagStr = "key_shift"
                    ) {
                        onShiftClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isShiftActive) Icons.Default.VerticalAlignTop else Icons.Default.ArrowUpward,
                    contentDescription = "Shift",
                    tint = if (isShiftActive) theme.accentTextColor else theme.textColor,
                    modifier = Modifier.size(19.dp)
                )
            }

            rows[2].forEach { char ->
                val letterCap = if (isShiftActive) char.uppercase() else char
                Box(
                    modifier = Modifier
                        .weight(1.0f)
                        .height(keyHeight)
                        .glassmorphicClickableKey(
                            cornerRadius = cornerRadius,
                            keyColor = theme.keyColor,
                            testTagStr = "key_$char"
                        ) {
                            onKeyClick(letterCap)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = letterCap,
                        color = theme.textColor,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = font
                    )
                }
            }

            // Backspace key
            Box(
                modifier = Modifier
                    .weight(1.3f)
                    .height(keyHeight)
                    .glassmorphicClickableKey(
                        cornerRadius = cornerRadius,
                        keyColor = theme.keyColor.copy(alpha = 0.6f),
                        testTagStr = "key_backspace"
                    ) {
                        onBackspace()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Backspace,
                    contentDescription = "Backspace",
                    tint = theme.textColor,
                    modifier = Modifier.size(19.dp)
                )
            }
        }

        // Row 4
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Mode Select (123)
            Box(
                modifier = Modifier
                    .weight(1.3f)
                    .height(keyHeight)
                    .glassmorphicClickableKey(
                        cornerRadius = cornerRadius,
                        keyColor = theme.keyColor.copy(alpha = 0.6f),
                        testTagStr = "key_mode_switch"
                    ) {
                        onToggleMode(KeyboardMode.NUMERIC)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "123",
                    color = theme.textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font
                )
            }

            // Language / Globe Switch Key (Only renders if switcher action is available)
            if (onSwitchKeyboard != null) {
                Box(
                    modifier = Modifier
                        .weight(1.0f)
                        .height(keyHeight)
                        .glassmorphicClickableKey(
                            cornerRadius = cornerRadius,
                            keyColor = theme.keyColor.copy(alpha = 0.6f),
                            testTagStr = "key_kb_switch"
                        ) {
                            onSwitchKeyboard()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Switch Keyboard",
                        tint = theme.textColor,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            // Language / Globe Shortcut (Emoji mode)
            Box(
                modifier = Modifier
                    .weight(1.0f)
                    .height(keyHeight)
                    .glassmorphicClickableKey(
                        cornerRadius = cornerRadius,
                        keyColor = theme.keyColor.copy(alpha = 0.6f),
                        testTagStr = "key_emoji_switch"
                    ) {
                        onToggleMode(KeyboardMode.EMOJI)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "😂",
                    fontSize = 16.sp
                )
            }

            // Spacebar (Support drag cursor control)
            var totalDragOffset by remember { mutableStateOf(0f) }
            val spaceWeight = if (onSwitchKeyboard != null) 3.6f else 4.5f
            Box(
                modifier = Modifier
                    .weight(spaceWeight)
                    .height(keyHeight)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { totalDragOffset = 0f },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                totalDragOffset += dragAmount.x
                                if (Math.abs(totalDragOffset) > 40f) {
                                    val direction = if (totalDragOffset > 0) 1 else -1
                                    onSpacebarDragged(direction)
                                    totalDragOffset = 0f
                                }
                            }
                        )
                    }
                    .glassmorphicClickableKey(
                        cornerRadius = cornerRadius,
                        keyColor = theme.keyColor.copy(alpha = 0.45f),
                        testTagStr = "key_spacebar"
                    ) {
                        onKeyClick(" ")
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "space",
                    color = theme.textColor.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = font
                )
            }

            // Return / Action Key
            Box(
                modifier = Modifier
                    .weight(1.5f)
                    .height(keyHeight)
                    .glassmorphicClickableKey(
                        cornerRadius = cornerRadius,
                        keyColor = theme.accentColor,
                        activeColor = theme.accentColor,
                        testTagStr = "key_return"
                    ) {
                        onActionPressed()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "return",
                    color = theme.accentTextColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font
                )
            }
        }
    }
}

@Composable
fun NumericSymbolsPlate(
    isShiftActive: Boolean,
    theme: GlassTheme,
    keyHeight: Dp,
    cornerRadius: Dp,
    font: FontFamily,
    onKeyClick: (String) -> Unit,
    onBackspace: () -> Unit,
    onToggleMode: (KeyboardMode) -> Unit,
    onSwitchKeyboard: (() -> Unit)? = null
) {
    // Secondary Symbols mapping
    val mapOne = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    val mapTwo = if (!isShiftActive) {
        listOf("-", "/", ":", ";", "(", ")", "$", "&", "@", "\"")
    } else {
        listOf("[", "]", "{", "}", "#", "%", "^", "*", "+", "=")
    }
    val mapThree = if (!isShiftActive) {
        listOf(".", ",", "?", "!", "'")
    } else {
        listOf("_", "\\", "|", "~", "<", ">", "€", "£", "¥")
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            mapOne.forEach { char ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(keyHeight)
                        .glassmorphicClickableKey(cornerRadius = cornerRadius, keyColor = theme.keyColor) {
                            onKeyClick(char)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = char, color = theme.textColor, fontSize = 19.sp, fontWeight = FontWeight.Bold, fontFamily = font)
                }
            }
        }

        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            mapTwo.forEach { char ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(keyHeight)
                        .glassmorphicClickableKey(cornerRadius = cornerRadius, keyColor = theme.keyColor) {
                            onKeyClick(char)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = char, color = theme.textColor, fontSize = 19.sp, fontWeight = FontWeight.Bold, fontFamily = font)
                }
            }
        }

        // Row 3
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // ALT Sym selector toggle
            Box(
                modifier = Modifier
                    .weight(1.3f)
                    .height(keyHeight)
                    .glassmorphicClickableKey(cornerRadius = cornerRadius, keyColor = theme.keyColor.copy(alpha = 0.6f)) {
                        onToggleMode(if (isShiftActive) KeyboardMode.NUMERIC else KeyboardMode.SYMBOLS)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (!isShiftActive) "#+=" else "123",
                    color = theme.textColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font
                )
            }

            mapThree.forEach { char ->
                Box(
                    modifier = Modifier
                        .weight(1.0f)
                        .height(keyHeight)
                        .glassmorphicClickableKey(cornerRadius = cornerRadius, keyColor = theme.keyColor) {
                            onKeyClick(char)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = char, color = theme.textColor, fontSize = 19.sp, fontWeight = FontWeight.Bold, fontFamily = font)
                }
            }

            // Backspace key
            Box(
                modifier = Modifier
                    .weight(1.3f)
                    .height(keyHeight)
                    .glassmorphicClickableKey(cornerRadius = cornerRadius, keyColor = theme.keyColor.copy(alpha = 0.6f)) {
                        onBackspace()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Backspace, contentDescription = "Backspace", tint = theme.textColor, modifier = Modifier.size(19.dp))
            }
        }

        // Row 4
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Return to ABC
            Box(
                modifier = Modifier
                    .weight(1.5f)
                    .height(keyHeight)
                    .glassmorphicClickableKey(cornerRadius = cornerRadius, keyColor = theme.keyColor.copy(alpha = 0.6f)) {
                        onToggleMode(KeyboardMode.ALPHABET)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("ABC", color = theme.textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = font)
            }

            // Language / Globe Switch Key (Only renders if switcher action is available)
            if (onSwitchKeyboard != null) {
                Box(
                    modifier = Modifier
                        .weight(1.0f)
                        .height(keyHeight)
                        .glassmorphicClickableKey(
                            cornerRadius = cornerRadius,
                            keyColor = theme.keyColor.copy(alpha = 0.6f),
                            testTagStr = "key_kb_switch"
                        ) {
                            onSwitchKeyboard()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Switch Keyboard",
                        tint = theme.textColor,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            // Space
            val spaceWeight = if (onSwitchKeyboard != null) 3.5f else 4.5f
            Box(
                modifier = Modifier
                    .weight(spaceWeight)
                    .height(keyHeight)
                    .glassmorphicClickableKey(cornerRadius = cornerRadius, keyColor = theme.keyColor.copy(alpha = 0.45f)) {
                        onKeyClick(" ")
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("space", color = theme.textColor.copy(alpha = 0.7f), fontSize = 14.sp, fontFamily = font)
            }

            // Done / Return
            Box(
                modifier = Modifier
                    .weight(1.5f)
                    .height(keyHeight)
                    .glassmorphicClickableKey(cornerRadius = cornerRadius, keyColor = theme.accentColor) {
                        onToggleMode(KeyboardMode.ALPHABET)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("done", color = theme.accentTextColor, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = font)
            }
        }
    }
}
