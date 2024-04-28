package com.takaotech.dashboard.ui.github.list

import cafe.adriel.voyager.core.model.ScreenModel
import com.takaotech.dashboard.repository.usecase.GHTagsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.flowOn
import org.koin.core.annotation.Factory

@Factory
class GHHomepageTagsPageViewModel(
	ghTagsUseCase: GHTagsUseCase,
) : ScreenModel {

	val tagList = ghTagsUseCase.getGhTags()
		.flowOn(Dispatchers.IO)
}