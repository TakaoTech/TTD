package com.takaotech.dashboard.route.github.controller

import com.takaotech.dashboard.route.github.repository.GithubRepository
import org.koin.core.annotation.Factory

@Factory
class GithubController(private val githubRepository: GithubRepository) {

	suspend fun getStarsFromZeroAndStore() {
		githubRepository.getAllStars().map {
			it.copy(
				languages = githubRepository.getLanguagesByRepository(it.id)
			)
		}
	}
}