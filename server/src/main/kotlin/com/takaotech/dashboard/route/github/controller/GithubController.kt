package com.takaotech.dashboard.route.github.controller

import com.takaotech.dashboard.model.GHRepositoryDao
import com.takaotech.dashboard.model.MainCategory
import com.takaotech.dashboard.model.Tag
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

	suspend fun getRepositoryById(id: Long): GHRepositoryDao? {
		return githubDepositoryRepository.getGHRepositoryById(id)
	}

	suspend fun updateMainCategoryAtRepository(repositoryId: Long, category: MainCategory) {
		githubDepositoryRepository.updateGhRepositoryMainCategory(repositoryId, category)
	}

	suspend fun getTags(): List<Tag> {
		return tagsRepository.getTags()
	}

	suspend fun addTag(newTag: Tag) {
		tagsRepository.addTag(newTag)
	}

	suspend fun removeTagById(tagName: String) {
		tagsRepository.removeTag(tagName)
	}

	suspend fun getTagById(id: String): Tag? {
		return tagsRepository.getTagById(id)
	}
}