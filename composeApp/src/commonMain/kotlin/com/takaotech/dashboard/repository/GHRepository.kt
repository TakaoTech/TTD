package com.takaotech.dashboard.repository

import com.github.kittinunf.result.Result
import com.takaotech.dashboard.model.GHRepositoryDao
import com.takaotech.dashboard.model.MainCategory
import com.takaotech.dashboard.model.Tag
import com.takaotech.dashboard.repository.api.GHApi
import org.koin.core.annotation.Single

@Single
class GHRepository(
	private val githubApi: GHApi
) {

	suspend fun getRepositories(mainCategory: MainCategory? = null): Result<List<GHRepositoryDao>, Throwable> {
		return Result.of<List<GHRepositoryDao>, Throwable> {
			githubApi.getRepositories(category = mainCategory)
		}
	}

	suspend fun updateCategoryRepository(id: Long, newCategory: MainCategory) {
		githubApi.updateRepositoryCategory(id, newCategory)
	}

	suspend fun getTags(): Result<List<Tag>, Throwable> {
		return Result.of<List<Tag>, Throwable> {
			githubApi.getTags()
		}
	}
}