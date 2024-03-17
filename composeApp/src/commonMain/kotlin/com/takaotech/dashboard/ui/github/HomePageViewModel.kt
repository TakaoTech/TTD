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
					val repositories = repositoryResult.get()
					val languageWeightMap = mutableMapOf<Long, List<Triple<String, Long, Float>>>()
					repositories.forEach {
						val totalLines = it.languages.values.sumOf { it }.toFloat()

						val languageWeight = it.languages.map {
							val weight = (it.value * 100) / totalLines

							Triple(it.key, it.value, weight)
						}
						languageWeightMap.put(it.id, languageWeight)
					}

					it.copy(repositoryList = repositoryResult.get(), languangeMappingPer = languageWeightMap)
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
	val languangeMappingPer: Map<Long, List<Triple<String, Long, Float>>> = mapOf()
)