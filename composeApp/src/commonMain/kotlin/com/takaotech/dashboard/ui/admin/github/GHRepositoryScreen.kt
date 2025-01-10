package com.takaotech.dashboard.ui.admin.github

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.takaotech.dashboard.model.github.MainCategory
import com.takaotech.dashboard.ui.admin.tags.AdminTagsScreen
import com.takaotech.dashboard.ui.admin.tags.list.TagSelectionList
import com.takaotech.dashboard.ui.platform.LocalTTDUriHandler
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class GHRepositoryScreen :
    Screen,
    KoinComponent {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = navigator.koinNavigatorScreenModel<GHRepositoryListViewModel>()
        val uriHandler = LocalTTDUriHandler.current
        val uiState by viewModel.uiState.collectAsState()

        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            viewModel.snackbarChannel.collect {
                if (it == GHRepositoryListUiState.SnackbarType.TAG_UPDATE) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Aggiornato Repository",
                        )
                    }
                }
            }
        }

        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) {
            GHRepositoryScreen(
                uiState = uiState,
                viewModel = viewModel,
                onCardClicked = {
                    uriHandler.openUrl(it)
                },
            )
        }
    }
}

@Composable
internal fun GHRepositoryScreen(
    uiState: GHRepositoryListUiState,
    viewModel: GHRepositoryListViewModel,
    onCardClicked: (url: String) -> Unit,
) {
    val navigator = LocalNavigator.currentOrThrow
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }

    val counterRefresh by viewModel.counterForRefresh.collectAsState()

    if (openBottomSheet) {
        MainCategoryBottomSheet(
            categoryList = uiState.mainCategoryUi.categoryList,
            onDismissRequest = {
                openBottomSheet = false
            },
        ) {
            viewModel.updateFilterMainCategory(it)
        }
    }

    Column {
        Row(
            modifier = Modifier.padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box {
                AssistChip(
                    onClick = {
                        openBottomSheet = true
                    },
                    label = {
                        Text(uiState.mainCategoryUi.selectedCategory?.name ?: "--")
                    },
                )
            }

            if (counterRefresh != null) {
                var showCancelMenu by remember { mutableStateOf(false) }

                Text(
                    modifier =
                        Modifier
                            .minimumInteractiveComponentSize()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        // Azione da eseguire al long press
                                        showCancelMenu = true
                                    },
                                )
                            },
                    text = counterRefresh.toString(),
                )

                DropdownMenu(
                    expanded = showCancelMenu,
                    onDismissRequest = {
                        showCancelMenu = false
                    },
                ) {
                    DropdownMenuItem(
                        text = {
                            Text("Cancel Pull")
                        },
                        onClick = {
                        },
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        viewModel.pullGHRepositories()
                    },
                ) {
                    Icon(Icons.Filled.Refresh, "")
                }
            }
        }

        Button(
            onClick = {
                navigator.push(AdminTagsScreen())
            },
        ) {
            Text("ListTag")
        }

        AdminGHRepositoryList(
            modifier = Modifier.fillMaxWidth(),
            ghRepositoryState = uiState.ghRepositoryListState,
            onCardClicked = onCardClicked,
            onCategoryChangeClicked = { id: Long, newCategory: MainCategory ->
                viewModel.updateGHRepositoryCategory(id, newCategory)
            },
            onTagEditClicked = {
                navigator.push(TagSelectionList(it))
            },
        )
    }
}
