package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class KeyboardPreferences(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("glassboard_prefs", Context.MODE_PRIVATE)

    private val _keyHeight = MutableStateFlow(prefs.getInt("key_height_dp", 54))
    val keyHeight: StateFlow<Int> = _keyHeight.asStateFlow()

    private val _cornerRadius = MutableStateFlow(prefs.getInt("corner_radius_dp", 12))
    val cornerRadius: StateFlow<Int> = _cornerRadius.asStateFlow()

    private val _blurIntensity = MutableStateFlow(prefs.getFloat("blur_intensity", 15f))
    val blurIntensity: StateFlow<Float> = _blurIntensity.asStateFlow()

    private val _glassOpacity = MutableStateFlow(prefs.getFloat("glass_opacity", 0.35f))
    val glassOpacity: StateFlow<Float> = _glassOpacity.asStateFlow()

    private val _reflectionIntensity = MutableStateFlow(prefs.getFloat("reflection_intensity", 0.5f))
    val reflectionIntensity: StateFlow<Float> = _reflectionIntensity.asStateFlow()

    private val _shadowDepth = MutableStateFlow(prefs.getFloat("shadow_depth", 4f))
    val shadowDepth: StateFlow<Float> = _shadowDepth.asStateFlow()

    private val _activeThemeId = MutableStateFlow(prefs.getString("active_theme_id", "crystal_blue") ?: "crystal_blue")
    val activeThemeId: StateFlow<String> = _activeThemeId.asStateFlow()

    private val _keyboardSize = MutableStateFlow(prefs.getFloat("keyboard_scale", 1.0f))
    val keyboardSize: StateFlow<Float> = _keyboardSize.asStateFlow()

    private val _keyboardPosition = MutableStateFlow(prefs.getString("keyboard_pos", "center") ?: "center") // "center", "left", "right"
    val keyboardPosition: StateFlow<String> = _keyboardPosition.asStateFlow()

    private val _keyboardLayout = MutableStateFlow(prefs.getString("keyboard_layout", "QWERTY") ?: "QWERTY")
    val keyboardLayout: StateFlow<String> = _keyboardLayout.asStateFlow()

    private val _fontFamily = MutableStateFlow(prefs.getString("font_family", "System") ?: "System") // "System", "Serif", "Monospace", "Sans-Serif"
    val fontFamily: StateFlow<String> = _fontFamily.asStateFlow()

    private val _isHapticEnabled = MutableStateFlow(prefs.getBoolean("haptic_feedback", true))
    val isHapticEnabled: StateFlow<Boolean> = _isHapticEnabled.asStateFlow()

    private val _hapticStrength = MutableStateFlow(prefs.getInt("haptic_strength", 50)) // 1 to 100
    val hapticStrength: StateFlow<Int> = _hapticStrength.asStateFlow()

    private val _isSoundEnabled = MutableStateFlow(prefs.getBoolean("sound_feedback", false))
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

    private val _isSwipeTypingEnabled = MutableStateFlow(prefs.getBoolean("swipe_typing", false))
    val isSwipeTypingEnabled: StateFlow<Boolean> = _isSwipeTypingEnabled.asStateFlow()

    private val _isAutoCapEnabled = MutableStateFlow(prefs.getBoolean("auto_cap", true))
    val isAutoCapEnabled: StateFlow<Boolean> = _isAutoCapEnabled.asStateFlow()

    private val _isAutoSpacingEnabled = MutableStateFlow(prefs.getBoolean("auto_spacing", true))
    val isAutoSpacingEnabled: StateFlow<Boolean> = _isAutoSpacingEnabled.asStateFlow()

    private val _isAutoCorrectEnabled = MutableStateFlow(prefs.getBoolean("auto_correct", true))
    val isAutoCorrectEnabled: StateFlow<Boolean> = _isAutoCorrectEnabled.asStateFlow()

    private val _isCursorControlEnabled = MutableStateFlow(prefs.getBoolean("cursor_control", true))
    val isCursorControlEnabled: StateFlow<Boolean> = _isCursorControlEnabled.asStateFlow()

    private val _isHighContrastMode = MutableStateFlow(prefs.getBoolean("high_contrast", false))
    val isHighContrastMode: StateFlow<Boolean> = _isHighContrastMode.asStateFlow()

    private val _customKeysJson = MutableStateFlow(prefs.getString("custom_keys_json", "") ?: "")
    val customKeysJson: StateFlow<String> = _customKeysJson.asStateFlow()

    // Single-view background image
    private val _backgroundUri = MutableStateFlow(prefs.getString("background_uri", "") ?: "")
    val backgroundUri: StateFlow<String> = _backgroundUri.asStateFlow()

    // Theme Engine customizations
    fun updateKeyHeight(value: Int) {
        prefs.edit().putInt("key_height_dp", value).apply()
        _keyHeight.value = value
    }

    fun updateCornerRadius(value: Int) {
        prefs.edit().putInt("corner_radius_dp", value).apply()
        _cornerRadius.value = value
    }

    fun updateBlurIntensity(value: Float) {
        prefs.edit().putFloat("blur_intensity", value).apply()
        _blurIntensity.value = value
    }

    fun updateGlassOpacity(value: Float) {
        prefs.edit().putFloat("glass_opacity", value).apply()
        _glassOpacity.value = value
    }

    fun updateReflectionIntensity(value: Float) {
        prefs.edit().putFloat("reflection_intensity", value).apply()
        _reflectionIntensity.value = value
    }

    fun updateShadowDepth(value: Float) {
        prefs.edit().putFloat("shadow_depth", value).apply()
        _shadowDepth.value = value
    }

    fun updateActiveThemeId(value: String) {
        prefs.edit().putString("active_theme_id", value).apply()
        _activeThemeId.value = value
    }

    fun updateKeyboardSize(value: Float) {
        prefs.edit().putFloat("keyboard_scale", value).apply()
        _keyboardSize.value = value
    }

    fun updateKeyboardPosition(value: String) {
        prefs.edit().putString("keyboard_pos", value).apply()
        _keyboardPosition.value = value
    }

    fun updateKeyboardLayout(value: String) {
        prefs.edit().putString("keyboard_layout", value).apply()
        _keyboardLayout.value = value
    }

    fun updateFontFamily(value: String) {
        prefs.edit().putString("font_family", value).apply()
        _fontFamily.value = value
    }

    fun updateHapticEnabled(value: Boolean) {
        prefs.edit().putBoolean("haptic_feedback", value).apply()
        _isHapticEnabled.value = value
    }

    fun updateHapticStrength(value: Int) {
        prefs.edit().putInt("haptic_strength", value).apply()
        _hapticStrength.value = value
    }

    fun updateSoundEnabled(value: Boolean) {
        prefs.edit().putBoolean("sound_feedback", value).apply()
        _isSoundEnabled.value = value
    }

    fun updateSwipeTypingEnabled(value: Boolean) {
        prefs.edit().putBoolean("swipe_typing", value).apply()
        _isSwipeTypingEnabled.value = value
    }

    fun updateAutoCapEnabled(value: Boolean) {
        prefs.edit().putBoolean("auto_cap", value).apply()
        _isAutoCapEnabled.value = value
    }

    fun updateAutoSpacingEnabled(value: Boolean) {
        prefs.edit().putBoolean("auto_spacing", value).apply()
        _isAutoSpacingEnabled.value = value
    }

    fun updateAutoCorrectEnabled(value: Boolean) {
        prefs.edit().putBoolean("auto_correct", value).apply()
        _isAutoCorrectEnabled.value = value
    }

    fun updateCursorControlEnabled(value: Boolean) {
        prefs.edit().putBoolean("cursor_control", value).apply()
        _isCursorControlEnabled.value = value
    }

    fun updateHighContrastMode(value: Boolean) {
        prefs.edit().putBoolean("high_contrast", value).apply()
        _isHighContrastMode.value = value
    }

    fun updateCustomKeysJson(value: String) {
        prefs.edit().putString("custom_keys_json", value).apply()
        _customKeysJson.value = value
    }

    fun updateBackgroundUri(value: String) {
        prefs.edit().putString("background_uri", value).apply()
        _backgroundUri.value = value
    }

    fun exportSettingsToJson(): String {
        return """
        {
          "keyHeight": ${keyHeight.value},
          "cornerRadius": ${cornerRadius.value},
          "blurIntensity": ${blurIntensity.value},
          "glassOpacity": ${glassOpacity.value},
          "reflectionIntensity": ${reflectionIntensity.value},
          "shadowDepth": ${shadowDepth.value},
          "activeThemeId": "${activeThemeId.value}",
          "keyboardSize": ${keyboardSize.value},
          "keyboardPosition": "${keyboardPosition.value}",
          "keyboardLayout": "${keyboardLayout.value}",
          "fontFamily": "${fontFamily.value}",
          "isHapticEnabled": ${isHapticEnabled.value},
          "hapticStrength": ${hapticStrength.value},
          "isSoundEnabled": ${isSoundEnabled.value},
          "isSwipeTypingEnabled": ${isSwipeTypingEnabled.value},
          "isAutoCapEnabled": ${isAutoCapEnabled.value},
          "isAutoSpacingEnabled": ${isAutoSpacingEnabled.value},
          "isAutoCorrectEnabled": ${isAutoCorrectEnabled.value},
          "isCursorControlEnabled": ${isCursorControlEnabled.value},
          "isHighContrastMode": ${isHighContrastMode.value},
          "backgroundUri": "${backgroundUri.value}"
        }
        """.trimIndent()
    }

    fun importSettingsFromJson(json: String): Boolean {
        try {
            // High-fidelity helper parsing simple JSON
            val keys = listOf(
                "keyHeight" to "key_height_dp",
                "cornerRadius" to "corner_radius_dp",
                "blurIntensity" to "blur_intensity",
                "glassOpacity" to "glass_opacity",
                "reflectionIntensity" to "reflection_intensity",
                "shadowDepth" to "shadow_depth",
                "activeThemeId" to "active_theme_id",
                "keyboardSize" to "keyboard_scale",
                "keyboardPosition" to "keyboard_pos",
                "keyboardLayout" to "keyboard_layout",
                "fontFamily" to "font_family",
                "isHapticEnabled" to "haptic_feedback",
                "hapticStrength" to "haptic_strength",
                "isSoundEnabled" to "sound_feedback",
                "isSwipeTypingEnabled" to "swipe_typing",
                "isAutoCapEnabled" to "auto_cap",
                "isAutoSpacingEnabled" to "auto_spacing",
                "isAutoCorrectEnabled" to "auto_correct",
                "isCursorControlEnabled" to "cursor_control",
                "isHighContrastMode" to "high_contrast",
                "backgroundUri" to "background_uri"
            )

            val editor = prefs.edit()
            for ((key, prefKey) in keys) {
                val match = Regex("\"$key\"\\s*:\\s*([^\n,]+)").find(json)
                if (match != null) {
                    val rawVal = match.groupValues[1].replace("\"", "").trim()
                    when {
                        rawVal.equals("true", true) -> editor.putBoolean(prefKey, true)
                        rawVal.equals("false", true) -> editor.putBoolean(prefKey, false)
                        rawVal.contains(".") -> editor.putFloat(prefKey, rawVal.toFloat())
                        rawVal.toIntOrNull() != null -> editor.putInt(prefKey, rawVal.toInt())
                        else -> editor.putString(prefKey, rawVal)
                    }
                }
            }
            editor.apply()
            
            // Reload
            _keyHeight.value = prefs.getInt("key_height_dp", 54)
            _cornerRadius.value = prefs.getInt("corner_radius_dp", 12)
            _blurIntensity.value = prefs.getFloat("blur_intensity", 15f)
            _glassOpacity.value = prefs.getFloat("glass_opacity", 0.35f)
            _reflectionIntensity.value = prefs.getFloat("reflection_intensity", 0.5f)
            _shadowDepth.value = prefs.getFloat("shadow_depth", 4f)
            _activeThemeId.value = prefs.getString("active_theme_id", "crystal_blue") ?: "crystal_blue"
            _keyboardSize.value = prefs.getFloat("keyboard_scale", 1.0f)
            _keyboardPosition.value = prefs.getString("keyboard_pos", "center") ?: "center"
            _keyboardLayout.value = prefs.getString("keyboard_layout", "QWERTY") ?: "QWERTY"
            _fontFamily.value = prefs.getString("font_family", "System") ?: "System"
            _isHapticEnabled.value = prefs.getBoolean("haptic_feedback", true)
            _hapticStrength.value = prefs.getInt("haptic_strength", 50)
            _isSoundEnabled.value = prefs.getBoolean("sound_feedback", false)
            _isSwipeTypingEnabled.value = prefs.getBoolean("swipe_typing", false)
            _isAutoCapEnabled.value = prefs.getBoolean("auto_cap", true)
            _isAutoSpacingEnabled.value = prefs.getBoolean("auto_spacing", true)
            _isAutoCorrectEnabled.value = prefs.getBoolean("auto_correct", true)
            _isCursorControlEnabled.value = prefs.getBoolean("cursor_control", true)
            _isHighContrastMode.value = prefs.getBoolean("high_contrast", false)
            _backgroundUri.value = prefs.getString("background_uri", "") ?: ""
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun resetAll() {
        prefs.edit().clear().apply()
        importSettingsFromJson("{}") // Reset triggers default falling back
    }
}
