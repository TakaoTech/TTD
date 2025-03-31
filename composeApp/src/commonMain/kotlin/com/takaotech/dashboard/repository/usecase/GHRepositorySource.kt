package com.takaotech.dashboard.repository.usecase

import com.takaotech.dashboard.model.TakaoPaging
import com.takaotech.dashboard.model.github.GHRepositoryMiniDto
import com.takaotech.dashboard.repository.GHRepository
import com.takaotech.dashboard.ui.utils.BasePagingSource
import org.koin.core.annotation.Factory

@Factory
class GHRepositorySource(
    private val ghRepository: GHRepository,
) : BasePagingSource<GHRepositoryMiniDto>() {
    var tagId: Int? = null

    override suspend fun fetchData(
        page: Int,
        limit: Int,
    ): TakaoPaging<GHRepositoryMiniDto> = ghRepository.getRepositories(page, limit, tagId = tagId).get()
}
