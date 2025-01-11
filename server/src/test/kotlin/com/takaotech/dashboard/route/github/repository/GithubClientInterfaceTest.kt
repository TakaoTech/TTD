package com.takaotech.dashboard.route.github.repository

import app.cash.turbine.test
import io.github.serpro69.kfaker.Faker
import io.github.serpro69.kfaker.lorem.LoremFaker
import io.kotest.common.DelicateKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.ktor.util.logging.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.toKotlinInstant
import org.kohsuke.github.GitHub
import org.kohsuke.github.PagedIterator
import java.net.URL
import java.util.*
import kotlin.reflect.jvm.jvmName
import kotlin.test.assertTrue
import org.kohsuke.github.GHRepository as GHRepositoryExternal
import org.kohsuke.github.GHUser as GHUserExternal

@OptIn(DelicateKotest::class)
class GithubClientInterfaceTest : FunSpec() {
    private val logger = KtorSimpleLogger(this::class.jvmName)
    private val faker = Faker()
    private val fakerLorem = LoremFaker()
    private val domainRegex = Regex("^(https?://)([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(:\\d+)?(/.*)?$")

    private val expectedList = faker.random.nextInt(5..10)


    private val githubClient = mockk<GitHub>()
    private val ghUsers = mutableListOf<GHUserExternal>()

    private val mDateMock = Date()

    private val ghUserExternalGenerator = arbitrary { rs ->
        mockk<GHUserExternal>().also {
            every { it.id } returns rs.random.nextLong()
            every { it.login } returns faker.name.name()
            every { it.url } returns URL("https://${faker.internet.domain()}")
            every { it.avatarUrl } returns "https://${faker.internet.domain()}"
        }
    }.also {
        repeat(10) { _ ->
            ghUsers.add(it.next())
        }
    }

    private val ghRepositoryExternalGenerator = arbitrary { rs ->
        mockk<GHRepositoryExternal>().also {
            every { it.id } returns faker.random.nextLong()
            every { it.owner } returns ghUsers.random()
            every { it.name } returns faker.name.name()
            every { it.fullName } returns faker.name.name()
            every { it.description } returns fakerLorem.lorem.words()
            every { it.htmlUrl } returns URL("https://${faker.internet.domain()}")
            every { it.license } returns mockk {
                every { name } returns faker.name.nameWithMiddle()
                every { htmlUrl } returns URL("https://${faker.internet.domain()}")
            }
            every { it.updatedAt } returns mDateMock
            every { it.listLanguages() } returns Arb.map(Arb.pair(Arb.string(), Arb.long()), 1, 10).next()
        }
    }.let {
        Arb.list(gen = it, range = (1..10))
    }

    init {
        val githubClientInterface = GithubClientImpl2(
            logger = logger,
            githubClient = githubClient
        )


        test("getAllStarsRemote Success").config(coroutineTestScope = true) {
            val reposTest = List(expectedList) {
                ghRepositoryExternalGenerator.next()
            }

            val testIterator = reposTest.iterator()

            val pagedIterator = mockk<PagedIterator<GHRepositoryExternal>>()

            every { pagedIterator.hasNext() } answers { testIterator.hasNext() }
            every { pagedIterator.nextPage() } answers { testIterator.next() }

            every {
                githubClient
                    .myself
                    .listStarredRepositories()
                    .withPageSize(eq(10))
                    .iterator()
            } returns pagedIterator

            githubClientInterface.getAllStarsRemote().test {
                repeat(expectedList) { index ->
                    val repoTestList = reposTest[index]
                    val repoListResult = awaitItem()
                    repoListResult.size shouldBe repoTestList.size

                    repoListResult.forEach {
                        val repoTest = repoTestList.first { r -> r.id == it.id }

                        it.id shouldBe repoTest.id
                        it.name shouldBe repoTest.name
                        it.fullName shouldBe repoTest.fullName
                        it.description shouldBe repoTest.description
                        it.license shouldBe repoTest.license?.name
                        it.licenseUrl.also { licenseUrl ->
                            licenseUrl shouldBe repoTest.license?.htmlUrl?.toString()
                            if (licenseUrl != null) {
                                assertTrue {
                                    domainRegex.matches(licenseUrl)
                                }
                            }
                        }
                        it.updatedAt shouldBe repoTest.updatedAt.toInstant().toKotlinInstant()
                        it.languages.forEach { language ->
                            val languageTest = repoTest.listLanguages()[language.name]
                            language.lines shouldBe languageTest
                        }

                        it.user.also { user ->
                            user.id shouldBe repoTest.owner.id
                            user.name shouldBe repoTest.owner.login
                            user.url shouldBe repoTest.owner.url.toString()
                            user.avatarUrl shouldBe repoTest.owner.avatarUrl
                        }
                    }
                }

                awaitComplete()
            }
        }


    }
}