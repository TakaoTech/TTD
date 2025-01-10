package com.takaotech.dashboard.repository.usecase

import androidx.paging.PagingData
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import com.takaotech.dashboard.model.github.TagDao
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GHTagsUseCase(
    private val ghRepositorySource: GHTagsSource,
) {
    fun getGhTags(): Flow<PagingData<TagDao>> =
        Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { ghRepositorySource },
        ).flow
}
