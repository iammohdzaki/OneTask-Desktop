package com.one.task.presentation.ui

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

object Motion {
    object Duration {
        const val Short1 = 100
        const val Short2 = 150
        const val Short3 = 200
        const val Medium1 = 250
        const val Medium2 = 300
        const val Medium3 = 350
        const val Medium4 = 400
        const val Long1 = 450
        const val Long2 = 500
        const val Long3 = 550
        const val Long4 = 600
    }

    object Easing {
        val Emphasized = CubicBezierEasing(0.2f, 0f, 0f, 1f)
        val EmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
        val EmphasizedAccelerate = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)
        val Standard = FastOutSlowInEasing
    }

    object Spec {
        val Emphasized = tween<Float>(durationMillis = Duration.Medium4, easing = Easing.Emphasized)
        val Enter = tween<Float>(durationMillis = Duration.Medium4, easing = Easing.EmphasizedDecelerate)
        val Exit = tween<Float>(durationMillis = Duration.Short3, easing = Easing.EmphasizedAccelerate)
        
        fun <T> enter() = tween<T>(durationMillis = Duration.Medium4, easing = Easing.EmphasizedDecelerate)
        fun <T> exit() = tween<T>(durationMillis = Duration.Short3, easing = Easing.EmphasizedAccelerate)
        fun <T> standard() = tween<T>(durationMillis = Duration.Medium2, easing = Easing.Standard)
        
        fun <T> springBouncy() = spring<T>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
        
        fun <T> springStandard() = spring<T>(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )
        
        fun <T> springStiff() = spring<T>(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        )
    }
}
