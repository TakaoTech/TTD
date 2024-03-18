package com.takaotech.dashboard.ui.github

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.kittinunf.result.isSuccess
import com.takaotech.dashboard.model.GHRepositoryMiniDao
import com.takaotech.dashboard.model.TagDao
import com.takaotech.dashboard.repository.GHRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory
class HomePageViewModel(
	private val ghRepository: GHRepository
) : ScreenModel {
	private val mUiState = MutableStateFlow(HomePageUi())
	val uiState = mUiState.asStateFlow()

	init {
		getGHRepository()
		getTags()
	}

	fun getGHRepository() {
		screenModelScope.launch(Dispatchers.IO) {
			val repositoryResult = ghRepository.getRepositories(1, 10)
			mUiState.update {
				if (repositoryResult.isSuccess()) {
					it.copy(repositoryList = repositoryResult.get())
				} else {
					it
				}
			}
		}
	}

	fun getTags() {
		screenModelScope.launch(Dispatchers.IO) {
			val tagsResult = ghRepository.getTags(1, 10)

			mUiState.update {
				if (tagsResult.isSuccess()) {
					it.copy(tags = tagsResult.get())
				} else {
					it
				}
			}

		}
	}

}

data class HomePageUi(
	val tags: List<TagDao> = listOf(),
	val repositoryList: List<GHRepositoryMiniDao> = listOf(),
)