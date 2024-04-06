package com.takaotech.dashboard.ui.github

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.isSuccess
import com.github.kittinunf.result.map
import com.takaotech.dashboard.model.github.GHLanguageDao
import com.takaotech.dashboard.model.github.GHRepositoryMiniDao
import com.takaotech.dashboard.model.github.TagDao
import com.takaotech.dashboard.repository.GHRepository
import com.takaotech.dashboard.ui.utils.NetworkResult
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
		initGHRepository()
		getTags()
	}

	private fun initGHRepository() {
		screenModelScope.launch {
			val result = getGHRepository()

			mUiState.update {
				it.copy(
					repositoryList = if (result.isSuccess()) {
						NetworkResult.Success(result.get())
					} else {
						//TODO Manage error with a message
						NetworkResult.Error("s")
					}
				)

			}
		}
	}

	fun refresh() {
		getTags()
		screenModelScope.launch {
			mUiState.update {
				it.copy(refreshing = true)
			}
			val result = getGHRepository()

			mUiState.update {
				it.copy(
					repositoryList = if (result.isSuccess()) {
						NetworkResult.Success(result.get())
					} else {
						NetworkResult.Error("s")
					},
					refreshing = false
				)
			}
		}
	}


	private suspend fun getGHRepository(): Result<List<GHRepositoryMiniDao>, Throwable> {
		return ghRepository.getRepositories(1, 10)
			.map {
				it.data.map {
					val languagesGrouped = mutableListOf<GHLanguageDao>()
					var languageGrouped = GHLanguageDao("Other", 0)

					it.languages.forEach {
						if (it.weight > 1) {
							languagesGrouped.add(it)
						} else {
							languageGrouped = languageGrouped.copy(
								weight = languageGrouped.weight + it.weight,
								lines = languageGrouped.lines + it.lines
							)
						}
					}
//						Alternative grouping
//						if (it.languages.size > 5) {
//							it.languages.forEach {
//								if (it.weight > 1) {
//									languagesGrouped.add(it)
//								} else {
//									languageGrouped.copy(
//										weight = languageGrouped.weight + it.weight,
//										lines = languageGrouped.lines + it.lines
//									)
//								}
//							}
//						} else {
//							languagesGrouped.addAll(it.languages)
//						}

					if (languageGrouped.lines > 0) {
						languagesGrouped.add(languageGrouped)
					}

					it.copy(languages = languagesGrouped.sortedByDescending { it.weight })
				}
			}
	}

	private fun getTags() {
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
	val repositoryList: NetworkResult<List<GHRepositoryMiniDao>> = NetworkResult.Loading(),
	val refreshing: Boolean = false
)