package com.takaotech.dashboard.route.github.repository

import io.ktor.util.logging.*
import org.koin.core.annotation.Factory

@Factory
class GithubRepository(
	private val logger: Logger,
	private val githubClient: GithubClientInterface
) {
	suspend fun getAllStars() = githubClient.getAllStarsRemote()

	suspend fun getRepositoryLanguages(repositoryId: Long) = githubClient.getLanguagesByRepository(repositoryId)
}