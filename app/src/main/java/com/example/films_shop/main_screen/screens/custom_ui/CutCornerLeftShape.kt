package com.example.films_shop.main_screen.screens.custom_ui

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

class CutCornerLeftShape(private val cutSize: Dp) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val cutPx = with(density) { (cutSize / 2).toPx() }
        val extraWidth = 0f
        val startX = cutPx + extraWidth


        val path = Path().apply {
            moveTo(startX, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
            lineTo(startX, size.height)
            lineTo(0f, size.height / 2)
            close()
        }


        return Outline.Generic(path)
    }
}


