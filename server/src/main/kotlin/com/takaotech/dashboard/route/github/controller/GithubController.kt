package com.takaotech.dashboard.route.github.controller

import com.github.kittinunf.result.Result
import com.github.kittinunf.result.getOrNull
import com.takaotech.dashboard.model.TakaoPaging
import com.takaotech.dashboard.models.GHRepositoriesDao
import com.takaotech.dashboard.models.GHRepositoryDao
import com.takaotech.dashboard.models.GHRepositoryMiniDao
import com.takaotech.dashboard.models.MainCategory
import com.takaotech.dashboard.models.TagDao
import com.takaotech.dashboard.models.TagNewDao
import com.takaotech.dashboard.route.github.repository.DepositoryRepository
import com.takaotech.dashboard.route.github.repository.GithubRepository
import com.takaotech.dashboard.route.github.repository.TagsRepository
import org.koin.core.annotation.Singleton

@Singleton
class GithubController(
    private val githubRepository: GithubRepository,
    private val githubDepositoryRepository: DepositoryRepository,
    private val tagsRepository: TagsRepository,
) {
    /**
     * Download all starred repositories from GH Account and save it on db
     */
    suspend fun getStarsAndStore() {
        val refreshAt = githubDepositoryRepository.detachUpdateTimestamp()
        githubRepository.getAllStars().collect { repoList ->
            repoList.mapNotNull {
                it.getOrNull()
            }.also {
                githubDepositoryRepository.saveRepositoriesToDB(refreshAt, it)
            }
        }
    }

    suspend fun getRepository(category: MainCategory? = null): GHRepositoriesDao =
        GHRepositoriesDao(githubDepositoryRepository.getGHRepository(category))

    suspend fun getRepositoryMini(
        page: Int,
        size: Int,
    ): TakaoPaging<GHRepositoryMiniDao> =
        githubDepositoryRepository.getGHRepositoryMini(
            mainCategory = MainCategory.KOTLIN,
            page = page,
            size = size,
        )

    suspend fun getRepositoryById(id: Long): GHRepositoryDao? = githubDepositoryRepository.getGHRepositoryById(id)

    suspend fun updateMainCategoryAtRepository(
        repositoryId: Long,
        category: MainCategory,
    ) {
        githubDepositoryRepository.updateGhRepositoryMainCategory(repositoryId, category)
    }

    suspend fun getTags(
        page: Int? = null,
        size: Int? = null,
    ): TakaoPaging<TagDao> = tagsRepository.getTags(page, size)

    suspend fun updateTag(newTag: TagDao) {
        tagsRepository.updateTag(newTag)
    }

    suspend fun addTag(newTag: TagNewDao) {
        tagsRepository.addTag(newTag)
    }

    suspend fun removeTagById(tagId: Int) {
        tagsRepository.removeTag(tagId)
    }

    suspend fun getTagById(id: Int): TagDao? = tagsRepository.getTagById(id)

    suspend fun updateRepositoryTags(
        id: Long,
        tagIds: List<Int>,
    ): Result<Unit, Throwable> =
        tagIds
            .mapNotNull {
                tagsRepository.getTagByIdInternal(it)
            }.let { tags ->
                githubDepositoryRepository.setTagsAtRepository(id, tags)
            }

    suspend fun getRepositoryByTag(
        page: Int,
        size: Int,
        tagId: Int,
    ): TakaoPaging<GHRepositoryMiniDao> =
        githubDepositoryRepository.getGHRepositoryByTag(page = page, size = size, tagId = tagId)
}
