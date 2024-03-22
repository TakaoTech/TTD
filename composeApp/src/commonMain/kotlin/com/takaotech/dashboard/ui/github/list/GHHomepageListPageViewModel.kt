package com.takaotech.dashboard.ui.github.list

import cafe.adriel.voyager.core.model.ScreenModel
import com.takaotech.dashboard.repository.usecase.GHRepositoryListUseCase
import org.koin.core.annotation.Factory

@Factory
class GHHomepageListPageViewModel(
	private val ghRepositoryListUseCase: GHRepositoryListUseCase
) : ScreenModel {

	val repositoryList = ghRepositoryListUseCase.getGhRepositoryList()
}