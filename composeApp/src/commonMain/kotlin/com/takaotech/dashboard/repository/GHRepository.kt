package com.takaotech.dashboard.repository

import co.touchlab.kermit.Logger
import com.github.kittinunf.result.Result
import com.takaotech.dashboard.model.TakaoPaging
import com.takaotech.dashboard.model.github.GHRepositoryMiniDao
import com.takaotech.dashboard.model.github.TagDao
import com.takaotech.dashboard.repository.api.GHApi
import org.koin.core.annotation.Single

@Single
class GHRepository(
	private val githubApi: GHApi,
	private val logger: Logger
) {

	suspend fun getRepositories(
		page: Int,
		size: Int
	): Result<TakaoPaging<GHRepositoryMiniDao>, Throwable> {
		return Result.of<TakaoPaging<GHRepositoryMiniDao>, Throwable> {
			githubApi.getRepositories(
				page = page,
				size = size
			)
		}
	}

	suspend fun getTags(
		page: Int?,
		size: Int?
	): Result<List<TagDao>, Throwable> {
		return Result.of<List<TagDao>, Throwable> {
			githubApi.getTags(
				page = page,
				size = size
			)
		}
	}

}