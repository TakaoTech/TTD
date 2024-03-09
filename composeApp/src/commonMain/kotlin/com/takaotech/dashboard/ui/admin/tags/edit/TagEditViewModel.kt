package com.takaotech.dashboard.ui.admin.tags.edit

import androidx.compose.ui.text.input.TextFieldValue
import cafe.adriel.voyager.core.model.ScreenModel
import com.takaotech.dashboard.repository.GHRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
class TagEditViewModel(
	@InjectedParam private val tagId: String,
	private val ghRepository: GHRepository
) : ScreenModel {
	private val mUiState = MutableStateFlow(TagEditUiState())
	val uiState = mUiState.asStateFlow()

	init {
		val abnc = tagId
	}
}

data class TagEditUiState(
	val title: TextFieldValue = TextFieldValue(),
	val description: TextFieldValue = TextFieldValue()
)