package com.takaotech.dashboard.ui.admin.tags.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kittinunf.result.isSuccess
import com.takaotech.dashboard.model.github.TagDao
import com.takaotech.dashboard.repository.AdminGHRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TagListViewModel(
    private val adminGhRepository: AdminGHRepository,
) : ViewModel() {
    private val mUiState = MutableStateFlow(TagListUiState())
    val uiState = mUiState.asStateFlow()

    init {
        refreshTagList()
    }

    fun refreshTagList() {
        viewModelScope.launch(Dispatchers.IO) {
            val tagListResult = adminGhRepository.getTags()

            mUiState.update {
                if (tagListResult.isSuccess()) {
                    it.copy(tagUi = TagListUiState.TagListUi.Success(tagListResult.get()))
                } else {
                    it.copy(tagUi = TagListUiState.TagListUi.Error)
                }
            }
        }
    }
}

data class TagListUiState(
    val tagUi: TagListUi = TagListUi.Loading,
) {
    sealed interface TagListUi {
        data class Success(
            val tagList: List<TagDao> = listOf(),
        ) : TagListUi

        data object Error : TagListUi

        data object Loading : TagListUi
    }
}
