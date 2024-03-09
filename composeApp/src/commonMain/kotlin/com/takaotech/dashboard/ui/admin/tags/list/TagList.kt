package com.takaotech.dashboard.ui.admin.tags.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TagList(
	tagListUi: TagListUiState.TagListUi,
	onTagClicked: (tagId: String) -> Unit
) {
	when (tagListUi) {
		TagListUiState.TagListUi.Error -> {
			//TODO()
		}

		TagListUiState.TagListUi.Loading -> {
			LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
		}

		is TagListUiState.TagListUi.Success -> {
			LazyColumn {
				items(tagListUi.tagList) {
					Text(
						modifier = Modifier.clickable {
							onTagClicked(it.name)
						},
						text = it.name
					)
				}
			}
		}
	}
}

