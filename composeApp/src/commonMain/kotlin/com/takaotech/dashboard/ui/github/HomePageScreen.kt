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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.takaotech.dashboard.model.github.GHRepositoryMiniDao
import com.takaotech.dashboard.model.github.TagDao
import com.takaotech.dashboard.ui.platform.components.TagChip
import com.takaotech.dashboard.ui.utils.NetworkResult
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import ttd.composeapp.generated.resources.Res
import ttd.composeapp.generated.resources.homepage_ghrepository_more_tags_label
import ttd.composeapp.generated.resources.homepage_ghrepository_tags_label
import ttd.composeapp.generated.resources.homepage_title_label


@OptIn(ExperimentalResourceApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomePageScreen(
	tags: List<TagDao>,
	repositories: NetworkResult<List<GHRepositoryMiniDao>>,
	isRefreshing: Boolean,
	onTagClicked: (tagId: Int) -> Unit,
	onMoreTagClicked: () -> Unit,
	onCardClicked: (repoId: Long) -> Unit,
	onMoreRepositoriesClicked: () -> Unit,
	onRefresh: () -> Unit
) {
	val pullToRefreshState = rememberPullToRefreshState()

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
		Box(
			modifier = Modifier
				.nestedScroll(pullToRefreshState.nestedScrollConnection)
		) {
			LazyColumn(
				modifier = Modifier
					.fillMaxSize()
					.haze(state = hazeState),
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
								TagChip(
									text = it.name,
									color = it.color
								) {
									onTagClicked(it.id)
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

				when (repositories) {
					is NetworkResult.Error -> {
						//TODO Show error
					}

					is NetworkResult.Loading -> {
						item {
							LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
						}
					}

					is NetworkResult.Success -> {
						repositories.data?.let { repositories ->
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
									TextButton(
										modifier = Modifier.fillMaxWidth(),
										onClick = onMoreRepositoriesClicked
									) {
										Text("Show More Repositories")

										Icon(Icons.AutoMirrored.Filled.ArrowForward, "")
									}
								}
							}
						}

					}
				}
			}

			if (pullToRefreshState.isRefreshing) {
				LaunchedEffect(true) {
					onRefresh()
				}
			}

			LaunchedEffect(isRefreshing) {
				if (isRefreshing) {
					pullToRefreshState.startRefresh()
				} else {
					pullToRefreshState.endRefresh()
				}
			}

			PullToRefreshContainer(
				state = pullToRefreshState,
				modifier = Modifier
					.align(Alignment.TopCenter),
			)
		}
	}
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun ExpandedTopBar() {
	Box(
		modifier = Modifier
			.background(MaterialTheme.colorScheme.primary)
			.fillMaxWidth()
			.height(EXPANDED_TOP_BAR_HEIGHT - COLLAPSED_TOP_BAR_HEIGHT),
		contentAlignment = Alignment.BottomStart
	) {
		Text(
			modifier = Modifier.padding(16.dp),
			text = stringResource(Res.string.homepage_title_label),
			color = MaterialTheme.colorScheme.onPrimary,
			style = MaterialTheme.typography.displayMedium.copy(
				fontWeight = FontWeight.Bold
			),
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
				Text(
					text = stringResource(Res.string.homepage_title_label),
					style = MaterialTheme.typography.headlineSmall.copy(
						fontWeight = FontWeight.ExtraBold
					)
				)
			}
		}
	)
}

val COLLAPSED_TOP_BAR_HEIGHT = 56.dp
val EXPANDED_TOP_BAR_HEIGHT = 200.dp