package com.takaotech.dashboard.ui.admin.tags.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.takaotech.dashboard.ui.utils.assistChipColors
import com.takaotech.dashboard.ui.utils.toColor

@Composable
fun TagList(
	tagListUi: TagListUiState.TagListUi,
	onTagClicked: (tagId: Int) -> Unit
) {
	when (tagListUi) {
		TagListUiState.TagListUi.Error -> {
			//TODO()
		}

		TagListUiState.TagListUi.Loading -> {
			LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
		}

		is TagListUiState.TagListUi.Success -> {
			LazyColumn(
				contentPadding = PaddingValues(16.dp)
			) {
				items(tagListUi.tagList) {
					AssistChip(
						onClick = {
							onTagClicked(it.id)
						},
						colors = it.color?.toColor().assistChipColors(),
						label = {
							Text(text = it.name)
						}
					)
				}
			}
		}
	}
}

