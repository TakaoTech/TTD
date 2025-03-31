package com.takaotech.dashboard.ui.admin.tags.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kittinunf.result.isSuccess
import com.github.kittinunf.result.onFailure
import com.github.kittinunf.result.onSuccess
import com.takaotech.dashboard.model.github.TagDto
import com.takaotech.dashboard.repository.AdminGHRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TagSelectionListViewModel(
    private val adminGhRepository: AdminGHRepository,
) : ViewModel() {
    private val mUiState = MutableStateFlow(TagSelectionListUi())
    val uiState = mUiState.asStateFlow()

    val refreshRepositoryChannel = Channel<Unit?>()

    fun init(tagListSelected: List<TagDto>) {
        viewModelScope.launch(Dispatchers.IO) {
            // TODO Loading init
            adminGhRepository
                .getTags()
                .onSuccess { availableTags ->
                    val tagMap =
                        availableTags.map { tag ->
                            TagSelectionListUi.TagUi(
                                id = tag.id,
                                name = tag.name,
                                selected = tagListSelected.find { it.id == tag.id } != null,
                                color = tag.color,
                            )
                        }

                    mUiState.update {
                        it.copy(tagList = tagMap)
                    }
                }.onFailure {
                }
        }
    }

    fun changeTagSelection(
        id: Int,
    ) {
        viewModelScope.launch {
            mUiState.update {
                it.copy(
                    tagList = it.tagList.map { tag ->
                        if (tag.id == id) {
                            tag.copy(selected = !tag.selected)
                        } else {
                            tag
                        }
                    },
                )
            }
        }
    }

    fun updateTags(repositoryId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val newTags =
                mUiState.value.tagList
                    .filter { it.selected }
                    .map { it.id }

            val updateResult = adminGhRepository.updateRepositoryTags(repositoryId, newTags)
            if (updateResult.isSuccess()) {
                refreshRepositoryChannel.send(Unit)
            } else {
                // TODO Show Error
            }
        }
    }
}

data class TagSelectionListUi(
    val tagList: List<TagUi> = listOf(),
) {
    data class TagUi(
        val id: Int,
        val name: String,
        val selected: Boolean = false,
        val color: String? = null,
    )
}
