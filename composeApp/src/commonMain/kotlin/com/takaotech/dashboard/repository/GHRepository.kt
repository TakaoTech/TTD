package com.takaotech.dashboard.repository

import com.takaotech.dashboard.model.GHRepositoryDao
import com.takaotech.dashboard.model.MainCategory
import com.takaotech.dashboard.repository.api.GHApi
import org.koin.core.annotation.Single

@Single
class GHRepository(
	private val githubApi: GHApi
) {

	suspend fun getRepositories(category: MainCategory? = null): List<GHRepositoryDao> {
		return githubApi.getRepositories(category = category)
	}
}