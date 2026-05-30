package com.example.service

import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedTextRequest
import android.inputmethodservice.InputMethodService
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.example.MainActivity
import com.example.data.*
import com.example.ui.keyboard.KeyboardView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GlassKeyboardService : InputMethodService() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var database: KeyboardDatabase
    private lateinit var preferences: KeyboardPreferences
    private var clipboardListener: ClipboardManager.OnPrimaryClipChangedListener? = null

    // Stateful indicators for keyboard composition binding
    private val scopeListState = mutableStateListOf<ClipboardItem>()
    private var activeThemeState by mutableStateOf(ThemeEngine.presetThemes[2]) // Default Crystal Blue
    private var layoutState by mutableStateOf("QWERTY")
    private var keyHeightState by mutableStateOf(54)
    private var cornerRadiusState by mutableStateOf(12)
    private var blurIntensityState by mutableStateOf(15f)
    private var glassOpacityState by mutableStateOf(0.35f)
    private var reflectionIntensityState by mutableStateOf(0.6f)
    private var shadowDepthState by mutableStateOf(4f)
    private var fontFamilyState by mutableStateOf("System")
    private var isHapticState by mutableStateOf(true)
    private var hapticStrengthState by mutableStateOf(50)
    private var isSoundState by mutableStateOf(false)
    private var isAutoCapState by mutableStateOf(true)
    private var isAutoSpaceState by mutableStateOf(true)

    override fun onCreate() {
        super.onCreate()
        database = KeyboardDatabase.getDatabase(this)
        preferences = KeyboardPreferences(this)

        // Sync settings with compose states
        serviceScope.launch {
            launch {
                preferences.activeThemeId.collectLatest { themeId ->
                    val custom = try {
                        database.customThemeDao().getThemeById(themeId)
                    } catch (e: Exception) {
                        null
                    }
                    activeThemeState = ThemeEngine.getThemeWithCustom(themeId, custom)
                }
            }
            launch { preferences.keyboardLayout.collectLatest { layoutState = it } }
            launch { preferences.keyHeight.collectLatest { keyHeightState = it } }
            launch { preferences.cornerRadius.collectLatest { cornerRadiusState = it } }
            launch { preferences.blurIntensity.collectLatest { blurIntensityState = it } }
            launch { preferences.glassOpacity.collectLatest { glassOpacityState = it } }
            launch { preferences.reflectionIntensity.collectLatest { reflectionIntensityState = it } }
            launch { preferences.shadowDepth.collectLatest { shadowDepthState = it } }
            launch { preferences.fontFamily.collectLatest { fontFamilyState = it } }
            launch { preferences.isHapticEnabled.collectLatest { isHapticState = it } }
            launch { preferences.hapticStrength.collectLatest { hapticStrengthState = it } }
            launch { preferences.isSoundEnabled.collectLatest { isSoundState = it } }
            launch { preferences.isAutoCapEnabled.collectLatest { isAutoCapState = it } }
            launch { preferences.isAutoSpacingEnabled.collectLatest { isAutoSpaceState = it } }

            // Observe Clipboard items
            launch {
                database.clipboardDao().getAllClips().collectLatest { clips ->
                    scopeListState.clear()
                    scopeListState.addAll(clips)
                }
            }
        }

        // Clip listener triggers automatic synchronisation
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        if (clipboard != null) {
            val listener = ClipboardManager.OnPrimaryClipChangedListener {
                val clipData = clipboard.primaryClip
                if (clipData != null && clipData.itemCount > 0) {
                    val text = clipData.getItemAt(0).text?.toString()
                    if (!text.isNullOrBlank()) {
                        saveClipToDatabase(text)
                    }
                }
            }
            clipboard.addPrimaryClipChangedListener(listener)
            clipboardListener = listener
        }
    }

    private fun saveClipToDatabase(text: String) {
        serviceScope.launch(Dispatchers.IO) {
            val clean = text.trim()
            val category = when {
                clean.startsWith("http://", true) || clean.startsWith("https://", true) || clean.contains(".com") -> "URL"
                clean.contains("@") -> "Email"
                clean.all { it.isDigit() || it == '+' || it == '-' || it == ' ' } && clean.length >= 7 -> "Phone"
                else -> "Text"
            }
            database.clipboardDao().insertClip(ClipboardItem(content = clean, category = category))
        }
    }

    override fun onCreateInputView(): View {
        val composeView = ComposeView(this)
        composeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
        composeView.setContent {
            // Collect flow updates safely
            KeyboardView(
                theme = activeThemeState,
                layoutName = layoutState,
                keyHeightDp = keyHeightState,
                cornerRadiusDp = cornerRadiusState,
                blurIntensity = blurIntensityState,
                glassOpacity = glassOpacityState,
                reflectionIntensity = reflectionIntensityState,
                shadowDepth = shadowDepthState,
                fontFamilyStr = fontFamilyState,
                isHapticEnabled = isHapticState,
                hapticStrength = hapticStrengthState,
                isSoundEnabled = isSoundState,
                isAutoCapEnabled = isAutoCapState,
                isAutoSpaceEnabled = isAutoSpaceState,
                clipboardItems = scopeListState,
                onKeyPress = { handleKeyPress(it) },
                onBackspace = { handleBackspace() },
                onSpacebarDragged = { handleSpacebarDrag(it) },
                onActionPressed = { handleReturn() },
                onClipSelected = { commitClip(it) },
                onClipDeleted = { deleteClip(it) },
                onClipPinned = { pinClip(it) },
                onOpenSettings = { launchSettingsScreen() },
                onSwitchKeyboard = {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as? android.view.inputmethod.InputMethodManager
                    imm?.showInputMethodPicker()
                }
            )
        }
        return composeView
    }

    private fun handleKeyPress(text: String) {
        val ic = currentInputConnection ?: return
        var finalCommit = text

        // Auto uppercase logic for the very start of a sentence
        if (isAutoCapState && text.length == 1 && text[0].isLetter()) {
            val extracted = ic.getExtractedText(ExtractedTextRequest(), 0)
            if (extracted != null) {
                val before = extracted.text.toString().take(extracted.selectionStart)
                if (before.isEmpty() || before.trimEnd().endsWith(".") || before.trimEnd().endsWith("?") || before.trimEnd().endsWith("!")) {
                    finalCommit = text.uppercase()
                }
            } else {
                finalCommit = text.uppercase()
            }
        }

        ic.commitText(finalCommit, 1)
    }

    private fun handleBackspace() {
        val ic = currentInputConnection ?: return
        // Smart backspace deletes selected text if available, else past char
        val selectedText = ic.getSelectedText(0)
        if (!selectedText.isNullOrEmpty()) {
            ic.commitText("", 1)
        } else {
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
        }
    }

    private fun handleSpacebarDrag(directionDelta: Int) {
        val ic = currentInputConnection ?: return
        val extracted = ic.getExtractedText(ExtractedTextRequest(), 0) ?: return
        val start = extracted.selectionStart
        val newSelection = (start + directionDelta).coerceIn(0, extracted.text.length)
        ic.setSelection(newSelection, newSelection)
    }

    private fun handleReturn() {
        val ic = currentInputConnection ?: return
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
    }

    private fun commitClip(text: String) {
        val ic = currentInputConnection ?: return
        ic.commitText(text, 1)
    }

    private fun deleteClip(clip: ClipboardItem) {
        serviceScope.launch(Dispatchers.IO) {
            database.clipboardDao().deleteClip(clip)
        }
    }

    private fun pinClip(clip: ClipboardItem) {
        serviceScope.launch(Dispatchers.IO) {
            database.clipboardDao().updateClip(clip.copy(isPinned = !clip.isPinned))
        }
    }

    private fun launchSettingsScreen() {
        val intent = android.content.Intent(this, MainActivity::class.java).apply {
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        if (clipboard != null && clipboardListener != null) {
            clipboard.removePrimaryClipChangedListener(clipboardListener)
        }
        serviceJob.cancel()
        super.onDestroy()
    }
}
