package com.takaotech.dashboard.ui.github.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.takaotech.dashboard.repository.usecase.GHRepositoryListUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.flowOn

class GHHomepageListPageViewModel(
    private val savedStateHandle: SavedStateHandle,
    ghRepositoryListUseCase: GHRepositoryListUseCase,
) : ViewModel() {
    val repositoryList =
        ghRepositoryListUseCase
            .getGhRepositoryList(tagId = savedStateHandle["tagId"])
            .flowOn(Dispatchers.IO)
}
