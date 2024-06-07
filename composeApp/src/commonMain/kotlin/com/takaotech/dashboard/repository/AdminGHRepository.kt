package com.takaotech.dashboard.repository

import co.touchlab.kermit.Logger
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.onFailure
import com.takaotech.dashboard.model.github.GHRepositoryDao
import com.takaotech.dashboard.model.github.MainCategory
import com.takaotech.dashboard.model.github.TagDao
import com.takaotech.dashboard.model.github.TagNewDao
import com.takaotech.dashboard.model.github.request.TagsUpdateRequest
import com.takaotech.dashboard.repository.api.AdminGHApi
import com.takaotech.dashboard.ui.login.SessionManager
import io.ktor.client.request.*
import org.koin.core.annotation.Single

//TODO Need pass token to every request
@Single
class AdminGHRepository(
    private val sessionManager: SessionManager,
    private val githubApi: AdminGHApi,
    private val logger: Logger
) {

    suspend fun refreshRepositories(): Result<Unit, Exception> {
        return Result.of<Unit, Exception> {
            githubApi.refreshRepositories(
                ext = {
                    sessionManager.sessionFlow.value?.accessToken?.let { bearerAuth(it) }
                }
            )
        }.onFailure {
            logger.e(it) { "Error refreshRepositories" }
        }
    }

    suspend fun getStatusOfRefreshRepositories(): Result<Boolean?, Exception> {
        return Result.of<Boolean?, Exception> {
            githubApi.refreshRepositoriesStatus(
                ext = {
                    sessionManager.sessionFlow.value?.accessToken?.let { bearerAuth(it) }
                }
            ).active
        }.onFailure {
            logger.e(it) { "Error getStatusOfRefreshRepositories" }
        }
    }

    suspend fun getRepositories(mainCategory: MainCategory? = null): Result<List<GHRepositoryDao>, Throwable> {
        return Result.of<List<GHRepositoryDao>, Throwable> {
            githubApi.getRepositories(
                category = mainCategory,
                ext = {
                    sessionManager.sessionFlow.value?.accessToken?.let { bearerAuth(it) }
                }
            ).data
        }.onFailure {
            logger.e(it) { "Error getRepositories" }
        }
    }

    suspend fun getRepositoryById(repositoryId: Long): Result<GHRepositoryDao, Throwable> {
        return Result.of<GHRepositoryDao, Throwable> {
            githubApi.getRepository(repositoryId)
        }.onFailure {
            logger.e(it) { "Error getRepositoryById" }
        }
    }

    suspend fun updateCategoryRepository(id: Long, newCategory: MainCategory) {
        githubApi.updateRepositoryCategory(
            id,
            newCategory,
            ext = {
                sessionManager.sessionFlow.value?.accessToken?.let { bearerAuth(it) }
            }
        )
    }

    suspend fun getTags(): Result<List<TagDao>, Throwable> {
        return Result.of<List<TagDao>, Throwable> {
            githubApi.getTags(
                ext = {
                    sessionManager.sessionFlow.value?.accessToken?.let { bearerAuth(it) }
                }
            )
        }
    }

    suspend fun getTagById(tagId: Int): Result<TagDao, Throwable> {
        return Result.of<TagDao, Throwable> {
            githubApi.getTagById(
                tagId,
                ext = {
                    sessionManager.sessionFlow.value?.accessToken?.let { bearerAuth(it) }
                }
            )
        }
    }

    suspend fun addTag(tag: TagNewDao): Result<Unit, Throwable> {
        return Result.of<Unit, Throwable> {
            githubApi.addTag(tag,
                ext = {
                    sessionManager.sessionFlow.value?.accessToken?.let { bearerAuth(it) }
                }
            )
        }.onFailure {
            logger.e(it) { "Error Save Tag" }
        }
    }

    suspend fun updateTag(tag: TagDao): Result<Unit, Throwable> {
        return Result.of<Unit, Throwable> {
            githubApi.updateTag(tag,
                ext = {
                    sessionManager.sessionFlow.value?.accessToken?.let { bearerAuth(it) }
                }
            )
        }.onFailure {
            logger.e(it) { "Error Update Tag" }
        }
    }

    suspend fun updateRepositoryTags(repositoryId: Long, newTags: List<Int>): Result<Unit, Throwable> {
        return Result.of<Unit, Throwable> {
            githubApi.updateRepositoryTags(repositoryId,
                TagsUpdateRequest(newTags),
                ext = {
                    sessionManager.sessionFlow.value?.accessToken?.let { bearerAuth(it) }
                }
            )
        }.onFailure {
            logger.e(it) { "Error Update Tags for Repository $repositoryId" }
        }
    }
}