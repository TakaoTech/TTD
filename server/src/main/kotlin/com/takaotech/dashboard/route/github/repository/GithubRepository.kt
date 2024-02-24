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
								url = repository.url.toString(),
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