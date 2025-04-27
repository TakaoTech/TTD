package com.takaotech.dashboard.route.github.controller

import com.github.kittinunf.result.Result
import com.takaotech.dashboard.model.github.exception.GHExternalConversionException
import com.takaotech.dashboard.models.GHRepositoryDao
import com.takaotech.dashboard.route.github.repository.DepositoryRepository
import com.takaotech.dashboard.route.github.repository.GithubRepository
import com.takaotech.dashboard.route.github.repository.TagsRepository
import io.kotest.core.spec.style.BehaviorSpec
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

class GithubControllerTest : BehaviorSpec({
    val refreshAt = Clock.System.now()

    Given("getStarsAndStore method") {
        val githubRepository = mockk<GithubRepository>()
        val depository = mockk<DepositoryRepository>()
        val tagsRepository = mockk<TagsRepository>()
        val controller = GithubController(githubRepository, depository, tagsRepository)

        coEvery {
            depository.detachUpdateTimestamp()
        } returns refreshAt

        When("repository list contains both successful and failed items") {
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

            Then("should filter out failed repositories and save only successful ones") {
                controller.getStarsAndStore()

                coVerify(exactly = 1) {
                    depository.saveRepositoriesToDB(eq(refreshAt), match { it.size == 2 })
                }
            }
        }

        When("flow collection is cancelled during processing") {
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

            Then("should process only the first batch of repositories before cancellation") {
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
    }
})
