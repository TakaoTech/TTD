package com.takaotech.dashboard.ui.github.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.kittinunf.result.isSuccess
import com.takaotech.dashboard.model.github.GHRepositoryDao
import com.takaotech.dashboard.repository.GHRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GHRepositoryDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val ghRepository: GHRepository,
) : ViewModel() {
    private val repositoryId: Long = savedStateHandle.toRoute<GHRepositoryDetail>().id
    private val mUiState = MutableStateFlow(GHRepositoryDetailUi())
    val uiState = mUiState.asStateFlow()

    init {
        getRepository()
    }

    private fun getRepository() {
        viewModelScope.launch {
            val repositoryResult = ghRepository.getRepository(repositoryId)
            mUiState.update {
                if (repositoryResult.isSuccess()) {
                    it.copy(
                        repositoryUiState = GHRepositoryDetailUi
                            .GHRepositoryDetailUiState
                            .Success(repositoryResult.get())
                    )
                } else {
                    it.copy(repositoryUiState = GHRepositoryDetailUi.GHRepositoryDetailUiState.Error)
                }
            }
        }
    }
}

data class GHRepositoryDetailUi(
    val repositoryUiState: GHRepositoryDetailUiState = GHRepositoryDetailUiState.Loading,
) {
    sealed interface GHRepositoryDetailUiState {
        data object Loading : GHRepositoryDetailUiState

        data object Error : GHRepositoryDetailUiState

        class Success(
            val repository: GHRepositoryDao,
        ) : GHRepositoryDetailUiState
    }
}
