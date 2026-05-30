package com.example.data

import androidx.compose.ui.graphics.Color

data class GlassTheme(
    val id: String,
    val displayName: String,
    val isDark: Boolean,
    val backgroundStart: Color,
    val backgroundEnd: Color,
    val glassColor: Color,
    val glassBorderColor: Color,
    val keyColor: Color,
    val keyActiveColor: Color,
    val textColor: Color,
    val accentColor: Color,
    val accentTextColor: Color = Color.White,
    val glassOpacity: Float = 0.25f,
    val reflectionIntensity: Float = 0.6f,
    val shadowDepth: Float = 6f
)

object ThemeEngine {
    val presetThemes = listOf(
        GlassTheme(
            id = "aurora_glass",
            displayName = "Aurora Glass",
            isDark = true,
            backgroundStart = Color(0xFF1F1C2C),
            backgroundEnd = Color(0xFF928DAB),
            glassColor = Color(0xFF4A346B),
            glassBorderColor = Color(0xFFFFFFFF).copy(alpha = 0.3f),
            keyColor = Color(0xFF33204D).copy(alpha = 0.45f),
            keyActiveColor = Color(0xFFD383FC),
            textColor = Color(0xFFF9F7FC),
            accentColor = Color(0xFF8E2DE2)
        ),
        GlassTheme(
            id = "ocean_glass",
            displayName = "Ocean Glass",
            isDark = true,
            backgroundStart = Color(0xFF0F2027),
            backgroundEnd = Color(0xFF203A43),
            glassColor = Color(0xFF1E3A4D),
            glassBorderColor = Color(0xFF81D4FA).copy(alpha = 0.35f),
            keyColor = Color(0xFF112233).copy(alpha = 0.5f),
            keyActiveColor = Color(0xFF00E5FF),
            textColor = Color(0xFFE0F7FA),
            accentColor = Color(0xFF00838F)
        ),
        GlassTheme(
            id = "crystal_blue",
            displayName = "Crystal Blue",
            isDark = false,
            backgroundStart = Color(0xFFE0F7FA),
            backgroundEnd = Color(0xFF80DEEA),
            glassColor = Color(0xFFFFFFFF),
            glassBorderColor = Color(0xFFFFFFFF).copy(alpha = 0.6f),
            keyColor = Color(0xFFFFFFFF).copy(alpha = 0.5f),
            keyActiveColor = Color(0xFF00E5FF),
            textColor = Color(0xFF006064),
            accentColor = Color(0xFF00BCD4),
            glassOpacity = 0.2f
        ),
        GlassTheme(
            id = "frost_white",
            displayName = "Frost White",
            isDark = false,
            backgroundStart = Color(0xFFF5F7FA),
            backgroundEnd = Color(0xFFB3C6CD),
            glassColor = Color(0xFFFFFFFF),
            glassBorderColor = Color(0xFFFFFFFF).copy(alpha = 0.7f),
            keyColor = Color(0xFFFFFFFF).copy(alpha = 0.65f),
            keyActiveColor = Color(0xFFE0E0E0),
            textColor = Color(0xFF37474F),
            accentColor = Color(0xFF90A4AE),
            glassOpacity = 0.3f
        ),
        GlassTheme(
            id = "lavender_mist",
            displayName = "Lavender Mist",
            isDark = false,
            backgroundStart = Color(0xFFF3E5F5),
            backgroundEnd = Color(0xFFCE93D8),
            glassColor = Color(0xFFFFFFFF),
            glassBorderColor = Color(0xFFFFFFFF).copy(alpha = 0.6f),
            keyColor = Color(0xFFFFFFFF).copy(alpha = 0.55f),
            keyActiveColor = Color(0xFFF3E5F5),
            textColor = Color(0xFF4A148C),
            accentColor = Color(0xFF9C27B0),
            glassOpacity = 0.32f
        ),
        GlassTheme(
            id = "sunset_glass",
            displayName = "Sunset Glass",
            isDark = true,
            backgroundStart = Color(0xFFF45C43),
            backgroundEnd = Color(0xFFEB3349),
            glassColor = Color(0xFF2D142C),
            glassBorderColor = Color(0xFFFF8A80).copy(alpha = 0.4f),
            keyColor = Color(0xFF510A32).copy(alpha = 0.4f),
            keyActiveColor = Color(0xFFFF5252),
            textColor = Color(0xFFFFF1F1),
            accentColor = Color(0xFFC72C41)
        ),
        GlassTheme(
            id = "neon_glass",
            displayName = "Neon Glass",
            isDark = true,
            backgroundStart = Color(0xFF2C1035),
            backgroundEnd = Color(0xFF000000),
            glassColor = Color(0xFF2E083E),
            glassBorderColor = Color(0xFFFF007F).copy(alpha = 0.45f),
            keyColor = Color(0xFF13021D).copy(alpha = 0.6f),
            keyActiveColor = Color(0xFFFF007F),
            textColor = Color(0xFFFFFFFF),
            accentColor = Color(0xFF39FF14)
        ),
        GlassTheme(
            id = "midnight_glass",
            displayName = "Midnight Glass",
            isDark = true,
            backgroundStart = Color(0xFF0D0D11),
            backgroundEnd = Color(0xFF1F1F2E),
            glassColor = Color(0xFF1A1A24),
            glassBorderColor = Color(0xFFFFFFFF).copy(alpha = 0.15f),
            keyColor = Color(0xFF12121A).copy(alpha = 0.55f),
            keyActiveColor = Color(0xFFFFFFFF),
            textColor = Color(0xFFECECEC),
            accentColor = Color(0xFF3D3D5C)
        ),
        GlassTheme(
            id = "rose_glass",
            displayName = "Rose Glass",
            isDark = false,
            backgroundStart = Color(0xFFFFF0F5),
            backgroundEnd = Color(0xFFFFB6C1),
            glassColor = Color(0xFFFFFFFF),
            glassBorderColor = Color(0xFFFFC0CB).copy(alpha = 0.65f),
            keyColor = Color(0xFFFFFFFF).copy(alpha = 0.5f),
            keyActiveColor = Color(0xFFFFE4E1),
            textColor = Color(0xFF8B2500),
            accentColor = Color(0xFFCD6889),
            glassOpacity = 0.35f
        ),
        GlassTheme(
            id = "emerald_glass",
            displayName = "Emerald Glass",
            isDark = true,
            backgroundStart = Color(0xFF0A2E1C),
            backgroundEnd = Color(0xFF1C4D35),
            glassColor = Color(0xFF0F3D26),
            glassBorderColor = Color(0xFF00FF88).copy(alpha = 0.3f),
            keyColor = Color(0xFF061E12).copy(alpha = 0.5f),
            keyActiveColor = Color(0xFF00FF88),
            textColor = Color(0xFFE8F5E9),
            accentColor = Color(0xFF2E7D32)
        ),
        GlassTheme(
            id = "crystal_ruby",
            displayName = "Crystal Ruby",
            isDark = true,
            backgroundStart = Color(0xFF4A1521),
            backgroundEnd = Color(0xFF1A050A),
            glassColor = Color(0xFF5B1020),
            glassBorderColor = Color(0xFFFF4D6D).copy(alpha = 0.4f),
            keyColor = Color(0xFF2B030A).copy(alpha = 0.55f),
            keyActiveColor = Color(0xFFFF4D6D),
            textColor = Color(0xFFFFF0F3),
            accentColor = Color(0xFFC9184A)
        ),
        GlassTheme(
            id = "arctic_mint",
            displayName = "Arctic Mint",
            isDark = false,
            backgroundStart = Color(0xFFE8F8F5),
            backgroundEnd = Color(0xFFA2D9CE),
            glassColor = Color(0xFFFFFFFF),
            glassBorderColor = Color(0xFFFFFFFF).copy(alpha = 0.7f),
            keyColor = Color(0xFFFFFFFF).copy(alpha = 0.6f),
            keyActiveColor = Color(0xFFE8F8F5),
            textColor = Color(0xFF16A085),
            accentColor = Color(0xFF1ABC9C),
            glassOpacity = 0.25f
        ),
        GlassTheme(
            id = "golden_gloss",
            displayName = "Golden Gloss",
            isDark = false,
            backgroundStart = Color(0xFFFFFDF0),
            backgroundEnd = Color(0xFFF0E4B8),
            glassColor = Color(0xFFFFFFFF),
            glassBorderColor = Color(0xFFF7E792).copy(alpha = 0.8f),
            keyColor = Color(0xFFFFFFFF).copy(alpha = 0.65f),
            keyActiveColor = Color(0xFFFFFDF0),
            textColor = Color(0xFF7D6608),
            accentColor = Color(0xFFF1C40F),
            glassOpacity = 0.3f
        ),
        GlassTheme(
            id = "silver_mirror",
            displayName = "Silver Mirror",
            isDark = false,
            backgroundStart = Color(0xFFECEFF1),
            backgroundEnd = Color(0xFFCFD8DC),
            glassColor = Color(0xFFF0F4F8),
            glassBorderColor = Color(0xFFCFD8DC).copy(alpha = 0.8f),
            keyColor = Color(0xFFFFFFFF).copy(alpha = 0.75f),
            keyActiveColor = Color(0xFFECEFF1),
            textColor = Color(0xFF37474F),
            accentColor = Color(0xFF78909C),
            glassOpacity = 0.4f
        ),
        GlassTheme(
            id = "amber_glow",
            displayName = "Amber Glow",
            isDark = true,
            backgroundStart = Color(0xFF291A07),
            backgroundEnd = Color(0xFF140D04),
            glassColor = Color(0xFF3E2723),
            glassBorderColor = Color(0xFFFFB74D).copy(alpha = 0.35f),
            keyColor = Color(0xFF1E120C).copy(alpha = 0.6f),
            keyActiveColor = Color(0xFFFFB74D),
            textColor = Color(0xFFFFF3E0),
            accentColor = Color(0xFFEF6C00)
        ),
        GlassTheme(
            id = "royal_velvet",
            displayName = "Royal Velvet",
            isDark = true,
            backgroundStart = Color(0xFF1A1126),
            backgroundEnd = Color(0xFF0F001A),
            glassColor = Color(0xFF2A1B40),
            glassBorderColor = Color(0xFFB388FF).copy(alpha = 0.4f),
            keyColor = Color(0xFF11071F).copy(alpha = 0.55f),
            keyActiveColor = Color(0xFFB388FF),
            textColor = Color(0xFFEDE7F6),
            accentColor = Color(0xFF673AB7)
        ),
        GlassTheme(
            id = "cherry_blossom",
            displayName = "Cherry Blossom",
            isDark = false,
            backgroundStart = Color(0xFFFFF5F5),
            backgroundEnd = Color(0xFFFFD1D1),
            glassColor = Color(0xFFFFFFFF),
            glassBorderColor = Color(0xFFFFCCD5).copy(alpha = 0.75f),
            keyColor = Color(0xFFFFFFFF).copy(alpha = 0.65f),
            keyActiveColor = Color(0xFFFFF0F3),
            textColor = Color(0xFF590D22),
            accentColor = Color(0xFFFF758F),
            glassOpacity = 0.3f
        ),
        GlassTheme(
            id = "charcoal_frosted",
            displayName = "Charcoal Frosted",
            isDark = true,
            backgroundStart = Color(0xFF212529),
            backgroundEnd = Color(0xFF343A40),
            glassColor = Color(0xFF2E343A),
            glassBorderColor = Color(0xFFFFFFFF).copy(alpha = 0.15f),
            keyColor = Color(0xFF212529).copy(alpha = 0.5f),
            keyActiveColor = Color(0xFF495057),
            textColor = Color(0xFFF8F9FA),
            accentColor = Color(0xFF6C757D)
        ),
        GlassTheme(
            id = "sahara_sand",
            displayName = "Sahara Sand",
            isDark = false,
            backgroundStart = Color(0xFFFAF4EB),
            backgroundEnd = Color(0xFFEEDCC3),
            glassColor = Color(0xFFFFFFFF),
            glassBorderColor = Color(0xFFE5D5C5).copy(alpha = 0.75f),
            keyColor = Color(0xFFFFFFFF).copy(alpha = 0.65f),
            keyActiveColor = Color(0xFFFAF4EB),
            textColor = Color(0xFF5C4033),
            accentColor = Color(0xFFC2A078),
            glassOpacity = 0.35f
        ),
        GlassTheme(
            id = "nebula_glow",
            displayName = "Nebula Glow",
            isDark = true,
            backgroundStart = Color(0xFF050515),
            backgroundEnd = Color(0xFF150A2E),
            glassColor = Color(0xFF1E0A3C),
            glassBorderColor = Color(0xFFBC13FE).copy(alpha = 0.35f),
            keyColor = Color(0xFF0C0219).copy(alpha = 0.6f),
            keyActiveColor = Color(0xFFBC13FE),
            textColor = Color(0xFFF3E8FF),
            accentColor = Color(0xFF7000FF)
        )
    )

    fun getTheme(id: String): GlassTheme {
        return presetThemes.firstOrNull { it.id == id } ?: presetThemes[2] // Crystal Blue defaults
    }

    fun getThemeWithCustom(id: String, customTheme: CustomTheme?): GlassTheme {
        if (id.startsWith("custom_") && customTheme != null) {
            return GlassTheme(
                id = customTheme.id,
                displayName = customTheme.displayName,
                isDark = customTheme.isDark,
                backgroundStart = Color(android.graphics.Color.parseColor(customTheme.backgroundColor)),
                backgroundEnd = Color(android.graphics.Color.parseColor(customTheme.backgroundColor)),
                glassColor = Color(android.graphics.Color.parseColor(customTheme.glassColor)),
                glassBorderColor = Color.White.copy(alpha = 0.4f),
                keyColor = Color(android.graphics.Color.parseColor(customTheme.keyColor)),
                keyActiveColor = Color(android.graphics.Color.parseColor(customTheme.accentColor)),
                textColor = Color(android.graphics.Color.parseColor(customTheme.textColor)),
                accentColor = Color(android.graphics.Color.parseColor(customTheme.accentColor)),
                glassOpacity = customTheme.glassOpacity,
                reflectionIntensity = customTheme.reflectionIntensity,
                shadowDepth = customTheme.shadowDepth
            )
        }
        return getTheme(id)
    }

    fun colorToHexStr(color: Color): String {
        val r = (color.red * 255).toInt().coerceIn(0, 255)
        val g = (color.green * 255).toInt().coerceIn(0, 255)
        val b = (color.blue * 255).toInt().coerceIn(0, 255)
        return String.format("#%02X%02X%02X", r, g, b)
    }
}
