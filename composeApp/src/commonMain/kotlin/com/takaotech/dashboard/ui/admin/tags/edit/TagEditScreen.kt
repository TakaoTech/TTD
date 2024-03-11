package com.takaotech.dashboard.ui.admin.tags.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getNavigatorScreenModel
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.takaotech.dashboard.ui.admin.tags.list.TagListViewModel
import org.koin.core.parameter.parametersOf


data class TagEditScreen(
	private val tagId: Int? = null,
	private val editMode: Boolean
) : Screen {
	@Composable
	override fun Content() {
		val navigator = LocalNavigator.currentOrThrow
		val tagListViewModel = navigator.getNavigatorScreenModel<TagListViewModel>()

		val viewModel = getScreenModel<TagEditViewModel>(
			parameters = { parametersOf(tagId, editMode) }
		)

		LaunchedEffect(Unit) {
			viewModel.exitChannel.collect {
				tagListViewModel.refreshTagList()
				navigator.pop()
			}
		}

		val uiState by viewModel.uiState.collectAsState()

		TagEdit(
			titleTag = uiState.name,
			descriptionTag = uiState.description,
			colorTag = uiState.color,
			onTitleTagChanged = viewModel::onTitleChange,
			onDescriptionTagChanged = viewModel::onDescriptionChange,
			onColorTagChanged = viewModel::onColorChange
		) {
			viewModel.saveTag()
		}
	}
}