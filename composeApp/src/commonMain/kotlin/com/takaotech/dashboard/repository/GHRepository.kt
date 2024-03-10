package com.takaotech.dashboard.repository

import co.touchlab.kermit.Logger
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.onFailure
import com.takaotech.dashboard.model.GHRepositoryDao
import com.takaotech.dashboard.model.MainCategory
import com.takaotech.dashboard.model.TagDao
import com.takaotech.dashboard.model.TagNewDao
import com.takaotech.dashboard.repository.api.GHApi
import org.koin.core.annotation.Single

@Single
class GHRepository(
	private val githubApi: GHApi,
	private val logger: Logger
) {

	suspend fun getRepositories(mainCategory: MainCategory? = null): Result<List<GHRepositoryDao>, Throwable> {
		return Result.of<List<GHRepositoryDao>, Throwable> {
			githubApi.getRepositories(category = mainCategory)
		}
	}

	suspend fun updateCategoryRepository(id: Long, newCategory: MainCategory) {
		githubApi.updateRepositoryCategory(id, newCategory)
	}

	suspend fun getTags(): Result<List<TagDao>, Throwable> {
		return Result.of<List<TagDao>, Throwable> {
			githubApi.getTags()
		}
	}

	suspend fun getTagById(tagId: Int): Result<TagDao, Throwable> {
		return Result.of<TagDao, Throwable> {
			githubApi.getTagById(tagId)
		}
	}

	suspend fun addTag(tag: TagNewDao): Result<Unit, Throwable> {
		return Result.of<Unit, Throwable> {
			githubApi.addTag(tag)
		}.onFailure {
			logger.e(it) { "Error Save Tag" }
		}
	}

	suspend fun updateTag(tag: TagDao): Result<Unit, Throwable> {
		return Result.of<Unit, Throwable> {
			githubApi.updateTag(tag)
		}.onFailure {
			logger.e(it) { "Error Update Tag" }
		}
	}
}