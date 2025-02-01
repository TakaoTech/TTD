package com.takaotech.dashboard.ui.github.list

import androidx.lifecycle.ViewModel
import com.takaotech.dashboard.repository.usecase.GHTagsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class GHHomepageTagsPageViewModel(
    ghTagsUseCase: GHTagsUseCase,
) : ViewModel() {
    val tagList =
        ghTagsUseCase
            .getGhTags()
            .flowOn(Dispatchers.IO)
}
