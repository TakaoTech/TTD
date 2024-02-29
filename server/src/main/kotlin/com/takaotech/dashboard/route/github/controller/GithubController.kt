package com.takaotech.dashboard.route.github.controller

import com.takaotech.dashboard.model.GHRepository
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

	suspend fun getStoredRepository(category: MainCategory? = null): List<GHRepository> {
		return githubDepositoryRepository.getDepository(category)
	}
}