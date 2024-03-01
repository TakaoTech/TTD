package com.takaotech.dashboard.ui.admin.github

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.takaotech.dashboard.model.GHRepositoryDao
import com.takaotech.dashboard.model.MainCategory
import com.takaotech.dashboard.repository.GHRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory
class GHRepositoryListViewModel(
	private val ghRepository: GHRepository
) : ScreenModel {
	private val mUiState = MutableStateFlow(GHRepositoryListUiState())
	val uiState = mUiState.asStateFlow()

	init {
		screenModelScope.launch(Dispatchers.IO) {
			val list = ghRepository.getRepositories()

			mUiState.update {
				it.copy(ghRepositoryData = list)
			}

		}
	}
}

data class GHRepositoryListUiState(
	val ghRepositoryData: List<GHRepositoryDao> = listOf(),
	val mainCategorySelected: MainCategory? = null
)