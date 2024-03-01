package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.model.GHRepository
import com.takaotech.dashboard.model.GHUser
import com.takaotech.dashboard.model.MainCategory
import io.ktor.util.logging.*
import kotlinx.coroutines.*
import org.kohsuke.github.GitHub
import org.koin.core.annotation.Single
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Single
class GithubRepository(
	private val logger: Logger,
	private val githubClient: GitHub
) {
	suspend fun getAllStars() = coroutineScope {
		val downloadedRepository = getAllStarsRemote()

		val mapJobs = mutableListOf<Deferred<List<GHRepository>>>()

		try {
			downloadedRepository.let {
				//TODO Make split size a constant
				if (it.size < 4) {
					listOf(it)
				} else {
					it.chunked(it.size / 4)
				}
			}.forEach {
				mapJobs.add(
					async(Dispatchers.Default) {
						it.map { repository ->
							logger.info("Processing repository ID=${repository.id} Name=${repository.name} ")
							GHRepository(
								id = repository.id,
								name = repository.name,
								fullName = repository.fullName,
								description = repository.description,
								url = repository.url.toString(),
//								license = repository.license?.name,
								license = null,
//								licenseUrl = repository.licenseContent?.htmlUrl,
								licenseUrl = null,
								user = repository.owner.let { user ->
									GHUser(
										user.id,
										user.login,
										user.url.toString()
									)
								},
								languages = repository.listLanguages(),
								//Use default on data recovery
								mainCategory = MainCategory.NONE,
								//Use default on data recovery
								tags = listOf()
							)
						}
					}
				)
			}
		} catch (ex: Exception) {
			logger.error("Error download getAllStars", ex)
			//TODO throw correct exception
			throw ex
		}

		mapJobs.awaitAll().flatten()
	}

	private suspend fun getAllStarsRemote() = suspendCoroutine {
		try {
			val stars = githubClient
				.myself
				.listStarredRepositories()
				.toList()
			it.resume(stars)
		} catch (ex: Throwable) {
			/**
			 * org.kohsuke.github.HttpException: {"message":"Bad credentials","documentation_url":"https://docs.github.com/rest"}
			 * 	at org.kohsuke.github.GitHubConnectorResponseErrorHandler$1.onError(GitHubConnectorResponseErrorHandler.java:62)
			 * 	at org.kohsuke.github.GitHubClient.detectKnownErrors(GitHubClient.java:504)
			 * 	at org.kohsuke.github.GitHubClient.sendRequest(GitHubClient.java:464)
			 * 	at org.kohsuke.github.GitHubClient.sendRequest(GitHubClient.java:427)
			 * 	at org.kohsuke.github.Requester.fetch(Requester.java:85)
			 * 	at org.kohsuke.github.GitHub.setMyself(GitHub.java:583)
			 * 	at org.kohsuke.github.GitHub.getMyself(GitHub.java:577)
			 * 	at com.takaotech.dashboard.route.github.repository.GithubRepository.getAllStarsRemote(GithubRepository.kt:72)
			 * 	at com.takaotech.dashboard.route.github.repository.GithubRepository.access$getAllStarsRemote(GithubRepository.kt:15)
			 * 	at com.takaotech.dashboard.route.github.repository.GithubRepository$getAllStars$2.invokeSuspend(GithubRepository.kt:21)
			 * 	at com.takaotech.dashboard.route.github.repository.GithubRepository$getAllStars$2.invoke(GithubRepository.kt)
			 * 	at com.takaotech.dashboard.route.github.repository.GithubRepository$getAllStars$2.invoke(GithubRepository.kt)
			 * 	at kotlinx.coroutines.intrinsics.UndispatchedKt.startUndispatchedOrReturn(Undispatched.kt:78)
			 * 	at kotlinx.coroutines.CoroutineScopeKt.coroutineScope(CoroutineScope.kt:264)
			 * 	at com.takaotech.dashboard.route.github.repository.GithubRepository.getAllStars(GithubRepository.kt:20)
			 * 	at com.takaotech.dashboard.route.github.controller.GithubController$getStarsFromZeroAndStore$2.invokeSuspend(GithubController.kt:20)
			 * 	at com.takaotech.dashboard.route.github.controller.GithubController$getStarsFromZeroAndStore$2.invoke(GithubController.kt)
			 * 	at com.takaotech.dashboard.route.github.controller.GithubController$getStarsFromZeroAndStore$2.invoke(GithubController.kt)
			 * 	at kotlinx.coroutines.intrinsics.UndispatchedKt.startUndispatchedOrReturn(Undispatched.kt:78)
			 * 	at kotlinx.coroutines.CoroutineScopeKt.coroutineScope(CoroutineScope.kt:264)
			 * 	at com.takaotech.dashboard.route.github.controller.GithubController.getStarsFromZeroAndStore(GithubController.kt:18)
			 * 	at com.takaotech.dashboard.route.github.GithubRouteKt$githubRoute$1$2.invokeSuspend(GithubRoute.kt:28)
			 * 	at com.takaotech.dashboard.route.github.GithubRouteKt$githubRoute$1$2.invoke(GithubRoute.kt)
			 * 	at com.takaotech.dashboard.route.github.GithubRouteKt$githubRoute$1$2.invoke(GithubRoute.kt
			 */

			logger.error("Error getAllStartsRemote", ex)
			it.resumeWithException(ex)
		}
	}

	suspend fun getLanguagesByRepository(repositoryId: Long) = suspendCoroutine<Map<String, Long>> {
		try {
			val listLanguages = githubClient
				.getRepositoryById(repositoryId)
				.listLanguages()
			it.resume(listLanguages)
		} catch (ex: IOException) {
			logger.error("Error getLanguagesByRepository", ex)
			it.resumeWithException(ex)
		}
	}
}