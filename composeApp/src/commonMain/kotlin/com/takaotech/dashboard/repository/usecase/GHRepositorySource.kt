package com.takaotech.dashboard.repository.usecase

import com.takaotech.dashboard.model.TakaoPaging
import com.takaotech.dashboard.model.github.GHRepositoryMiniDao
import com.takaotech.dashboard.repository.GHRepository
import com.takaotech.dashboard.ui.utils.BasePagingSource
import org.koin.core.annotation.Factory

@Factory
class GHRepositorySource(
	private val ghRepository: GHRepository
) : BasePagingSource<GHRepositoryMiniDao>() {

	var tagId: Int? = null

	override suspend fun fetchData(page: Int, limit: Int): TakaoPaging<GHRepositoryMiniDao> {
		return ghRepository.getRepositories(page, limit, tagId = tagId).get()
	}
}