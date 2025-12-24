package com.name.petmemo.ui.components

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class WavyBottomBarShape(
    private val circleRadius: Float = 28f,
    private val dipHeight: Float = 15f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, size.height)
            lineTo(0f, 0f)
            val center = size.width / 2f
            val startWaveX = center - circleRadius * 1.5f
            val endWaveX = center + circleRadius * 1.5f
            val startBezierX = center - circleRadius * 0.8f
            val endBezierX = center + circleRadius * 0.8f
            lineTo(startWaveX, 0f)
            cubicTo(
                x1 = startBezierX, y1 = 0f,
                x2 = center - circleRadius, y2 = dipHeight,
                x3 = center, y3 = dipHeight
            )

            cubicTo(
                x1 = center + circleRadius, y1 = dipHeight,
                x2 = endBezierX, y2 = 0f,
                x3 = endWaveX, y3 = 0f
            )
            lineTo(size.width, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}