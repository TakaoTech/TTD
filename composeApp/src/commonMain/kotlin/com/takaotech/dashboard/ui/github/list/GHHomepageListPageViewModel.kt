package com.takaotech.dashboard.ui.github.list

import cafe.adriel.voyager.core.model.ScreenModel
import com.takaotech.dashboard.repository.usecase.GHRepositoryListUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.flowOn
import org.koin.core.annotation.Factory

@Factory
class GHHomepageListPageViewModel(
	private val ghRepositoryListUseCase: GHRepositoryListUseCase
) : ScreenModel {

	val repositoryList = ghRepositoryListUseCase.getGhRepositoryList()
		.flowOn(Dispatchers.IO)
}