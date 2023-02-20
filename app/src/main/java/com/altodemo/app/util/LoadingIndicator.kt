package com.altodemo.app.util

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.altodemo.app.ui.theme.AltoDemoColor

// Clean loading Indicator
// - pulled from SO https://stackoverflow.com/questions/73966501/circular-loading-spinner-in-jetpack-compose
@Composable
fun LoadingIndicator(
) {
    ////// animation //////
    // docs recommend use transition animation for infinite loops
    // https://developer.android.com/jetpack/compose/animation
    val transition = rememberInfiniteTransition()

    // define the changing value from 0 to 360.
    // This is the angle of the beginning of indicator arc
    // this value will change over time from 0 to 360 and repeat indefinitely.
    // it changes starting position of the indicator arc and the animation is obtained
    val currentArcStartAngle by transition.animateValue(
        -35,
        325,
        Int.VectorConverter,
        infiniteRepeatable(
            animation = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            )
        )
    )

    ////// draw /////

    // define stroke with given width and arc ends type considering device DPI
    val stroke = with(LocalDensity.current) {
        Stroke(width = ProgressIndicatorDefaults.CircularStrokeWidth.toPx() - 5, cap = StrokeCap.Square)
    }

    // draw on canvas
    Canvas(
        Modifier
            .progressSemantics() // (optional) for Accessibility services
            .size(52.dp) // canvas size
            .padding(ProgressIndicatorDefaults.CircularStrokeWidth / 2) //padding. otherwise, not the whole circle will fit in the canvas
    ) {

        // draw "background" (gray) circle with defined stroke.
        // without explicit center and radius it fit canvas bounds
        drawCircle(AltoDemoColor.Divider, style = stroke)

        // draw arc with the same stroke
        drawArc(
            AltoDemoColor.Brown80,
            // arc start angle
            // -90 shifts the start position towards the y-axis
            startAngle = currentArcStartAngle.toFloat() - 90,
            sweepAngle = 67.5f,
            useCenter = false,
            style = stroke
        )
    }
}