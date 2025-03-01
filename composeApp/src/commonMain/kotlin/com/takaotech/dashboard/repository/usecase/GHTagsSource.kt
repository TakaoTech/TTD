package com.takaotech.dashboard.repository.usecase

import com.takaotech.dashboard.model.TakaoPaging
import com.takaotech.dashboard.model.github.TagDto
import com.takaotech.dashboard.repository.GHRepository
import com.takaotech.dashboard.ui.utils.BasePagingSource
import org.koin.core.annotation.Factory

@Factory
class GHTagsSource(
    private val ghRepository: GHRepository,
) : BasePagingSource<TagDto>() {
    override suspend fun fetchData(
        page: Int,
        limit: Int,
    ): TakaoPaging<TagDto> = ghRepository.getTags(page, limit).get()
}
