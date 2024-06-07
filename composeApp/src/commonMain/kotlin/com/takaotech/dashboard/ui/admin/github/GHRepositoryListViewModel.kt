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
import com.takaotech.dashboard.ui.utils.tickerCounterFlow
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import org.koin.core.annotation.Factory
import kotlin.time.Duration.Companion.seconds

@Factory
class GHRepositoryListViewModel(
    private val adminGhRepository: AdminGHRepository
) : ScreenModel {
    private val mUiState = MutableStateFlow(GHRepositoryListUiState())
    val uiState = mUiState.asStateFlow()

    //TODO Channel for send show snackbar refresh

    private val mSnackbarChannel = Channel<GHRepositoryListUiState.SnackbarType>()
    val snackbarChannel = mSnackbarChannel.receiveAsFlow()

    private val tickerCoroutineScope = CoroutineScope(screenModelScope.coroutineContext)
    private val mCounterForRefresh = MutableStateFlow<Long?>(null)
    val counterForRefresh = mCounterForRefresh.asStateFlow()

    private var mTickerRefresh: Flow<Long>? = null


    init {
        getRepositoryList()
        startCheckRepositoryRefresh()
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

    fun pullGHRepositories() {
        screenModelScope.launch {
            adminGhRepository.refreshRepositories()
                .onSuccess {
                    startCheckRepositoryRefresh()
                }
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

    private fun startCheckRepositoryRefresh() {
        screenModelScope.launch(Dispatchers.IO) {
            val statusResult = adminGhRepository.getStatusOfRefreshRepositories()
            statusResult.onSuccess {
                if (it != null && it) {
                    startLoopCheck()
                }
            }
        }
    }

    private fun startLoopCheck() {
        tickerCoroutineScope.launch {
            mTickerRefresh = tickerCounterFlow(10.seconds)
                .onCompletion {
                    if (it is CancellationException) {
                        mTickerRefresh = null
                    } else {
                        val statusResult = adminGhRepository.getStatusOfRefreshRepositories()
                        statusResult.onSuccess {
                            if (it != null && it) {
                                startLoopCheck()
                            }else{
                                mCounterForRefresh.emit(null)
                            }
                        }.onFailure {
                            //TODO need understand for restart another time
                            // remember, call refresh with active refresh respond HttpStatusCode.Conflict
                        }
                    }
                }
            mTickerRefresh!!.collect {
                mCounterForRefresh.emit(it)
            }
        }
    }

    override fun onDispose() {
        tickerCoroutineScope.cancel()
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