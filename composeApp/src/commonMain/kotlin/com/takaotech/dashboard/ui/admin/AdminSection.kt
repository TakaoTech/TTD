package com.takaotech.dashboard.ui.admin

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.takaotech.dashboard.ui.admin.github.AdminGHRepositoryPage
import com.takaotech.dashboard.ui.admin.github.GHRepositoryListViewModel
import com.takaotech.dashboard.ui.admin.tags.edit.TagEditPage
import com.takaotech.dashboard.ui.admin.tags.edit.TagEditViewModel
import com.takaotech.dashboard.ui.admin.tags.list.TagListPage
import com.takaotech.dashboard.ui.admin.tags.list.TagListViewModel
import com.takaotech.dashboard.ui.admin.tags.list.TagSelectionListViewModel
import com.takaotech.dashboard.ui.admin.tags.list.TagSelectionPage
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AdminSection() {
    val navController = rememberNavController()

    NavHost(navController, "/list") {
        composable("/list") {
            val viewModel = koinViewModel<GHRepositoryListViewModel>()

            AdminGHRepositoryPage(
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel,
                onTagEditClicked = {
                    navController.navigate(AdminTagsDestination.ListSelection)
                },
                onTagListClicked = {
                    navController.navigate(AdminTagsDestination.List)
                }
            )
        }

        navigation<AdminTagsDestination.Route>(
            startDestination = AdminTagsDestination.List
        ) {
            composable<AdminTagsDestination.List> {
                val backStackEntry = remember { navController.getBackStackEntry(AdminTagsDestination.Route) }
                val viewModel = koinViewModel<TagListViewModel>(
                    viewModelStoreOwner = backStackEntry
                )

                TagListPage(
                    viewModel = viewModel,
                    onTagEditClick = { edit: Boolean, id: Int? ->
                        navController.navigate(
                            AdminTagsDestination.Edit(
                                tagId = id,
                                editMode = edit
                            )
                        )
                    }
                )
            }

            composable<AdminTagsDestination.Edit> {
                val backStackEntry = remember { navController.getBackStackEntry(AdminTagsDestination.Route) }
                val tagListViewModel = koinViewModel<TagListViewModel>(
                    viewModelStoreOwner = backStackEntry
                )
                val tagEditViewModel = koinViewModel<TagEditViewModel>()
                TagEditPage(
                    modifier = Modifier.padding(WindowInsets.systemBars.asPaddingValues()),
                    tagListViewModel = tagListViewModel,
                    tagEditViewModel = tagEditViewModel
                )
            }

            composable<AdminTagsDestination.ListSelection> { backStackEntry ->
                val viewModel = koinViewModel<GHRepositoryListViewModel>()
                val tagSelectionViewModel = koinViewModel<TagSelectionListViewModel>()

                val repositoryId = backStackEntry.toRoute<AdminTagsDestination.ListSelection>().repositoryId

                LaunchedEffect(Unit) {
                    val tags = viewModel.getAssignedTags(repositoryId)
                    tagSelectionViewModel.init(tags)
                }

                LaunchedEffect(Unit) {
                    tagSelectionViewModel.refreshRepositoryChannel.receiveAsFlow().collect {
                        if (it != null) {
                            viewModel.refreshGHRepository(repositoryId)
                            navController.navigateUp()
                        }
                    }
                }

                val uiState by tagSelectionViewModel.uiState.collectAsState()

                TagSelectionPage(
                    uiState = uiState,
                    onTagChanged = {
                        tagSelectionViewModel.changeTagSelection(it)
                    },
                    onSaveClicked = {
                        tagSelectionViewModel.updateTags(repositoryId)
                    }
                )
            }
        }
    }
}

sealed class AdminTagsDestination {
    @Serializable
    data object Route : AdminTagsDestination()

    @Serializable
    data object List : AdminTagsDestination()

    @Serializable
    data class ListSelection(val repositoryId: Long) : AdminTagsDestination()

    @Serializable
    data class Edit(val tagId: Int?, val editMode: Boolean) : AdminTagsDestination()
}
