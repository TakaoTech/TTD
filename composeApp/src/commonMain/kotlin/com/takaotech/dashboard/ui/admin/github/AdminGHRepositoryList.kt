package com.takaotech.dashboard.ui.admin.github

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.takaotech.dashboard.model.github.MainCategory
import com.takaotech.dashboard.model.github.TagDao
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import ttd.composeapp.generated.resources.Res
import ttd.composeapp.generated.resources.ghrepository_no_tags
import ttd.composeapp.generated.resources.ghrepository_tags_label

@Composable
fun AdminGHRepositoryList(
	ghRepositoryState: GHRepositoryListUiState.GhRepositoryListState,
	modifier: Modifier = Modifier,
	onCardClicked: (url: String) -> Unit,
	onTagEditClicked: (repoId: Long) -> Unit,
	onCategoryChangeClicked: (repoId: Long, newCategory: MainCategory) -> Unit
) {
	when (ghRepositoryState) {
		GHRepositoryListUiState.GhRepositoryListState.Error -> {
			//TODO
		}

		GHRepositoryListUiState.GhRepositoryListState.Loading -> {
			LinearProgressIndicator(
				modifier = Modifier.fillMaxWidth()
			)
		}

		is GHRepositoryListUiState.GhRepositoryListState.Success -> {
			val repoList = ghRepositoryState.ghRepositoryData
			LazyColumn(modifier = modifier) {
				items(key = { it.id }, items = repoList) {
					var openBottomSheet by rememberSaveable { mutableStateOf(false) }

					if (openBottomSheet) {
						MainCategoryBottomSheet(
							categoryList = MainCategory.entries,
							onDismissRequest = {
								openBottomSheet = false
							},
							onCategoryClicked = { newCategory ->
								//not null because parameter entries not have nulls
								onCategoryChangeClicked(it.id, newCategory!!)
							}
						)
					}



					AdminGHRepositoryCard(
						modifier = Modifier.padding(8.dp),
						fullName = it.fullName,
						tags = it.tags,
						mainCategory = it.mainCategory,
						onMainCategoryClicked = {
							openBottomSheet = true
						},
						onTagEditClicked = {
							onTagEditClicked(it.id)
						},
						onCardClicked = {
							onCardClicked(it.url)
						}
					)
				}
			}
		}
	}


}

@OptIn(ExperimentalMaterialApi::class, ExperimentalResourceApi::class)
@Composable
internal fun AdminGHRepositoryCard(
	fullName: String,
	mainCategory: MainCategory,
	tags: List<TagDao>,
	onMainCategoryClicked: () -> Unit,
	onTagEditClicked: () -> Unit,
	modifier: Modifier = Modifier,
	onCardClicked: () -> Unit
) {
	Card(modifier = modifier, onClick = onCardClicked) {
		Column(modifier = Modifier.padding(16.dp)) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				Text(
					modifier = Modifier.weight(1f),
					text = fullName
				)
				Chip(onClick = {
					onMainCategoryClicked()
				}) {
					Text(mainCategory.name)
				}

			}

			Text(stringResource(Res.string.ghrepository_tags_label))
			Row {
				if (tags.isEmpty()) {
					Text(
						modifier = Modifier.weight(1f),
						text = stringResource(Res.string.ghrepository_no_tags)
					)
				} else {
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
				}



				IconButton(
					onClick = onTagEditClicked
				) {
					//TODO contentDesc
					Icon(Icons.Filled.Edit, "Edit tags")
				}
			}
			//TODO Chart as Github
		}
	}
}

