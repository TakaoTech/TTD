package com.takaotech.dashboard.route.github.controller

import com.takaotech.dashboard.model.GHRepositoryDao
import com.takaotech.dashboard.model.MainCategory
import com.takaotech.dashboard.route.github.repository.DepositoryRepository
import com.takaotech.dashboard.route.github.repository.GithubRepository
import kotlinx.coroutines.coroutineScope
import org.koin.core.annotation.Factory

@Factory
class GithubController(
	private val githubRepository: GithubRepository,
	private val githubDepositoryRepository: DepositoryRepository
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

	suspend fun updateMainCategoryAtRepository(repositoryId: Long, category: MainCategory?) {

	}
}