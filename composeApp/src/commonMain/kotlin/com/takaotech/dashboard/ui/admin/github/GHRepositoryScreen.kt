package com.takaotech.dashboard.ui.admin.github

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.takaotech.dashboard.model.github.MainCategory
import com.takaotech.dashboard.ui.admin.tags.AdminTagsScreen
import com.takaotech.dashboard.ui.admin.tags.list.TagSelectionList
import com.takaotech.dashboard.ui.platform.LocalTTDUriHandler
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent


class GHRepositoryScreen : Screen, KoinComponent {

	@Composable
	override fun Content() {
		val navigator = LocalNavigator.currentOrThrow
		val viewModel = navigator.getNavigatorScreenModel<GHRepositoryListViewModel>()
		val uriHandler = LocalTTDUriHandler.current
		val uiState by viewModel.uiState.collectAsState()

		val scope = rememberCoroutineScope()
		val snackbarHostState = remember { SnackbarHostState() }

		LaunchedEffect(Unit) {
			viewModel.snackbarChannel.collect {
				if (it == GHRepositoryListUiState.SnackbarType.TAG_UPDATE) {
					scope.launch {
						snackbarHostState.showSnackbar(
							message = "Aggiornato Repository"
						)
					}
				}
			}
		}


		Scaffold(
			snackbarHost = {
				SnackbarHost(hostState = snackbarHostState)
			}
		) {
			GHRepositoryScreen(
				uiState = uiState,
				viewModel = viewModel,
				onCardClicked = {
					uriHandler.openUrl(it)
				}
			)
		}
	}
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
internal fun GHRepositoryScreen(
	uiState: GHRepositoryListUiState,
	viewModel: GHRepositoryListViewModel,
	onCardClicked: (url: String) -> Unit
) {
	val navigator = LocalNavigator.currentOrThrow
	var openBottomSheet by rememberSaveable { mutableStateOf(false) }

	if (openBottomSheet) {
		MainCategoryBottomSheet(categoryList = uiState.mainCategoryUi.categoryList,
			onDismissRequest = {
				openBottomSheet = false
			}
		) {
			viewModel.updateFilterMainCategory(it)
		}
	}

	Column {
		Row(modifier = Modifier.padding(4.dp)) {
			Box {
				Chip(
					onClick = {
						openBottomSheet = true
					}
				) {
					Text(uiState.mainCategoryUi.selectedCategory?.name ?: "--")
				}
			}
		}

		Button(
			onClick = {
				navigator.push(AdminTagsScreen())
			}
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
			}
		)
	}
}