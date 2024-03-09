package com.takaotech.dashboard.ui.admin.tags.edit

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.kittinunf.result.isSuccess
import com.takaotech.dashboard.repository.GHRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
		screenModelScope.launch(Dispatchers.IO) {
			val tagResult = ghRepository.getTagById(tagId)
			if (tagResult.isSuccess()) {
				mUiState.update {
					val tag = tagResult.get()
					it.copy(
						title = tag.name.run {
							TextFieldValue(text = this, selection = TextRange(length))
						},
						description = tag.description.orEmpty().run {
							TextFieldValue(text = this, selection = TextRange(length))
						}
					)
				}
			}
		}
	}

	fun onTitleChange(value: TextFieldValue) {
		mUiState.update {
			it.copy(title = value)
		}
	}

	fun onDescriptionChange(value: TextFieldValue) {
		mUiState.update {
			it.copy(description = value)
		}
	}
}

data class TagEditUiState(
	val title: TextFieldValue = TextFieldValue(),
	val description: TextFieldValue = TextFieldValue()
)