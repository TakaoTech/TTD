package com.takaotech.dashboard.ui.platform.components

import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.takaotech.dashboard.ui.utils.assistChipColors
import com.takaotech.dashboard.ui.utils.toColor

@Composable
fun TagChip(
    text: String,
    color: String? = null,
    onTagClicked: () -> Unit,
) {
    AssistChip(
        onClick = onTagClicked,
        colors = color?.toColor().assistChipColors(),
        label = {
            Text(text = text)
        },
    )
}
