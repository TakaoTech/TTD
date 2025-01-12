package com.takaotech.dashboard.route.github.repository

import app.cash.turbine.test
import com.github.kittinunf.result.isFailure
import com.github.kittinunf.result.isSuccess
import com.takaotech.dashboard.model.github.exception.GHExternalConversionException
import com.takaotech.dashboard.route.github.repository.utils.convertToGHRepositoryWithDefaults
import io.github.serpro69.kfaker.Faker
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.of
import io.ktor.util.logging.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.kohsuke.github.GitHub
import org.kohsuke.github.PagedIterator
import kotlin.reflect.jvm.jvmName
import org.kohsuke.github.GHRepository as GHRepositoryExternal

class GithubClientInterfaceTest : FunSpec() {
    private val logger = KtorSimpleLogger(this::class.jvmName)
    private val faker = Faker()
    private val expectedList = faker.random.nextInt(5..10)

    private val githubClient = mockk<GitHub>()

    private lateinit var reposTest: List<List<GHRepositoryExternal>>
    private lateinit var pagedIterator: PagedIterator<GHRepositoryExternal>

    init {
        val githubClientInterface = GithubClientImpl2(
            logger = logger,
            githubClient = githubClient
        )

        val ghMock = mockk<GHRepositoryExternal>(relaxed = true) {
            every { id } returns faker.random.nextLong()
            every { name } returns faker.name.firstName()
        }

        beforeEach {
            mockkStatic("com.takaotech.dashboard.route.github.repository.utils.GHRepositoryUtilsKt")

            reposTest = Arb.list(
                gen = Arb.of(
                    ghMock
                ),
                range = (1..10)
            ).let {
                List(expectedList) { _ ->
                    it.next()
                }
            }

            val testIterator = reposTest.iterator()
            pagedIterator = mockk<PagedIterator<GHRepositoryExternal>>()
            every { pagedIterator.hasNext() } answers { testIterator.hasNext() }
            every { pagedIterator.nextPage() } answers { testIterator.next() }

            every {
                githubClient
                    .myself
                    .listStarredRepositories()
                    .withPageSize(eq(10))
                    .iterator()
            } returns pagedIterator
        }

        context("getAllStarsRemote") {
            test("Happy flow").config(coroutineTestScope = true) {
                every { ghMock.convertToGHRepositoryWithDefaults() } returns mockk()
                githubClientInterface.getAllStarsRemote().test {
                    repeat(expectedList) { index ->
                        val repoTestList = reposTest[index]
                        val repoListResult = awaitItem()
                        repoListResult.size shouldBe repoTestList.size
                        repoListResult.all {
                            it.isSuccess()
                        } shouldBe true
                    }
                    awaitComplete()
                }
            }

            test("throw GHExternalConversionException").config(coroutineTestScope = true) {
                every { ghMock.convertToGHRepositoryWithDefaults() } answers {
                    throw GHExternalConversionException("", "", "", Exception())
                }
                githubClientInterface.getAllStarsRemote().test {
                    repeat(expectedList) { index ->
                        val repoTestList = reposTest[index]
                        val repoListResult = awaitItem()
                        repoListResult.size shouldBe repoTestList.size
                        repoListResult.all {
                            it.isFailure()
                        } shouldBe true

                    }
                    awaitComplete()
                }
            }

            test("throw Exception").config(coroutineTestScope = true) {
                every { ghMock.convertToGHRepositoryWithDefaults() } answers {
                    throw Exception("Test exception")
                }
                githubClientInterface.getAllStarsRemote().test {
                    awaitError()
                }
            }
        }

        xtest("getLanguagesByRepository Success").config(coroutineTestScope = true) {

        }


    }
}