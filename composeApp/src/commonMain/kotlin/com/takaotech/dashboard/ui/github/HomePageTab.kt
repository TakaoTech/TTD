package com.takaotech.dashboard.ui.github

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeTab(
    viewModel: HomePageViewModel = koinViewModel(),
    onTagClicked: (tagId: Int) -> Unit,
    onMoreTagClicked: () -> Unit,
    onRepositoryClicked: (repositoryId: Long) -> Unit,
    onMoreRepositoriesClicked: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    HomePageScreen(
        tags = uiState.tags,
        repositories = uiState.repositoryList,
        isRefreshing = uiState.refreshing,
        onTagClicked = onTagClicked,
        onMoreTagClicked = onMoreTagClicked,
        onMoreRepositoriesClicked = onMoreRepositoriesClicked,
        onCardClicked = {
            onRepositoryClicked(it)
        },
        onRefresh = {
            viewModel.refresh()
        },
    )
}
