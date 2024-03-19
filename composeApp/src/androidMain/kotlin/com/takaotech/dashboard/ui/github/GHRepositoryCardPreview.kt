package com.takaotech.dashboard.ui.github

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview


@Preview
@Composable
fun GHRepositoryCardPreview() {
	MaterialTheme {
		GHRepositoryCard(
			fullName = "Malcolm Nelson",
			tags = listOf(),
			modifier = Modifier.fillMaxWidth(),
			languages = listOf(),
			onCardClicked = {}
		)
	}
}