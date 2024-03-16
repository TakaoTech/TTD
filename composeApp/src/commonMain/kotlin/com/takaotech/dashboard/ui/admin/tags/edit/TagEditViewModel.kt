package com.takaotech.dashboard.ui.admin.tags.edit

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.kittinunf.result.isSuccess
import com.takaotech.dashboard.model.TagDao
import com.takaotech.dashboard.model.TagNewDao
import com.takaotech.dashboard.repository.AdminGHRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam


@Factory
class TagEditViewModel(
	@InjectedParam private val tagId: Int?,
	@InjectedParam private val editMode: Boolean,
	private val adminGhRepository: AdminGHRepository
) : ScreenModel {

	private val mExitChannel = Channel<Unit>()
	val exitChannel = mExitChannel.receiveAsFlow()

	private val mUiState = MutableStateFlow(TagEditUiState())
	val uiState = mUiState.asStateFlow()

	init {
		if (editMode) {
			if (tagId != null) {
				getTagForEdit(tagId)
			} else {
				//TODO Show Error for edit mode without ID
			}
		}
	}

	private fun getTagForEdit(tagId: Int) {
		screenModelScope.launch(Dispatchers.IO) {
			val tagResult = adminGhRepository.getTagById(tagId)
			if (tagResult.isSuccess()) {
				mUiState.update {
					val tag = tagResult.get()
					it.copy(
						name = tag.name.run {
							TextFieldValue(text = this, selection = TextRange(length))
						},
						description = tag.description.orEmpty().run {
							TextFieldValue(text = this, selection = TextRange(length))
						},
						color = tag.color.orEmpty().run {
							TextFieldValue(text = this, selection = TextRange(length))
						}
					)
				}
			}
		}
	}

	fun onTitleChange(value: TextFieldValue) {
		mUiState.update {
			it.copy(name = value)
		}
	}

	fun onDescriptionChange(value: TextFieldValue) {
		mUiState.update {
			it.copy(description = value)
		}
	}

	fun onColorChange(value: TextFieldValue) {
		mUiState.update {
			it.copy(color = value)
		}
	}

	fun saveTag() {
		screenModelScope.launch {
			val tagSaveResult = if (editMode) {
				adminGhRepository.updateTag(
					with(uiState.value) {
						TagDao(
							id = tagId!!,
							name = name.text,
							description = description.text.ifBlank { null },
							color = color.text.ifBlank { null }
						)
					}
				).isSuccess()
			} else {
				adminGhRepository.addTag(
					with(uiState.value) {
						TagNewDao(
							name = name.text,
							description = description.text.ifBlank { null },
							color = color.text.ifBlank { null }
						)
					}
				).isSuccess()

			}

			if (tagSaveResult) {
				mExitChannel.send(Unit)
			} else {
				//TODO Manage Failure
			}
		}
	}
}

data class TagEditUiState(
	val name: TextFieldValue = TextFieldValue(),
	val description: TextFieldValue = TextFieldValue(),
	val color: TextFieldValue = TextFieldValue(),
)