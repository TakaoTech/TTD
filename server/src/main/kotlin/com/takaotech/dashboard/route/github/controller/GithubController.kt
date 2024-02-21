package com.takaotech.dashboard.route.github.controller

import com.takaotech.dashboard.route.github.repository.GithubRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.koin.core.annotation.Factory

@Factory
class GithubController(private val githubRepository: GithubRepository) {

	suspend fun getStarsFromZeroAndStore() = coroutineScope {
		val mergedStarsWithLanguages = githubRepository.getAllStars().map {
			async {
				it.copy(
					languages = githubRepository.getLanguagesByRepository(it.id)
				)
			}
		}

		mergedStarsWithLanguages.awaitAll()
	}
}