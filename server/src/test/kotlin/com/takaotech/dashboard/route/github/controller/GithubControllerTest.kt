package com.takaotech.dashboard.route.github.controller

import com.github.kittinunf.result.Result
import com.takaotech.dashboard.model.github.exception.GHExternalConversionException
import com.takaotech.dashboard.models.GHRepositoryDao
import com.takaotech.dashboard.route.github.repository.DepositoryRepository
import com.takaotech.dashboard.route.github.repository.GithubRepository
import com.takaotech.dashboard.route.github.repository.TagsRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class GithubControllerTest :
    FunSpec({
        var githubRepository = mockk<GithubRepository>()
        var depository = mockk<DepositoryRepository>()
        var tagsRepository = mockk<TagsRepository>()
        var controller = GithubController(githubRepository, depository, tagsRepository)

        val refreshAt = Clock.System.now()

        beforeEach {
            githubRepository = mockk<GithubRepository>()
            depository = mockk<DepositoryRepository>()
            tagsRepository = mockk<TagsRepository>()
            controller = GithubController(githubRepository, depository, tagsRepository)

            coEvery {
                depository.detachUpdateTimestamp()
            } returns refreshAt
        }

        context("getStarsAndStore") {
            test("Happy flow with some errors") {
                val mockedRepoList = listOf(
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

                val mockFlow = flow {
                    emit(mockedRepoList)
                }

                coEvery {
                    githubRepository.getAllStars()
                } returns mockFlow

                coEvery {
                    depository.saveRepositoriesToDB(any(), any())
                } just Runs

                controller.getStarsAndStore()

                coVerify(exactly = 1) {
                    depository.saveRepositoriesToDB(eq(refreshAt), match { it.size == 2 })
                }
            }

            test("Flow cancelled") {
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
                    delay(10)
                    emit(mockedRepoList[1])
                }

                coEvery {
                    githubRepository.getAllStars()
                } returns mockFlow

                coEvery {
                    depository.saveRepositoriesToDB(any(), any())
                } coAnswers {
                    delay(5)
                    Runs
                }

                val starAndStoreJob = launch {
                    controller.getStarsAndStore()
                }

                delay(1)
                starAndStoreJob.cancelAndJoin()

                starAndStoreJob.isCancelled shouldBe true

                coVerify(exactly = 1) {
                    depository.saveRepositoriesToDB(eq(refreshAt), match { it.size == 2 })
                }

                coVerify(exactly = 0) {
                    depository.saveRepositoriesToDB(eq(refreshAt), match { it.size == 3 })
                }
            }
        }
    })
