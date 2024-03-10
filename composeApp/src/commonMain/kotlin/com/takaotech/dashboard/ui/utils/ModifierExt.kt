package com.takaotech.dashboard.ui.utils

import androidx.compose.ui.graphics.Color

fun String.toColor(): Color {
	val red = substring(0, 2).toInt(16) / 255f
	val green = substring(2, 4).toInt(16) / 255f
	val blue = substring(4, 6).toInt(16) / 255f
	return Color(red, green, blue, alpha = 1f)
}