package com.takaotech.dashboard.ui.github

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.takaotech.dashboard.model.github.GHRepositoryMiniDao
import com.takaotech.dashboard.model.github.TagDao
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import ttd.composeapp.generated.resources.Res
import ttd.composeapp.generated.resources.homepage_ghrepository_more_tags_label
import ttd.composeapp.generated.resources.homepage_ghrepository_tags_label
import ttd.composeapp.generated.resources.homepage_title_label


@OptIn(ExperimentalMaterialApi::class, ExperimentalResourceApi::class)
@Composable
fun HomePageScreen(
	tags: List<TagDao>,
	repositories: List<GHRepositoryMiniDao>,
	onTagClicked: (tagId: Int) -> Unit,
	onMoreTagClicked: () -> Unit,
	onCardClicked: (repoId: Long) -> Unit,
	onMoreRepositoriesClicked: () -> Unit
) {
	val hazeState = remember { HazeState() }
	val listState = rememberLazyListState()
	val isCollapsed: Boolean by remember {
		derivedStateOf { listState.firstVisibleItemIndex > 0 }
	}

	Scaffold(
		topBar = {
			CollapsedTopBar(
				modifier = Modifier
					.fillMaxWidth()
					.hazeChild(state = hazeState),
				isCollapsed = isCollapsed
			)
		}
	) {
		LazyColumn(
			modifier = Modifier.padding(
				bottom = it.calculateBottomPadding()
			).haze(
				state = hazeState,
			),
			state = listState,
		) {
			item { ExpandedTopBar() }
			if (tags.isNotEmpty()) {
				item {
					LazyRow(
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.spacedBy(8.dp),
						contentPadding = PaddingValues(8.dp)
					) {
						item {
							Text(stringResource(Res.string.homepage_ghrepository_tags_label))
						}

						items(tags) {
							Chip(
								onClick = {
									onTagClicked(it.id)
								}
							) {
								Text(it.name)
							}
						}

						item {
							TextButton(
								onClick = onMoreTagClicked,
							) {
								Text(stringResource(Res.string.homepage_ghrepository_more_tags_label))
								Icon(Icons.AutoMirrored.Filled.ArrowForward, "")
							}
						}
					}
				}
			}

			items(repositories) {
				GHRepositoryCard(
					modifier = Modifier.fillMaxWidth()
						.padding(horizontal = 16.dp, vertical = 8.dp),
					fullName = it.fullName,
					tags = it.tags,
					languages = it.languages,
					onTagClicked = {
						onTagClicked(it)
					},
					onCardClicked = {
						onCardClicked(it.id)
					}
				)
			}

			if (repositories.isNotEmpty()) {
				item {
					Row(
						modifier = Modifier.fillMaxWidth()
							.clickable(
								onClick = onMoreRepositoriesClicked
							).padding(horizontal = 8.dp, vertical = 16.dp),
						horizontalArrangement = Arrangement.SpaceBetween,
						verticalAlignment = Alignment.CenterVertically
					) {
						Text("Show More Repositories")

						Icon(Icons.AutoMirrored.Filled.ArrowForward, "")
					}
				}
			}
		}


	}
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun ExpandedTopBar() {
	Box(
		modifier = Modifier
			.background(MaterialTheme.colors.primaryVariant)
			.fillMaxWidth()
			.height(EXPANDED_TOP_BAR_HEIGHT - COLLAPSED_TOP_BAR_HEIGHT),
		contentAlignment = Alignment.BottomStart
	) {
		Text(
			modifier = Modifier.padding(16.dp),
			text = stringResource(Res.string.homepage_title_label),
			color = MaterialTheme.colors.onPrimary,
			style = MaterialTheme.typography.h3,
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
private fun CollapsedTopBar(
	modifier: Modifier = Modifier,
	isCollapsed: Boolean
) {
	val layoutDirection = LocalLayoutDirection.current

	TopAppBar(
		modifier = modifier,
		colors = TopAppBarDefaults.largeTopAppBarColors(Color.Transparent),
		title = {
			AnimatedVisibility(
				visible = isCollapsed,
				enter = slideInHorizontally { fullWidth: Int ->
					if (layoutDirection == LayoutDirection.Ltr) {
						-fullWidth * 2
					} else {
						fullWidth * 2
					}
				},
				exit = slideOutHorizontally { fullWidth: Int ->
					if (layoutDirection == LayoutDirection.Ltr) {
						-fullWidth * 2
					} else {
						fullWidth * 2
					}
				}
			) {
				Text(text = stringResource(Res.string.homepage_title_label), style = MaterialTheme.typography.h6)
			}
		}
	)
}

val COLLAPSED_TOP_BAR_HEIGHT = 56.dp
val EXPANDED_TOP_BAR_HEIGHT = 200.dp