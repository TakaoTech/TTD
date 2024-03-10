package com.takaotech.dashboard.ui.admin.github

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.takaotech.dashboard.model.MainCategory
import com.takaotech.dashboard.ui.admin.tags.list.TagListScreen
import com.takaotech.dashboard.ui.platform.LocalTTDUriHandler
import org.koin.core.component.KoinComponent


class GHRepositoryScreen : Screen, KoinComponent {

	@Composable
	override fun Content() {
		val viewModel = getScreenModel<GHRepositoryListViewModel>()
		val uriHandler = LocalTTDUriHandler.current
		val uiState by viewModel.uiState.collectAsState()


		GHRepositoryScreen(
			uiState = uiState,
			viewModel = viewModel,
			onCardClicked = {
				uriHandler.openUrl(it)
			}
		)
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
				navigator.push(TagListScreen())
			}
		) {
			Text("ListTag")
		}

		GHRepositoryList(
			modifier = Modifier.fillMaxWidth(),
			ghRepositoryState = uiState.ghRepositoryListState,
			onCardClicked = onCardClicked,
			onCategoryChangeClicked = { id: Long, newCategory: MainCategory ->
				viewModel.updateGHRepositoryCategory(id, newCategory)
			}
		)
	}
}