package com.takaotech.dashboard.ui.admin.tags

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.takaotech.dashboard.model.TagDao
import com.takaotech.dashboard.ui.admin.tags.list.TagList
import com.takaotech.dashboard.ui.admin.tags.list.TagListUiState


@Preview(showBackground = true)
@Composable
internal fun TagListPreview() {
	Column {
		TagList(
			tagListUi = TagListUiState.TagListUi.Success(
				listOf(
					TagDao(
						id = 5274,
						name = "Jewell Britt",
						description = null,
						color = "000000"
					)
				)
			),
			onTagClicked = {}
		)
	}
}