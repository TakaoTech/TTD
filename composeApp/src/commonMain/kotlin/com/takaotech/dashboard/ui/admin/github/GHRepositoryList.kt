package com.takaotech.dashboard.ui.admin.github

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.takaotech.dashboard.model.GHRepository
import com.takaotech.dashboard.model.MainCategory
import com.takaotech.dashboard.model.Tag

@Composable
fun GHRepositoryList(
	list: List<GHRepository>
) {
	LazyColumn {
		items(key = { it.id }, items = list) {
			GHRepositoryCard(
				fullName = it.fullName,
				tags = it.tags,
				mainCategory = it.mainCategory,
				onTagsEditClicked = {

				}
			)
		}
	}
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun GHRepositoryCard(
	fullName: String,
	mainCategory: MainCategory,
	tags: List<Tag>,
	onTagsEditClicked: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Card(modifier) {
		Column(modifier = Modifier.padding(16.dp)) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				Text(
					modifier = Modifier.weight(1f),
					text = fullName
				)
				Chip(onClick = {}) {
					Text(mainCategory.name)
				}

			}

			Text("Tags")
			Row {
				LazyRow(
					modifier = Modifier.weight(1f),
					horizontalArrangement = Arrangement.spacedBy(4.dp)
				) {
					items(tags) {
						Chip(onClick = {}) {
							Text(it.name)
						}
					}
				}

				IconButton(
					onClick = onTagsEditClicked
				) {
					//TODO contentDesc
					Icon(Icons.Filled.Edit, "Edit tags")
				}
			}
			//TODO Chart as Github
		}
	}
}

