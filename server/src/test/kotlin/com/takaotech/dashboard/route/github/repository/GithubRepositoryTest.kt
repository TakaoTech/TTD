package com.takaotech.dashboard.route.github.repository

import app.cash.turbine.test
import com.github.kittinunf.result.Result
import com.takaotech.dashboard.model.github.exception.GHExternalConversionException
import com.takaotech.dashboard.models.GHRepositoryDao
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flow

class GithubRepositoryTest : FunSpec() {

    lateinit var githubClient: GithubClientInterface

    lateinit var repository: GithubRepository

    init {
        beforeTest {
            githubClient = mockk<GithubClientInterface>()
            repository = GithubRepository(
                mockk(relaxed = true),
                githubClient
            )
        }

        context("getAllStars") {
            test("Happy flow") {
                val mockedRepoList = List(2) {
                    listOf(
                        Result.success(mockk<GHRepositoryDao>()),
                        Result.success(mockk<GHRepositoryDao>()),
                        Result.failure(
                            GHExternalConversionException(
                                "Test id",
                                "Test name",
                                "Test property",
                                Exception()
                            )
                        )
                    )
                }

                val mockFlow = flow {
                    emit(mockedRepoList[0])
                    emit(mockedRepoList[1])
                }

                coEvery {
                    githubClient.getAllStarsRemote()
                } returns mockFlow

                repository.getAllStars()

                coVerify(exactly = 1) {
                    githubClient.getAllStarsRemote()
                }
            }

            test("Cancelled") {
                val mockFlow = flow<List<Result<GHRepositoryDao, GHExternalConversionException>>> {
                    throw IllegalStateException("Test exception")
                }

                coEvery {
                    githubClient.getAllStarsRemote()
                } returns mockFlow

                repository.getAllStars().test {
                    awaitComplete()
                }

                coVerify(exactly = 1) {
                    githubClient.getAllStarsRemote()
                }
            }
        }
    }
}