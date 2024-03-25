package com.takaotech.dashboard.repository.usecase

import androidx.paging.PagingData
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import com.takaotech.dashboard.model.github.GHRepositoryMiniDao
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GHRepositoryListUseCase(
	private val ghRepositorySource: GHRepositorySource,
) {
	fun getGhRepositoryList(tagId: Int? = null): Flow<PagingData<GHRepositoryMiniDao>> {
		ghRepositorySource.tagId = tagId
		return Pager(
			config = PagingConfig(pageSize = 10),
			pagingSourceFactory = { ghRepositorySource }
		).flow
	}
}