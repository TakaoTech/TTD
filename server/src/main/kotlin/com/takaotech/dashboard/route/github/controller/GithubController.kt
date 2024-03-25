package com.takaotech.dashboard.route.github.controller

import com.takaotech.dashboard.model.TakaoPaging
import com.takaotech.dashboard.model.github.*
import com.takaotech.dashboard.route.github.repository.DepositoryRepository
import com.takaotech.dashboard.route.github.repository.GithubRepository
import com.takaotech.dashboard.route.github.repository.TagsRepository
import kotlinx.coroutines.coroutineScope
import org.koin.core.annotation.Factory

@Factory
class GithubController(
	private val githubRepository: GithubRepository,
	private val githubDepositoryRepository: DepositoryRepository,
	private val tagsRepository: TagsRepository
) {

	/**
	 * Download all starred repositories from GH Account and save it on db
	 */
	suspend fun getStarsFromZeroAndStore() = coroutineScope {
//		val mapJobs = mutableListOf<Deferred<List<GHRepository>>>()
		val allStars = githubRepository.getAllStars()
			.filter { !githubDepositoryRepository.ghRepositoryExist(it.id) }

//			.let {
//				if (it.size < 4) {
//					listOf(it)
//				} else {
//					it.chunked(it.size / 4)
//				}
//			}.map {
//				mapJobs.add(
//					async {
//						it.map {
//							it.copy(
//								languages = githubRepository.getLanguagesByRepository(it.id)
//							)
//						}
//					}
//				)
//			}

//		allStars = mapJobs.awaitAll().flatten()

		githubDepositoryRepository.saveRepositoriesToDB(allStars)

	}

	suspend fun getRepository(category: MainCategory? = null): List<GHRepositoryDao> {
		return githubDepositoryRepository.getGHRepository(category)
	}

	suspend fun getRepositoryMini(page: Int, size: Int): TakaoPaging<GHRepositoryMiniDao> {
		return githubDepositoryRepository.getGHRepositoryMini(
			mainCategory = MainCategory.KOTLIN,
			page = page,
			size = size
		)
	}

	suspend fun getRepositoryById(id: Long): GHRepositoryDao? {
		return githubDepositoryRepository.getGHRepositoryById(id)
	}

	suspend fun updateMainCategoryAtRepository(repositoryId: Long, category: MainCategory) {
		githubDepositoryRepository.updateGhRepositoryMainCategory(repositoryId, category)
	}

	suspend fun getTags(page: Int? = null, size: Int? = null): List<TagDao> {
		return tagsRepository.getTags(page, size)
	}

	suspend fun updateTag(newTag: TagDao) {
		tagsRepository.updateTag(newTag)
	}

	suspend fun addTag(newTag: TagNewDao) {
		tagsRepository.addTag(newTag)
	}

	suspend fun removeTagById(tagId: Int) {
		tagsRepository.removeTag(tagId)
	}

	suspend fun getTagById(id: Int): TagDao? {
		return tagsRepository.getTagById(id)
	}

	suspend fun updateRepositoryTags(id: Long, tagIds: List<Int>) {
		tagIds.mapNotNull {
			tagsRepository.getTagByIdInternal(it)
		}.let { tags ->
			githubDepositoryRepository.setTagsAtRepository(id, tags)
		}
	}
}