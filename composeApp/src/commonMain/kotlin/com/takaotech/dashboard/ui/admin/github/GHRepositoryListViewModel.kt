package com.takaotech.dashboard.ui.admin.github

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.kittinunf.result.isSuccess
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
			val listResult = ghRepository.getRepositories(mainCategory = mainCategory)

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
		screenModelScope.launch {
			ghRepository.updateCategoryRepository(id, newCategory)
		}
	}
}

data class GHRepositoryListUiState(
	val mainCategoryUi: MainCategoryUi = MainCategoryUi(),
	val ghRepositoryListState: GhRepositoryListState = GhRepositoryListState.Loading,
	val mainCategorySelected: MainCategory? = null
) {

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