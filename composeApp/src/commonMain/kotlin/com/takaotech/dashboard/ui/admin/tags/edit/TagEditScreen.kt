package com.takaotech.dashboard.ui.admin.tags.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.core.parameter.parametersOf


data class TagEditScreen(val tagId: String) : Screen {
	@Composable

	override fun Content() {
		val navigator = LocalNavigator.currentOrThrow
		val viewModel = getScreenModel<TagEditViewModel>(
			parameters = { parametersOf(tagId) }
		)

		LaunchedEffect(Unit) {
			viewModel.exitChannel.collect {
				navigator.pop()
			}
		}

		val uiState by viewModel.uiState.collectAsState()

		TagEdit(
			titleTag = uiState.title,
			descriptionTag = uiState.description,
			onTitleTagChanged = viewModel::onTitleChange,
			onDescriptionTagChanged = viewModel::onDescriptionChange
		) {
			viewModel.saveTag()
		}
	}
}