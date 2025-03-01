package com.takaotech.dashboard.ui.admin.github

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.takaotech.dashboard.model.github.MainCategoryDto
import com.takaotech.dashboard.ui.platform.LocalTTDUriHandler
import kotlinx.coroutines.launch

@Composable
fun AdminGHRepositoryPage(
    modifier: Modifier = Modifier,
    viewModel: GHRepositoryListViewModel,
    onTagEditClicked: (repoId: Long) -> Unit,
    onTagListClicked: () -> Unit,
) {
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
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) {
        GHRepositoryScreen(
            modifier = Modifier.padding(top = it.calculateTopPadding()),
            uiState = uiState,
            viewModel = viewModel,
            onTagListClicked = onTagListClicked,
            onTagEditClicked = onTagEditClicked,
            onCardClicked = {
                uriHandler.openUrl(it)
            },
        )
    }
}

@Composable
internal fun GHRepositoryScreen(
    uiState: GHRepositoryListUiState,
    viewModel: GHRepositoryListViewModel,
    modifier: Modifier = Modifier,
    onTagListClicked: () -> Unit,
    onTagEditClicked: (repoId: Long) -> Unit,
    onCardClicked: (url: String) -> Unit,
) {
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

    Column(
        modifier = modifier
    ) {
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
                    modifier = Modifier
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
            onClick = onTagListClicked,
        ) {
            Text("ListTag")
        }

        AdminGHRepositoryList(
            modifier = Modifier.fillMaxWidth(),
            ghRepositoryState = uiState.ghRepositoryListState,
            onCardClicked = onCardClicked,
            onCategoryChangeClicked = { id: Long, newCategory: MainCategoryDto ->
                viewModel.updateGHRepositoryCategory(id, newCategory)
            },
            onTagEditClicked = onTagEditClicked,
        )
    }
}
