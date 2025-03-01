package com.takaotech.dashboard.repository

import co.touchlab.kermit.Logger
import com.github.kittinunf.result.Result
import com.takaotech.dashboard.model.TakaoPaging
import com.takaotech.dashboard.model.github.GHRepositoryDto
import com.takaotech.dashboard.model.github.GHRepositoryMiniDto
import com.takaotech.dashboard.model.github.TagDto
import com.takaotech.dashboard.repository.api.GHApi
import org.koin.core.annotation.Single

@Single
class GHRepository(
    private val githubApi: GHApi,
    private val logger: Logger,
) {
    suspend fun getRepositories(
        page: Int,
        size: Int,
        tagId: Int? = null,
    ): Result<TakaoPaging<GHRepositoryMiniDto>, Throwable> =
        Result.of<TakaoPaging<GHRepositoryMiniDto>, Throwable> {
            githubApi.getRepositories(
                page = page,
                size = size,
                tagId = tagId,
            )
        }

    suspend fun getRepository(id: Long): Result<GHRepositoryDto, Throwable> =
        Result.of<GHRepositoryDto, Throwable> {
            githubApi.getRepository(id)
        }

    suspend fun getTags(
        page: Int?,
        size: Int?,
    ): Result<TakaoPaging<TagDto>, Throwable> =
        Result.of<TakaoPaging<TagDto>, Throwable> {
            githubApi.getTags(
                page = page,
                size = size,
            )
        }
}
