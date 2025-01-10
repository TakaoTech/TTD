package com.takaotech.dashboard.ui.github

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.koin.koinNavigatorScreenModel
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.takaotech.dashboard.ui.github.detail.GHRepositoryDetail
import com.takaotech.dashboard.ui.github.list.GHHomepageListPage
import com.takaotech.dashboard.ui.github.list.GHTagsPage

object HomePageTab : Tab {
    @Composable
    override fun Content() {
        // https://proandroiddev.com/collapsing-toolbar-in-jetpack-compose-lazycolumn-3-approaches-702684d61843
        val parent = LocalNavigator.currentOrThrow.parent
        val viewModel: HomePageViewModel = parent?.koinNavigatorScreenModel<HomePageViewModel>() ?: koinScreenModel()

        val uiState by viewModel.uiState.collectAsState()

        HomePageScreen(
            tags = uiState.tags,
            repositories = uiState.repositoryList,
            isRefreshing = uiState.refreshing,
            onTagClicked = {
                parent?.push(GHHomepageListPage(tagId = it))
            },
            onMoreTagClicked = {
                parent?.push(GHTagsPage())
            },
            onMoreRepositoriesClicked = {
                parent?.push(GHHomepageListPage())
            },
            onCardClicked = {
                parent?.push(GHRepositoryDetail(it))
            },
            onRefresh = {
                viewModel.refresh()
            },
        )
    }

    override val options: TabOptions
        @Composable
        get() {
            val title = "Home"
            val icon = rememberVectorPainter(Icons.Filled.Home)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon,
                )
            }
        }
}
