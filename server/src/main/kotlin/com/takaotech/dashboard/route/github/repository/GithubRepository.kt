package com.takaotech.dashboard.route.github.repository

import io.ktor.util.logging.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import org.koin.core.annotation.Singleton

@Singleton
class GithubRepository(
    private val logger: Logger,
    private val githubClient: GithubClientInterface,
) {
    suspend fun getAllStars() = githubClient.getAllStarsRemote()
        .cancellable()
        .catch {
            logger.error("Cancelling getAllStars", it)
            currentCoroutineContext().cancel()
        }

    suspend fun getRepositoryLanguages(repositoryId: Long) = githubClient.getLanguagesByRepository(repositoryId)
}
