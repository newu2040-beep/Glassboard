package com.example.ui.keyboard

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

fun Modifier.glassmorphicContainer(
    cornerRadius: Dp,
    glassColor: Color,
    glassOpacity: Float,
    borderAlpha: Float = 0.4f,
    reflectionIntensity: Float = 0.6f,
    shadowDepth: Float = 6f,
    blurIntensity: Float = 15f
): Modifier {
    // 1. Shadow Layer (Perfectly sharp, drawn behind the blurred layer)
    val shadowModifier = this.drawBehind {
        val radiusPx = cornerRadius.toPx()
        if (shadowDepth > 0f) {
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.12f * (shadowDepth / 6f).coerceIn(0f, 1f)),
                topLeft = Offset(0f, shadowDepth),
                size = this.size,
                cornerRadius = CornerRadius(radiusPx, radiusPx)
            )
        }
    }

    // 2. Translucent glass background fill that gets blurred inside its own clipped graphicsLayer
    val blurredModifier = shadowModifier.graphicsLayer {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && blurIntensity > 0f) {
            val blur = RenderEffect.createBlurEffect(
                blurIntensity,
                blurIntensity,
                Shader.TileMode.CLAMP
            )
            renderEffect = blur.asComposeRenderEffect()
            clip = true
        }
    }.drawBehind {
        val radiusPx = cornerRadius.toPx()
        drawRoundRect(
            color = glassColor.copy(alpha = glassOpacity),
            size = this.size,
            cornerRadius = CornerRadius(radiusPx, radiusPx)
        )
    }

    // 3. Highlight Gloss reflection and white frosted border outlines drawn sharp on top
    return blurredModifier.graphicsLayer {
        // Enforce a separate graphics layer to prevent GPU layer merging from blurring this draw pass
    }.drawBehind {
        val radiusPx = cornerRadius.toPx()

        // Highlight Glow reflection from upper-left (Liquid Shine)
        val glossBrush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.22f * reflectionIntensity),
                Color.White.copy(alpha = 0.02f)
            ),
            start = Offset(0f, 0f),
            end = Offset(this.size.width * 0.4f, this.size.height)
        )
        drawRoundRect(
            brush = glossBrush,
            size = this.size,
            cornerRadius = CornerRadius(radiusPx, radiusPx)
        )

        // White frosted inner borders
        val strokePx = 1.5f
        drawRoundRect(
            color = Color.White.copy(alpha = borderAlpha * (glassOpacity + 0.15f).coerceIn(0f, 1f)),
            size = this.size,
            cornerRadius = CornerRadius(radiusPx, radiusPx),
            style = Stroke(width = strokePx)
        )
    }
}

@Composable
fun Modifier.glassmorphicClickableKey(
    cornerRadius: Dp,
    keyColor: Color,
    isActive: Boolean = false,
    activeColor: Color = Color.White,
    reflectionIntensity: Float = 0.5f,
    shadowDepth: Float = 3f,
    testTagStr: String = "key_button",
    onDoubleClick: (() -> Unit)? = null,
    onClick: () -> Unit
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState().value
    val scaleAnim = remember { Animatable(1.0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(isPressed || isActive) {
        val targetScale = if (isPressed || isActive) 0.9f else 1.0f
        scaleAnim.animateTo(
            targetScale,
            animationSpec = spring(dampingRatio = 0.55f, stiffness = 400f)
        )
    }

    return this
        .graphicsLayer {
            scaleX = scaleAnim.value
            scaleY = scaleAnim.value
        }
        .drawBehind {
            val radiusPx = cornerRadius.toPx()
            val fill = if (isPressed || isActive) activeColor.copy(alpha = 0.7f) else keyColor

            // Key Drop shadow
            if (shadowDepth > 0f) {
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.08f),
                    topLeft = Offset(0f, shadowDepth),
                    size = this.size,
                    cornerRadius = CornerRadius(radiusPx, radiusPx)
                )
            }

            // Glass fill
            drawRoundRect(
                color = fill,
                size = this.size,
                cornerRadius = CornerRadius(radiusPx, radiusPx)
            )

            // High-fidelity liquid specular glare
            val glossGradient = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.15f * reflectionIntensity),
                    Color.Transparent
                ),
                startY = 0f,
                endY = this.size.height * 0.45f
            )
            drawRoundRect(
                brush = glossGradient,
                size = this.size,
                cornerRadius = CornerRadius(radiusPx, radiusPx)
            )

            // Inner border outline
            val borderAlpha = if (isPressed || isActive) 0.7f else 0.25f
            drawRoundRect(
                color = Color.White.copy(alpha = borderAlpha),
                size = this.size,
                cornerRadius = CornerRadius(radiusPx, radiusPx),
                style = Stroke(width = 1.0f)
            )
        }
        .testTag(testTagStr)
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
}
