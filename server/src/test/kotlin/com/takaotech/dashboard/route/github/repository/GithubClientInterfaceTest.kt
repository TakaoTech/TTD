package com.takaotech.dashboard.route.github.repository

import app.cash.turbine.test
import com.github.kittinunf.result.isFailure
import com.github.kittinunf.result.isSuccess
import com.takaotech.dashboard.model.github.exception.GHExternalConversionException
import com.takaotech.dashboard.route.github.repository.utils.convertToGHRepositoryWithDefaults
import com.takaotech.dashboard.utils.getGHUserExternalGenerator
import io.github.serpro69.kfaker.Faker
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.of
import io.ktor.util.logging.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.kohsuke.github.GitHub
import org.kohsuke.github.PagedIterator
import kotlin.math.abs
import kotlin.reflect.jvm.jvmName
import org.kohsuke.github.GHRepository as GHRepositoryExternal

class GithubClientInterfaceTest : BehaviorSpec({
    val logger = KtorSimpleLogger(GithubClientInterfaceTest::class.jvmName)
    val faker = Faker()
    val expectedList = faker.random.nextInt(5..10)

    val githubClient = mockk<GitHub>()
    val githubClientInterface = GithubClientImpl(
        logger = logger,
        githubClient = githubClient
    )


    val externalUserMock = getGHUserExternalGenerator()

    val ghMock = mockk<GHRepositoryExternal>(relaxed = true) {
        every { id } returns abs(faker.random.nextLong())
        every { name } returns faker.name.firstName()
        every { owner } returns externalUserMock.next()
    }

    lateinit var reposTest: List<List<GHRepositoryExternal>>
    lateinit var pagedIterator: PagedIterator<GHRepositoryExternal>

    beforeContainer {
        mockkStatic(GHRepositoryExternal::convertToGHRepositoryWithDefaults)

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

    afterContainer {
        unmockkAll()
    }

    Given("getAllStarsRemote method") {
        When("repository conversion is successful") {
            every {
                ghMock.convertToGHRepositoryWithDefaults()
            } returns mockk()

            Then("should return successful results for all repositories").config(coroutineTestScope = true) {
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
        }

        When("repository conversion throws GHExternalConversionException") {
            every {
                ghMock.convertToGHRepositoryWithDefaults()
            } answers {
                throw GHExternalConversionException("", "", "", Exception())
            }

            Then("should return failure results for all repositories").config(coroutineTestScope = true) {
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
        }

        When("repository conversion throws a general exception") {
            every { ghMock.convertToGHRepositoryWithDefaults() } answers {
                throw Exception("Test exception")
            }

            Then("should propagate the error").config(coroutineTestScope = true) {
                githubClientInterface.getAllStarsRemote().test {
                    awaitError()
                }
            }
        }
    }
})
