package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.keyboard.KeyboardView
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

class MainActivity : ComponentActivity() {

    private lateinit var database: KeyboardDatabase
    private lateinit var preferences: KeyboardPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = KeyboardDatabase.getDatabase(this)
        preferences = KeyboardPreferences(this)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color(0xFF0F172A) // Sleek Slate Charcoal Background
                ) { innerPadding ->
                    GlassBoardSettingsApp(
                        preferences = preferences,
                        database = database,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassBoardSettingsApp(
    preferences: KeyboardPreferences,
    database: KeyboardDatabase,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // 1. Collect Key Prefs
    val activeThemeId by preferences.activeThemeId.collectAsState()
    val layoutName by preferences.keyboardLayout.collectAsState()
    val keyHeightDp by preferences.keyHeight.collectAsState()
    val cornerRadiusDp by preferences.cornerRadius.collectAsState()
    val blurIntensity by preferences.blurIntensity.collectAsState()
    val glassOpacity by preferences.glassOpacity.collectAsState()
    val reflectionIntensity by preferences.reflectionIntensity.collectAsState()
    val shadowDepth by preferences.shadowDepth.collectAsState()
    val fontFamilyStr by preferences.fontFamily.collectAsState()
    val isHaptic by preferences.isHapticEnabled.collectAsState()
    val hapticStrength by preferences.hapticStrength.collectAsState()
    val isSound by preferences.isSoundEnabled.collectAsState()
    val isAutoCap by preferences.isAutoCapEnabled.collectAsState()
    val isAutoSpace by preferences.isAutoSpacingEnabled.collectAsState()
    val isHighContrast by preferences.isHighContrastMode.collectAsState()

    // 2. Room Clipboard History
    var clipsList by remember { mutableStateOf<List<ClipboardItem>>(emptyList()) }
    LaunchedEffect(Unit) {
        database.clipboardDao().getAllClips().collectLatest { clips ->
            clipsList = clips
        }
    }

    // Determine current theme
    var activeTheme by remember { mutableStateOf(ThemeEngine.presetThemes[2]) }
    LaunchedEffect(activeThemeId) {
        val custom = try {
            database.customThemeDao().getThemeById(activeThemeId)
        } catch (e: Exception) {
            null
        }
        activeTheme = ThemeEngine.getThemeWithCustom(activeThemeId, custom)
    }

    // Interactive text area inside the live preview
    var previewInputText by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Themes", "Designer", "Clipboard", "Backup")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B)
                    )
                )
            )
    ) {
        // Top Premium Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "GlassBoard",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Apple Liquid Glass Aesthetic",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Activation Status Badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.08f),
                modifier = Modifier.clickable {
                    Toast.makeText(context, "Follow System Setup instructions at the bottom to register", Toast.LENGTH_LONG).show()
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4ADE80))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "System Setup",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Live Interactive Preview Card (Stunning visual design anchoring settings)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
            shape = RoundedCornerShape(28.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = "LIVE KEYBOARD PREVIEW",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 4.dp)
                )

                // Interactive Text Input Area
                OutlinedTextField(
                    value = previewInputText,
                    onValueChange = { previewInputText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    placeholder = {
                        Text(
                            text = "Tap mock keys to type here...",
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 13.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Black.copy(alpha = 0.2f),
                        unfocusedContainerColor = Color.Black.copy(alpha = 0.1f),
                        focusedBorderColor = activeTheme.accentColor,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.08f)
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Anchor the Live Keyboard View!
                KeyboardView(
                    theme = activeTheme,
                    layoutName = layoutName,
                    keyHeightDp = keyHeightDp,
                    cornerRadiusDp = cornerRadiusDp,
                    blurIntensity = blurIntensity,
                    glassOpacity = glassOpacity,
                    reflectionIntensity = reflectionIntensity,
                    shadowDepth = shadowDepth,
                    fontFamilyStr = fontFamilyStr,
                    isHapticEnabled = isHaptic,
                    hapticStrength = hapticStrength,
                    isSoundEnabled = isSound,
                    isAutoCapEnabled = isAutoCap,
                    isAutoSpaceEnabled = isAutoSpace,
                    clipboardItems = clipsList,
                    onKeyPress = { previewInputText += it },
                    onBackspace = {
                        if (previewInputText.isNotEmpty()) {
                            previewInputText = previewInputText.dropLast(1)
                        }
                    },
                    onSpacebarDragged = { delta ->
                        Toast.makeText(context, "Cursor Slider: Offset $delta", Toast.LENGTH_SHORT).show()
                    },
                    onActionPressed = {
                        Toast.makeText(context, "Return Executed", Toast.LENGTH_SHORT).show()
                        previewInputText = ""
                    },
                    onClipSelected = { previewInputText += it },
                    onClipDeleted = { clip ->
                        coroutineScope.launch(Dispatchers.IO) {
                            database.clipboardDao().deleteClip(clip)
                        }
                    },
                    onClipPinned = { clip ->
                        coroutineScope.launch(Dispatchers.IO) {
                            database.clipboardDao().updateClip(clip.copy(isPinned = !clip.isPinned))
                        }
                    },
                    onOpenSettings = {
                        Toast.makeText(context, "Settings already open!", Toast.LENGTH_SHORT).show()
                    },
                    onSwitchKeyboard = {
                        val imm = context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as? android.view.inputmethod.InputMethodManager
                        imm?.showInputMethodPicker()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tab selection for options
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = Color.White,
            indicator = { tabPositions ->
                if (selectedTabIndex < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = activeTheme.accentColor
                    )
                }
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
            }
        }

        // Expanded Scrollable Panel Contents
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (selectedTabIndex) {
                0 -> ThemesTab(activeThemeId, preferences)
                1 -> DesignerTab(
                    preferences,
                    database,
                    activeTheme,
                    keyHeightDp,
                    cornerRadiusDp,
                    blurIntensity,
                    glassOpacity,
                    reflectionIntensity,
                    shadowDepth,
                    layoutName,
                    fontFamilyStr,
                    isHaptic,
                    hapticStrength,
                    isSound,
                    isAutoCap,
                    isAutoSpace,
                    isHighContrast
                )
                2 -> ClipboardTab(clipsList, database)
                3 -> BackupTab(preferences)
            }
        }
    }
}

@Composable
fun ThemesTab(
    activeThemeId: String,
    preferences: KeyboardPreferences
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "PRESET GLASS THEMES",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        items(ThemeEngine.presetThemes.chunked(2)) { list ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                list.forEach { theme ->
                    val isSelected = activeThemeId == theme.id
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(94.dp)
                            .clickable {
                                preferences.updateActiveThemeId(theme.id)
                            },
                        colors = CardDefaults.cardColors(containerColor = theme.backgroundStart.copy(alpha = 0.95f)),
                        shape = RoundedCornerShape(20.dp),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color.White) else null
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.verticalGradient(listOf(theme.backgroundStart, theme.backgroundEnd)))
                                .padding(12.dp)
                        ) {
                            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                                Text(
                                    text = theme.displayName,
                                    color = theme.textColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (theme.isDark) "Dark mode" else "Light mode",
                                    color = theme.textColor.copy(alpha = 0.6f),
                                    fontSize = 10.sp
                                )
                            }

                            // Dynamic Glass preview bar
                            Box(
                                modifier = Modifier
                                    .size(width = 44.dp, height = 24.dp)
                                    .align(Alignment.TopEnd)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(theme.glassColor.copy(alpha = theme.glassOpacity))
                                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DesignerTab(
    preferences: KeyboardPreferences,
    database: KeyboardDatabase,
    activeTheme: GlassTheme,
    keyHeightDp: Int,
    cornerRadiusDp: Int,
    blurIntensity: Float,
    glassOpacity: Float,
    reflectionIntensity: Float,
    shadowDepth: Float,
    layoutName: String,
    fontFamilyStr: String,
    isHaptic: Boolean,
    hapticStrength: Int,
    isSound: Boolean,
    isAutoCap: Boolean,
    isAutoSpace: Boolean,
    isHighContrast: Boolean
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Layout Properties
        item {
            Text(
                "DOCKING & KEYBOARD SIZING",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.03f))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Key height slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Key Height", color = Color.White, fontSize = 13.sp)
                        Text("${keyHeightDp}dp", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                    }
                    Slider(
                        value = keyHeightDp.toFloat(),
                        onValueChange = { preferences.updateKeyHeight(it.toInt()) },
                        valueRange = 40f..75f,
                        colors = SliderDefaults.colors(thumbColor = activeTheme.accentColor, activeTrackColor = activeTheme.accentColor)
                    )
                }

                // Keyboard corners slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Corner Radius", color = Color.White, fontSize = 13.sp)
                        Text("${cornerRadiusDp}dp", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                    }
                    Slider(
                        value = cornerRadiusDp.toFloat(),
                        onValueChange = { preferences.updateCornerRadius(it.toInt()) },
                        valueRange = 4f..30f,
                        colors = SliderDefaults.colors(thumbColor = activeTheme.accentColor, activeTrackColor = activeTheme.accentColor)
                    )
                }
            }
        }

        // Accent Glass Settings
        item {
            Text(
                "LIQUID GLASS INTENSITIES",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.03f))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Glass Opacity
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Glass Opacity", color = Color.White, fontSize = 13.sp)
                        Text("${(glassOpacity * 100).toInt()}%", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                    }
                    Slider(
                        value = glassOpacity,
                        onValueChange = { preferences.updateGlassOpacity(it) },
                        valueRange = 0.1f..0.8f,
                        colors = SliderDefaults.colors(thumbColor = activeTheme.accentColor, activeTrackColor = activeTheme.accentColor)
                    )
                }

                // Blur Intensity
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Real-time Blur Radius", color = Color.White, fontSize = 13.sp)
                        Text("${blurIntensity.toInt()}px", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                    }
                    Slider(
                        value = blurIntensity,
                        onValueChange = { preferences.updateBlurIntensity(it) },
                        valueRange = 0f..40f,
                        colors = SliderDefaults.colors(thumbColor = activeTheme.accentColor, activeTrackColor = activeTheme.accentColor)
                    )
                }

                // Reflection Shimmer Strength
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Reflection Intensity", color = Color.White, fontSize = 13.sp)
                        Text("${(reflectionIntensity * 100).toInt()}%", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                    }
                    Slider(
                        value = reflectionIntensity,
                        onValueChange = { preferences.updateReflectionIntensity(it) },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(thumbColor = activeTheme.accentColor, activeTrackColor = activeTheme.accentColor)
                    )
                }
            }
        }

        // Typing Layout Editor Mappings
        item {
            Text(
                "KEYBOARD LAYOUT & DICTIONARY",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.03f))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Layout selector row
                Text("Select Keyboard Layout Mappings:", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("QWERTY", "AZERTY", "QWERTZ", "DVORAK").forEach { mapping ->
                        val isSel = mapping.equals(layoutName, true)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) activeTheme.accentColor else Color.White.copy(alpha = 0.06f))
                                .clickable { preferences.updateKeyboardLayout(mapping) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(mapping, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Vowel selections or font style selections
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Selected Font Family", color = Color.White, fontSize = 13.sp)
                    Box(modifier = Modifier.clickable {
                        val next = when (fontFamilyStr) {
                            "System" -> "Serif"
                            "Serif" -> "Monospace"
                            "Monospace" -> "Sans-Serif"
                            else -> "System"
                        }
                        preferences.updateFontFamily(next)
                    }) {
                        Surface(shape = RoundedCornerShape(8.dp), color = Color.White.copy(alpha = 0.08f)) {
                            Text(fontFamilyStr, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                }
            }
        }

        // Feedback & Corrections settings
        item {
            Text(
                "CORRECTIONS & FEEDBACK SYSTEM",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.03f))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Haptic switcher
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Haptic Vibration Feedback", color = Color.White, fontSize = 13.sp)
                    Switch(
                        checked = isHaptic,
                        onCheckedChange = { preferences.updateHapticEnabled(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = activeTheme.accentColor)
                    )
                }

                if (isHaptic) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Vibration Strength", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                            Text("$hapticStrength%", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                        }
                        Slider(
                            value = hapticStrength.toFloat(),
                            onValueChange = { preferences.updateHapticStrength(it.toInt()) },
                            valueRange = 10f..100f,
                            colors = SliderDefaults.colors(thumbColor = activeTheme.accentColor, activeTrackColor = activeTheme.accentColor)
                        )
                    }
                }

                // Sound feedback switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Keypress Audio Feedback", color = Color.White, fontSize = 13.sp)
                    Switch(
                        checked = isSound,
                        onCheckedChange = { preferences.updateSoundEnabled(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = activeTheme.accentColor)
                    )
                }

                // Sentence automatic cap
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Auto Capitalization", color = Color.White, fontSize = 13.sp)
                    Switch(
                        checked = isAutoCap,
                        onCheckedChange = { preferences.updateAutoCapEnabled(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = activeTheme.accentColor)
                    )
                }

                // High Contrast
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("High Contrast Accents", color = Color.White, fontSize = 13.sp)
                    Switch(
                        checked = isHighContrast,
                        onCheckedChange = { preferences.updateHighContrastMode(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = activeTheme.accentColor)
                    )
                }
            }
        }

        // Custom designer creator
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(activeTheme.accentColor)
                    .clickable {
                        // Quick customized theme save handler
                        coroutineScope.launch(Dispatchers.IO) {
                            val customRandomName = "custom_design_" + (100..999).random()
                            val custom = CustomTheme(
                                id = customRandomName,
                                displayName = "My Glass Design",
                                isDark = activeTheme.isDark,
                                backgroundColor = "#0F172A",
                                glassColor = "#FFFFFF",
                                keyColor = "#FFFFFF",
                                textColor = "#000000",
                                accentColor = "#00BCD4",
                                glassOpacity = glassOpacity,
                                blurIntensity = blurIntensity,
                                reflectionIntensity = reflectionIntensity,
                                shadowDepth = shadowDepth,
                                cornerRadius = cornerRadiusDp
                            )
                            database.customThemeDao().insertTheme(custom)
                            preferences.updateActiveThemeId(customRandomName)
                        }
                        Toast.makeText(context, "Saved custom design successfully!", Toast.LENGTH_SHORT).show()
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Save, contentDescription = "Save Custom Theme", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SAVE CUSTOM DESIGN THEME", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ClipboardTab(
    clips: List<ClipboardItem>,
    database: KeyboardDatabase
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var clipboardSearchQuery by remember { mutableStateOf("") }
    var selectedCategoryTab by remember { mutableStateOf("All") }

    val filteredClips = remember(clips, clipboardSearchQuery, selectedCategoryTab) {
        clips.filter { clip ->
            val matchQuery = clipboardSearchQuery.isEmpty() || clip.content.contains(clipboardSearchQuery, true)
            val matchCategory = selectedCategoryTab == "All" || clip.category.equals(selectedCategoryTab, true)
            matchQuery && matchCategory
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search & Option Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = clipboardSearchQuery,
                onValueChange = { clipboardSearchQuery = it },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                placeholder = { Text("Search Clipboard...", fontSize = 11.sp, color = Color.White.copy(alpha = 0.4f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(14.dp)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White.copy(alpha = 0.3f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.08f)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Delete ALL unpinned clips button
            Box(
                modifier = Modifier
                    .size(width = 96.dp, height = 36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Red.copy(alpha = 0.15f))
                    .clickable {
                        coroutineScope.launch(Dispatchers.IO) {
                            database.clipboardDao().clearUnpinnedClips()
                        }
                        Toast.makeText(context, "Unpinned clips cleared", Toast.LENGTH_SHORT).show()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("Clear All", color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Categories selector tabs row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("All", "Text", "URL", "Email", "Phone").forEach { cat ->
                val isSel = cat == selectedCategoryTab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) Color.White.copy(alpha = 0.12f) else Color.Transparent)
                        .border(1.dp, if (isSel) Color.White.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                        .clickable { selectedCategoryTab = cat },
                    contentAlignment = Alignment.Center
                ) {
                    Text(cat, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredClips.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No copied items found in this category",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredClips) { clip ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.02f)),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color.White.copy(alpha = 0.08f)
                                    ) {
                                        Text(
                                            text = clip.category.uppercase(),
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                    if (clip.isPinned) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(Icons.Filled.PushPin, contentDescription = "Pinned", tint = Color.White, modifier = Modifier.size(10.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = clip.content,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            }

                            Row {
                                // Pinned toggle key
                                IconButton(onClick = {
                                    coroutineScope.launch(Dispatchers.IO) {
                                        database.clipboardDao().updateClip(clip.copy(isPinned = !clip.isPinned))
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.PushPin,
                                        contentDescription = "Pin Clip",
                                        tint = if (clip.isPinned) Color.Yellow else Color.White.copy(alpha = 0.4f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // Delete key
                                IconButton(onClick = {
                                    coroutineScope.launch(Dispatchers.IO) {
                                        database.clipboardDao().deleteClip(clip)
                                    }
                                    Toast.makeText(context, "Deleted clip", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Clip", tint = Color.Red.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BackupTab(preferences: KeyboardPreferences) {
    val context = LocalContext.current
    var inputBackupJson by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "BACKUP, RESTORE & SYSTEM CODES",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.03f))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "You can backup or export your GlassBoard settings, colors, layout scales, and feedback configurations to an external readable JSON string, and paste it back into other devices to restore it.",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )

                // Export Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .clickable {
                            val json = preferences.exportSettingsToJson()
                            val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cb.setPrimaryClip(ClipData.newPlainText("GlassBoard Settings", json))
                            Toast.makeText(context, "Settings JSON copied to clipboard!", Toast.LENGTH_LONG).show()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Share, contentDescription = "Export JSON", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("EXPORT GLASSBOARD SETTINGS JSON", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Input box to import
                OutlinedTextField(
                    value = inputBackupJson,
                    onValueChange = { inputBackupJson = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    placeholder = { Text("Paste GlassBoard settings JSON here to import...", fontSize = 11.sp, color = Color.White.copy(alpha = 0.4f)) },
                    textStyle = LocalTextStyle.current.copy(fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.08f)
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                // Import Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF10B981))
                        .clickable {
                            if (inputBackupJson.isEmpty()) {
                                Toast.makeText(context, "Please paste settings JSON first!", Toast.LENGTH_SHORT).show()
                            } else {
                                val success = preferences.importSettingsFromJson(inputBackupJson)
                                if (success) {
                                    Toast.makeText(context, "Settings imported and applied successfully!", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Invalid JSON settings code, please try again", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Download, contentDescription = "Import JSON", tint = Color.White, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("IMPORT & RESTORE SETTINGS", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Setup Instructions
        item {
            Text(
                "HOW TO ENABLE GLASSBOARD",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.03f))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "To activate this keyboard on your Android system:",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "1. Open Settings on your device.\n" +
                    "2. Navigate to System > Languages & Input > On-screen Keyboard.\n" +
                    "3. Select 'Manage Keyboards' and switch on 'GlassBoard'.\n" +
                    "4. Tap on any text field elsewhere, drag down notification screen or click keyboard selector icon, and switch primary layout to 'GlassBoard'.",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
