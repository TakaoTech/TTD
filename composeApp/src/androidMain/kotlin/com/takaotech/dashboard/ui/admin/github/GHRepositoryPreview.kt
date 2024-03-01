package com.takaotech.dashboard.ui.admin.github

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.takaotech.dashboard.model.MainCategory
import com.takaotech.dashboard.model.Tag

@Preview
@Composable
private fun GHRepositoryListPreview() {

}

@Preview(showSystemUi = true)
@Composable
private fun GHRepositoryCardPreview() {
	Column {
		GHRepositoryCard(
			modifier = Modifier.fillMaxWidth(),
			fullName = "JetBrains/kotlin",
			tags = buildList {
				add(Tag("chart"))
				add(Tag("jetbrains"))
				add(Tag("experimental"))
				add(Tag("experimental2"))
				add(Tag("experimental3"))
			},
			mainCategory = MainCategory.KOTLIN,
			onTagsEditClicked = {}
		)
	}
}