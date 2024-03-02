package com.takaotech.dashboard.repository

import com.takaotech.dashboard.model.GHRepositoryDao
import com.takaotech.dashboard.model.MainCategory
import com.takaotech.dashboard.repository.api.GHApi
import org.koin.core.annotation.Single

@Single
class GHRepository(
	private val githubApi: GHApi
) {

	suspend fun getRepositories(mainCategory: MainCategory? = null): List<GHRepositoryDao> {
		return githubApi.getRepositories(category = mainCategory)
	}

	suspend fun updateCategoryRepository(id: Long, newCategory: MainCategory) {
		githubApi.updateRepositoryCategory(id, newCategory)
	}
}