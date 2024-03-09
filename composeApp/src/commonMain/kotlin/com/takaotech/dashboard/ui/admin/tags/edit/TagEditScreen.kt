package com.takaotech.dashboard.ui.admin.tags.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import org.koin.core.parameter.parametersOf


data class TagEditScreen(val tagId: String) : Screen {
	@Composable

	override fun Content() {
		val viewModel = getScreenModel<TagEditViewModel>(
			parameters = { parametersOf(tagId) }
		)
		val uiState by viewModel.uiState.collectAsState()

		TagEdit(
			titleTag = uiState.title,
			descriptionTag = uiState.description,
			onTitleTagChanged = {

			},
			onDescriptionTagChanged = {

			}
		)
	}
}