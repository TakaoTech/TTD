package com.takaotech.dashboard.ui.admin.tags

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.takaotech.dashboard.model.Tag
import com.takaotech.dashboard.ui.admin.tags.list.TagList
import com.takaotech.dashboard.ui.admin.tags.list.TagListUiState


@Preview(showBackground = true)
@Composable
internal fun TagListPreview() {
	Column {
		TagList(
			tagListUi = TagListUiState.TagListUi.Success(
				listOf(
					Tag("Test Tag1")
				)
			),
			onTagClicked = {}
		)
	}
}