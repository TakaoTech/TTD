package com.takaotech.dashboard.ui.admin.github

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.takaotech.dashboard.model.github.MainCategory
import com.takaotech.dashboard.model.github.TagDao

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
				add(TagDao(1, name = "Annmarie Whitfield", description = null))
				add(TagDao(id = 2111, name = "Lorraine Sherman", description = null))
				add(TagDao(id = 1645, name = "Buddy Simpson", description = null))
				add(TagDao(id = 9869, name = "Tia Holcomb", description = null))
				add(TagDao(id = 9137, name = "Kelly Barker", description = null))
			},
			mainCategory = MainCategory.KOTLIN,
			onTagsEditClicked = {}
		) {}
	}
}