package com.takaotech.dashboard.ui.github

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import com.takaotech.dashboard.model.GHRepositoryDao
import com.takaotech.dashboard.model.TagDao
import com.takaotech.dashboard.ui.admin.github.GHRepositoryCard
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
	repositories: List<GHRepositoryDao>,
	onTagClicked: (tagId: Int) -> Unit,
	onMoreTagClicked: () -> Unit
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
			item {
				LazyRow(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(4.dp),
					contentPadding = PaddingValues(4.dp)
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

			items(repositories) {
				//TODO Change this, use shrinked card
				GHRepositoryCard(
					modifier = Modifier.fillMaxWidth(),
					fullName = it.fullName,
					mainCategory = it.mainCategory,
					tags = it.tags,
					onTagsEditClicked = {},
					onCardClicked = {}
				)
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
	TopAppBar(
		modifier = modifier,
		colors = TopAppBarDefaults.largeTopAppBarColors(Color.Transparent),
		title = {
			AnimatedVisibility(
				visible = isCollapsed,
				enter = slideInHorizontally { fullWidth: Int ->
					-fullWidth * 2
				},
				exit = slideOutHorizontally { fullWidth: Int ->
					-fullWidth * 2
				}
			) {
				Text(text = stringResource(Res.string.homepage_title_label), style = MaterialTheme.typography.h6)
			}
		}
	)
}

val COLLAPSED_TOP_BAR_HEIGHT = 56.dp
val EXPANDED_TOP_BAR_HEIGHT = 200.dp