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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.takaotech.dashboard.model.github.GHRepositoryMiniDao
import com.takaotech.dashboard.model.github.TagDao
import com.takaotech.dashboard.ui.platform.components.TagChip
import com.takaotech.dashboard.ui.utils.NetworkResult
import org.jetbrains.compose.resources.stringResource
import ttd.composeapp.generated.resources.Res
import ttd.composeapp.generated.resources.homepage_ghrepository_more_tags_label
import ttd.composeapp.generated.resources.homepage_ghrepository_tags_label
import ttd.composeapp.generated.resources.homepage_title_label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePageScreen(
    tags: List<TagDao>,
    repositories: NetworkResult<List<GHRepositoryMiniDao>>,
    isRefreshing: Boolean,
    onTagClicked: (tagId: Int) -> Unit,
    onMoreTagClicked: () -> Unit,
    onCardClicked: (repoId: Long) -> Unit,
    onMoreRepositoriesClicked: () -> Unit,
    onRefresh: () -> Unit,
) {
    val pullToRefreshState = rememberPullToRefreshState()

    val listState = rememberLazyListState()
    val isCollapsed: Boolean by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }

    Scaffold(
        topBar = {
            CollapsedTopBar(
                modifier =
                    Modifier
                        .fillMaxWidth(),
                isCollapsed = isCollapsed,
            )
        },
    ) {
        PullToRefreshBox(
            modifier = Modifier.padding(top = it.calculateTopPadding()),
            state = pullToRefreshState,
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
        ) {
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize(),
                state = listState,
            ) {
// 				item { ExpandedTopBar() }
                if (tags.isNotEmpty()) {
                    item {
                        LazyRow(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(8.dp),
                        ) {
                            item {
                                Text(stringResource(Res.string.homepage_ghrepository_tags_label))
                            }

                            items(tags) {
                                TagChip(
                                    text = it.name,
                                    color = it.color,
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
                        // TODO Show error
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
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                    fullName = it.fullName,
                                    tags = it.tags,
                                    languages = it.languages,
                                    onTagClicked = {
                                        onTagClicked(it)
                                    },
                                    onCardClicked = {
                                        onCardClicked(it.id)
                                    },
                                )
                            }

                            if (repositories.isNotEmpty()) {
                                item {
                                    TextButton(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = onMoreRepositoriesClicked,
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
        }
    }
}

@Composable
private fun ExpandedTopBar() {
    Box(
        modifier =
            Modifier
                .background(MaterialTheme.colorScheme.primary)
                .fillMaxWidth()
                .height(EXPANDED_TOP_BAR_HEIGHT - COLLAPSED_TOP_BAR_HEIGHT),
        contentAlignment = Alignment.BottomStart,
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(Res.string.homepage_title_label),
            color = MaterialTheme.colorScheme.onPrimary,
            style =
                MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollapsedTopBar(
    modifier: Modifier = Modifier,
    isCollapsed: Boolean,
) {
    val layoutDirection = LocalLayoutDirection.current

    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.secondary),
        title = {
            AnimatedVisibility(
                visible = isCollapsed,
                enter =
                    slideInHorizontally { fullWidth: Int ->
                        if (layoutDirection == LayoutDirection.Ltr) {
                            -fullWidth * 2
                        } else {
                            fullWidth * 2
                        }
                    },
                exit =
                    slideOutHorizontally { fullWidth: Int ->
                        if (layoutDirection == LayoutDirection.Ltr) {
                            -fullWidth * 2
                        } else {
                            fullWidth * 2
                        }
                    },
            ) {
                Text(
                    text = stringResource(Res.string.homepage_title_label),
                    style =
                        MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                        ),
                )
            }
        },
    )
}

val COLLAPSED_TOP_BAR_HEIGHT = 56.dp
val EXPANDED_TOP_BAR_HEIGHT = 200.dp
