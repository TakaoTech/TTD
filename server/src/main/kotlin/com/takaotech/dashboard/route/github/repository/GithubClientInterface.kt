package com.takaotech.dashboard.route.github.repository

import com.github.kittinunf.result.Result
import com.github.kittinunf.result.onFailure
import com.takaotech.dashboard.model.github.GHRepositoryDao
import com.takaotech.dashboard.model.github.exception.GHExternalConversionException
import com.takaotech.dashboard.route.github.repository.utils.convertToGHRepositoryWithDefaults
import io.ktor.util.logging.*
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.kohsuke.github.GitHub
import org.koin.core.annotation.Factory
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

interface GithubClientInterface {
    fun getAllStarsRemote(): Flow<List<Result<GHRepositoryDao, GHExternalConversionException>>>

    suspend fun getLanguagesByRepository(repositoryId: Long): Map<String, Long>
}

@Factory
class GithubClientImpl(
    private val logger: Logger,
    private val githubClient: GitHub,
) : GithubClientInterface {
    override fun getAllStarsRemote(): Flow<List<Result<GHRepositoryDao, GHExternalConversionException>>> = flow {
        val iterator = githubClient
            .myself
            .listStarredRepositories()
            .withPageSize(10)
            .iterator()

        while (iterator.hasNext()) {
            currentCoroutineContext().ensureActive()
            val page = iterator.nextPage()
            page.mapNotNull { repository ->
                logger.debug("Conversion repository {} {}", repository.id.toString(), repository.name)
                Result.of<GHRepositoryDao, GHExternalConversionException> {
                    repository.convertToGHRepositoryWithDefaults()
                }.onFailure {
                    logger.error("Failed to convert repository {} {}:", repository.id.toString(), repository.name, it)
                }
            }.also { emit(it) }
        }
    }.buffer()
        .catch {
            logger.error("Error during flow processing GithubClientImpl.getAllStarsRemote: {}", it.message, it)
            throw it
        }

    override suspend fun getLanguagesByRepository(repositoryId: Long): Map<String, Long> =
        suspendCoroutine {
            try {
                val listLanguages =
                    githubClient
                        .getRepositoryById(repositoryId)
                        .listLanguages()
                it.resume(listLanguages)
            } catch (ex: IOException) {
                logger.error("Error getLanguagesByRepository", ex)
                it.resumeWithException(ex)
            }
        }
}