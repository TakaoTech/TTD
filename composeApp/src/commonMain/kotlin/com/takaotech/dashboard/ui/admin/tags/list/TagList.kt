package com.takaotech.dashboard.ui.admin.tags.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.takaotech.dashboard.ui.utils.toColor

@OptIn(ExperimentalMaterialApi::class)
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
					val chipBackgroundColor =
						it.color?.let { color -> ChipDefaults.chipColors(backgroundColor = color.toColor()) }
							?: ChipDefaults.chipColors()
					Chip(
						onClick = {
							onTagClicked(it.id)
						},
						colors = chipBackgroundColor,
					) {

						//TODO Adapt text color from Chip Background color
						val isDark = it.color?.toColor()?.luminance()?.let { luminance ->
							luminance < 0.5
						} ?: false

						Text(
							color = if (isDark) {
								Color.White
							} else {
								Color.Black
							},
							text = it.name
						)
					}


				}
			}
		}
	}
}

