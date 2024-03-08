package com.takaotech.dashboard.ui.admin.tags

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel

class TagListScreen : Screen {

	@Composable
	override fun Content() {
		val viewModel = getScreenModel<TagListViewModel>()
		val uiState by viewModel.uiState.collectAsState()

		TagList(
			tagListUi = uiState.tagUi
		)
	}
}