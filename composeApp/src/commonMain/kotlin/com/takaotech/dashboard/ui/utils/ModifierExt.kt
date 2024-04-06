package com.takaotech.dashboard.ui.utils

import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ChipColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

fun String.toColor(): Color {
	val baseColor = replace("#", "")

	val red = baseColor.substring(0, 2).toInt(16) / 255f
	val green = baseColor.substring(2, 4).toInt(16) / 255f
	val blue = baseColor.substring(4, 6).toInt(16) / 255f
	return Color(red, green, blue, alpha = 1f)
}

@Composable
fun Color?.assistChipColors(): ChipColors {
	return this?.let { color ->
		val isDark = color.luminance() < 0.5

		AssistChipDefaults.assistChipColors(
			containerColor = color,
			labelColor = if (isDark) {
				Color.White
			} else {
				Color.Black
			}
		)
	}
		?: AssistChipDefaults.assistChipColors()
}