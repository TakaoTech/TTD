package com.takaotech.dashboard.repository

import co.touchlab.kermit.Logger
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.onFailure
import com.takaotech.dashboard.model.github.GHRepositoryDto
import com.takaotech.dashboard.model.github.MainCategoryDto
import com.takaotech.dashboard.model.github.TagDto
import com.takaotech.dashboard.model.github.TagNewDto
import com.takaotech.dashboard.model.github.request.TagsUpdateRequest
import com.takaotech.dashboard.repository.api.AdminGHApi
import org.koin.core.annotation.Single

@Single
class AdminGHRepository(
    private val githubApi: AdminGHApi,
    private val logger: Logger,
) {
    suspend fun refreshRepositories(): Result<Unit, Exception> =
        Result
            .of<Unit, Exception> {
                githubApi.refreshRepositories()
            }.onFailure {
                logger.e(it) { "Error refreshRepositories" }
            }

    suspend fun getStatusOfRefreshRepositories(): Result<Boolean?, Exception> =
        Result
            .of<Boolean?, Exception> {
                githubApi.refreshRepositoriesStatus().active
            }.onFailure {
                logger.e(it) { "Error getStatusOfRefreshRepositories" }
            }

    suspend fun getRepositories(mainCategory: MainCategoryDto? = null): Result<List<GHRepositoryDto>, Throwable> =
        Result
            .of<List<GHRepositoryDto>, Throwable> {
                githubApi
                    .getRepositories(
                        category = mainCategory,
                    ).data
            }.onFailure {
                logger.e(it) { "Error getRepositories" }
            }

    suspend fun getRepositoryById(repositoryId: Long): Result<GHRepositoryDto, Throwable> =
        Result
            .of<GHRepositoryDto, Throwable> {
                githubApi.getRepository(repositoryId)
            }.onFailure {
                logger.e(it) { "Error getRepositoryById" }
            }

    suspend fun updateCategoryRepository(
        id: Long,
        newCategory: MainCategoryDto,
    ) {
        githubApi.updateRepositoryCategory(
            id,
            newCategory,
        )
    }

    suspend fun getTags(): Result<List<TagDto>, Throwable> =
        Result.of<List<TagDto>, Throwable> {
            githubApi.getTags().data
        }

    suspend fun getTagById(tagId: Int): Result<TagDto, Throwable> =
        Result.of<TagDto, Throwable> {
            githubApi.getTagById(tagId)
        }

    suspend fun addTag(tag: TagNewDto): Result<Unit, Throwable> =
        Result
            .of<Unit, Throwable> {
                githubApi.addTag(tag)
            }.onFailure {
                logger.e(it) { "Error Save Tag" }
            }

    suspend fun updateTag(tag: TagDto): Result<Unit, Throwable> =
        Result
            .of<Unit, Throwable> {
                githubApi.updateTag(tag)
            }.onFailure {
                logger.e(it) { "Error Update Tag" }
            }

    suspend fun updateRepositoryTags(
        repositoryId: Long,
        newTags: List<Int>,
    ): Result<Unit, Throwable> =
        Result
            .of<Unit, Throwable> {
                githubApi.updateRepositoryTags(
                    repositoryId,
                    TagsUpdateRequest(newTags),
                )
            }.onFailure {
                logger.e(it) { "Error Update Tags for Repository $repositoryId" }
            }
}
