package com.one.task

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import onetask.shared.generated.resources.Res
import onetask.shared.generated.resources.app_name
import onetask.shared.generated.resources.launcher
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash")

    // Subtle breathing scale on the logo
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoScale"
    )

    // Glow pulse behind logo
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    // Rotating arc sweep angle
    val arcRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "arcRotation"
    )

    // Arc sweep (expressive stretch & shrink)
    val arcSweep by infiniteTransition.animateFloat(
        initialValue = 40f,
        targetValue = 270f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1400
                40f  at 0    using FastOutSlowInEasing
                270f at 700  using FastOutSlowInEasing
                40f  at 1400 using FastOutSlowInEasing
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "arcSweep"
    )

    // Subtitle fade
    val subtitleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "subtitleAlpha"
    )

    val primaryColor   = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val trackColor     = MaterialTheme.colorScheme.surfaceContainerHigh

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            // ── Logo + circular progress ring ────────────────────────────────
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(160.dp)
            ) {
                // Pulsing glow disc behind logo
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .alpha(glowAlpha)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(primaryColor, Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                )

                // Expressive circular progress arc
                CircularProgressArc(
                    rotation    = arcRotation,
                    sweepAngle  = arcSweep,
                    trackColor  = trackColor,
                    arcColor    = primaryColor,
                    trailColor  = secondaryColor,
                    size        = 152.dp,
                    strokeWidth = 4.dp
                )

                // App logo — sits in the centre of the ring
                Image(
                    painter = painterResource(Res.drawable.launcher),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(80.dp)
                        .scale(logoScale)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── App name ────────────────────────────────────────────────────
            Text(
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            // ── Subtitle ────────────────────────────────────────────────────
            Text(
                text = "Loading your workspace…",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = subtitleAlpha)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Expressive arc — a track ring + a rotating gradient-filled sweep arc
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CircularProgressArc(
    rotation: Float,
    sweepAngle: Float,
    trackColor: Color,
    arcColor: Color,
    trailColor: Color,
    size: Dp,
    strokeWidth: Dp
) {
    Canvas(modifier = Modifier.size(size)) {
        val stroke = Stroke(
            width = strokeWidth.toPx(),
            cap   = StrokeCap.Round
        )
        val inset = strokeWidth.toPx() / 2f
        val arcRect = androidx.compose.ui.geometry.Rect(
            left   = inset,
            top    = inset,
            right  = this.size.width  - inset,
            bottom = this.size.height - inset
        )

        // Track (faint full circle)
        drawArc(
            color      = trackColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter  = false,
            topLeft    = arcRect.topLeft,
            size       = arcRect.size,
            style      = Stroke(width = strokeWidth.toPx() * 0.6f)
        )

        // Expressive rotating sweep arc with gradient colours
        drawArc(
            brush = Brush.sweepGradient(
                0f   to trailColor.copy(alpha = 0.3f),
                0.5f to arcColor,
                1f   to trailColor.copy(alpha = 0.3f)
            ),
            startAngle = rotation,
            sweepAngle = sweepAngle,
            useCenter  = false,
            topLeft    = arcRect.topLeft,
            size       = arcRect.size,
            style      = stroke
        )

        // Bright leading dot at the head of the arc
        val headAngleRad = Math.toRadians((rotation + sweepAngle).toDouble())
        val cx = this.size.width  / 2f
        val cy = this.size.height / 2f
        val radius = (this.size.width - strokeWidth.toPx()) / 2f
        val dotX = (cx + radius * cos(headAngleRad)).toFloat()
        val dotY = (cy + radius * sin(headAngleRad)).toFloat()

        drawCircle(
            color  = arcColor,
            radius = strokeWidth.toPx() * 0.7f,
            center = androidx.compose.ui.geometry.Offset(dotX, dotY)
        )
    }
}
