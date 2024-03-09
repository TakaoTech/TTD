package com.takaotech.dashboard.ui.admin.tags.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.takaotech.dashboard.ui.admin.tags.edit.TagEditScreen

class TagListScreen : Screen {

	@Composable
	override fun Content() {
		val navigator = LocalNavigator.currentOrThrow
		val viewModel = getScreenModel<TagListViewModel>()
		val uiState by viewModel.uiState.collectAsState()

		TagList(
			tagListUi = uiState.tagUi,
			onTagClicked = {
				navigator.push(TagEditScreen(it))
			}
		)
	}
}