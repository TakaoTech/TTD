package com.takaotech.dashboard.ui.admin.github

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.kittinunf.result.isSuccess
import com.github.kittinunf.result.onFailure
import com.github.kittinunf.result.onSuccess
import com.takaotech.dashboard.model.github.GHRepositoryDao
import com.takaotech.dashboard.model.github.MainCategory
import com.takaotech.dashboard.model.github.TagDao
import com.takaotech.dashboard.repository.AdminGHRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory
class GHRepositoryListViewModel(
	private val adminGhRepository: AdminGHRepository
) : ScreenModel {
	private val mUiState = MutableStateFlow(GHRepositoryListUiState())
	val uiState = mUiState.asStateFlow()

	//TODO Channel for send show snackbar refresh

	private val mSnackbarChannel = Channel<GHRepositoryListUiState.SnackbarType>()
	val snackbarChannel = mSnackbarChannel.receiveAsFlow()
	init {
		getRepositoryList()
	}

	fun updateFilterMainCategory(mainCategory: MainCategory?) {
		screenModelScope.launch {
			mUiState.update {
				it.copy(
					mainCategoryUi = with(it.mainCategoryUi) {
						copy(
							selectedCategory = mainCategory
						)
					}
				)
			}

			getRepositoryList()
		}
	}

	private fun getRepositoryList() {
		screenModelScope.launch(Dispatchers.IO) {
			mUiState.update {
				it.copy(ghRepositoryListState = GHRepositoryListUiState.GhRepositoryListState.Loading)
			}

			val mainCategory = uiState.value.mainCategoryUi.selectedCategory
			val listResult = adminGhRepository.getRepositories(mainCategory = mainCategory)

			mUiState.update {
				if (listResult.isSuccess()) {
					it.copy(ghRepositoryListState = GHRepositoryListUiState.GhRepositoryListState.Success(listResult.get()))
				} else {
					it.copy(ghRepositoryListState = GHRepositoryListUiState.GhRepositoryListState.Error)
				}
			}

		}
	}

	fun updateGHRepositoryCategory(id: Long, newCategory: MainCategory) {
		screenModelScope.launch(Dispatchers.IO) {
			adminGhRepository.updateCategoryRepository(id, newCategory)
		}
	}

	fun getAssignedTags(repositoryId: Long): List<TagDao> {
		return (uiState.value.ghRepositoryListState as GHRepositoryListUiState.GhRepositoryListState.Success)
			.ghRepositoryData
			.find { it.id == repositoryId }
			?.tags ?: listOf()
	}

	fun refreshGHRepository(repositoryId: Long) {
		screenModelScope.launch {
			adminGhRepository.getRepositoryById(repositoryId)
				.onSuccess { repositoryUpdated ->
					mUiState.update {
						with(it.ghRepositoryListState as GHRepositoryListUiState.GhRepositoryListState.Success) {
							ghRepositoryData.map { repo ->
								if (repo.id == repositoryUpdated.id) {
									repositoryUpdated
								} else {
									repo
								}
							}
						}.let {
							GHRepositoryListUiState.GhRepositoryListState.Success(it)
						}.let { ghListState ->
							it.copy(
								ghRepositoryListState = ghListState
							)
						}
					}

					mSnackbarChannel.send(GHRepositoryListUiState.SnackbarType.TAG_UPDATE)
				}.onFailure {
					//TODO Show error snackbar
				}
		}
	}
}

data class GHRepositoryListUiState(
	val mainCategoryUi: MainCategoryUi = MainCategoryUi(),
	val ghRepositoryListState: GhRepositoryListState = GhRepositoryListState.Loading,
	val mainCategorySelected: MainCategory? = null
) {
	enum class SnackbarType {
		TAG_UPDATE
	}

	data class MainCategoryUi(
		val categoryList: List<MainCategory?> = MainCategory.entries
			.toMutableList().let {
				it as MutableList<MainCategory?>
			}.let {
				it.add(0, null)
				it
			},
		val selectedCategory: MainCategory? = null
	)

	sealed interface GhRepositoryListState {
		data class Success(
			val ghRepositoryData: List<GHRepositoryDao> = listOf()
		) : GhRepositoryListState

		data object Error : GhRepositoryListState
		data object Loading : GhRepositoryListState
	}
}