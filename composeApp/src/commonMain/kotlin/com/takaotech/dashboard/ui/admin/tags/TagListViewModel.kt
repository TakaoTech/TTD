package com.takaotech.dashboard.ui.admin.tags

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.takaotech.dashboard.model.Tag
import com.takaotech.dashboard.repository.GHRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory
class TagListViewModel(
	private val ghRepository: GHRepository
) : ScreenModel {
	private val mUiState = MutableStateFlow(TagListUiState())
	val uiState = mUiState.asStateFlow()

	init {
		screenModelScope.launch(Dispatchers.IO) {
			val tagListUi = try {
				TagListUiState.TagListUi.Success(ghRepository.getTags())
			} catch (ex: Exception) {
				TagListUiState.TagListUi.Error
			}

			mUiState.update {
				it.copy(tagUi = tagListUi)
			}
		}
	}
}

data class TagListUiState(val tagUi: TagListUi = TagListUi.Loading) {
	sealed interface TagListUi {
		data class Success(
			val tagList: List<Tag> = listOf()
		) : TagListUi

		data object Error : TagListUi
		data object Loading : TagListUi
	}
}