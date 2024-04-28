package com.takaotech.dashboard.repository.usecase

import com.takaotech.dashboard.model.TakaoPaging
import com.takaotech.dashboard.model.github.TagDao
import com.takaotech.dashboard.repository.GHRepository
import com.takaotech.dashboard.ui.utils.BasePagingSource
import org.koin.core.annotation.Factory

@Factory
class GHTagsSource(
	private val ghRepository: GHRepository
) : BasePagingSource<TagDao>() {

	override suspend fun fetchData(page: Int, limit: Int): TakaoPaging<TagDao> {
		return ghRepository.getTags(page, limit).get()
	}
}