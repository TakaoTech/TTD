package com.takaotech.dashboard.ui.admin.github

import androidx.compose.foundation.layout.*
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.takaotech.dashboard.model.MainCategory


class GHRepositoryScreen : Screen {

	@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
	@Composable
	override fun Content() {
		val viewModel = getScreenModel<GHRepositoryListViewModel>()
		val uiState by viewModel.uiState.collectAsState()


		GHRepositoryScreen(uiState, viewModel)
	}


}

@Composable
@OptIn(ExperimentalMaterialApi::class)
internal fun GHRepositoryScreen(
	uiState: GHRepositoryListUiState,
	viewModel: GHRepositoryListViewModel
) {
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

		GHRepositoryList(
			modifier = Modifier.fillMaxWidth(),
			ghRepositoryState = uiState.ghRepositoryListState,
			onCategoryChangeClicked = { id: Long, newCategory: MainCategory ->
				viewModel.updateGHRepositoryCategory(id, newCategory)
			}
		)
	}
}