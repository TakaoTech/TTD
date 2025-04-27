package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.utils.RedisDatabase
import com.takaotech.dashboard.utils.getGithubColorsFile
import com.takaotech.dashboard.utils.getRedisConfiguration
import com.takaotech.dashboard.utils.installRedis
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.clock.TestClock
import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.mockk.clearMocks
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import kotlinx.serialization.json.JsonObject
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.days

class GithubColorControllerTest : BehaviorSpec({
    lateinit var colorController: GithubColorControllerImpl
    lateinit var redisDatabase: RedisDatabase

    val githubColorsFile = getGithubColorsFile()
    val redis = installRedis()

    var callCounter = 0

    val mockEngine = MockEngine {
        it.url.toString() shouldBe "https://raw.githubusercontent.com/ozh/github-colors/master/colors.json"
        callCounter++

        respond(
            content = ByteReadChannel(githubColorsFile),
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )
    }

    val httpClient = HttpClient(mockEngine)

    val clock = TestClock(Clock.System.now().toJavaInstant(), ZoneOffset.UTC)

    beforeSpec {
        val redisConfiguration = getRedisConfiguration(
            redis.redisURI
        )

        redisDatabase = RedisDatabase(
            redisConfiguration
        ).also {
            runBlocking {
                it.connect()
            }
        }
    }

    beforeContainer {
        callCounter = 0
        colorController = spyk(
            GithubColorControllerImpl(redisDatabase, httpClient),
            recordPrivateCalls = true
        )
    }

    beforeContainer {
        clearMocks(
            colorController,
            answers = false,
            recordedCalls = true,
            childMocks = false,
            verificationMarks = false,
            exclusionRules = false
        )
    }

    afterContainer {
        unmockkObject(Clock.System)
    }

    Given("GitHub color controller with empty cache") {
        When("requesting Kotlin language color for the first time") {
            val color = colorController.getColorLanguageByName("Kotlin")

            Then("should fetch color from remote and return correct value") {
                color shouldBe "#A97BFF"

                // This is bugged, method called multiple times https://github.com/mockk/mockk/issues/554
                coVerify {
                    colorController invokeNoArgs "checkNeedUpdate"
                    colorController invokeNoArgs "getColorLanguagesMappingRemote"
                    colorController invoke "setColorLanguagesMappingLocal" withArguments listOf(any<JsonObject>())
                    colorController["setLastUpdateMapping"](any<Instant>())
                }

                callCounter shouldBe 1
            }
        }

        When("requesting Kotlin language color after it's cached") {
            // First call to cache the data
            colorController.getColorLanguageByName("Kotlin")
            callCounter = 0

            // Second call should use cache
            val color = colorController.getColorLanguageByName("Kotlin")

            Then("should use cached value without remote call") {
                color shouldBe "#A97BFF"

                verify {
                    colorController["checkNeedUpdate"]()
                }

                verify(exactly = 0) {
                    colorController["setColorLanguagesMappingLocal"](any<JsonObject>())
                    colorController["setLastUpdateMapping"](any<Instant>())
                }

                callCounter shouldBe 0
            }
        }

        When("requesting a non-existent language color") {
            val color = colorController.getColorLanguageByName("NotExisted")

            Then("should return default color") {
                color shouldBe "#ededed"

                verify {
                    colorController["checkNeedUpdate"]()
                }

                verify(exactly = 0) {
                    colorController["setColorLanguagesMappingLocal"](any<JsonObject>())
                    colorController["setLastUpdateMapping"](any<Instant>())
                }

                callCounter shouldBe 0
            }
        }
    }

    Given("GitHub color controller with populated cache") {
        // Ensure cache is populated
        colorController.getColorLanguageByName("Kotlin")
        callCounter = 0
        clearMocks(
            colorController,
            answers = false,
            recordedCalls = true,
            childMocks = false,
            verificationMarks = false,
            exclusionRules = false
        )

        When("requesting Kotlin language color") {
            val color = colorController.getColorLanguageByName("Kotlin")

            Then("should use cached value") {
                color shouldBe "#A97BFF"

                verify {
                    colorController["checkNeedUpdate"]()
                    colorController["getColorLanguagesMappingLocal"]()
                }

                verify(exactly = 0) {
                    colorController["setColorLanguagesMappingLocal"](any<JsonObject>())
                    colorController["setLastUpdateMapping"](any<Instant>())
                }

                callCounter shouldBe 0
            }
        }

        When("requesting a non-existent language color") {
            val color = colorController.getColorLanguageByName("NotExisted")

            Then("should return default color") {
                color shouldBe "#ededed"

                verify {
                    colorController["checkNeedUpdate"]()
                }

                verify(exactly = 0) {
                    colorController["setColorLanguagesMappingLocal"](any<JsonObject>())
                    colorController["setLastUpdateMapping"](any<Instant>())
                }

                callCounter shouldBe 0
            }
        }
    }

    Given("GitHub color controller with cache that needs refresh") {
        mockkObject(Clock.System)
        every { Clock.System.now() } answers {
            clock.instant().toKotlinInstant()
        }

        When("requesting color after cache expiration period") {
            // First call to populate cache
            colorController.getColorLanguageByName("Kotlin") shouldBe "#A97BFF"

            verify {
                colorController["checkNeedUpdate"]()
                colorController["getColorLanguagesMappingLocal"]()
            }

            // Advance time to trigger cache refresh
            clock.plus(35.days)

            // Second call should refresh cache
            val color = colorController.getColorLanguageByName("Kotlin")

            Then("should refresh cache and return correct value") {
                color shouldBe "#A97BFF"

                verify {
                    colorController["checkNeedUpdate"]()
                    colorController["getColorLanguagesMappingRemote"]()
                    colorController["setColorLanguagesMappingLocal"](any<JsonObject>())
                    colorController["setLastUpdateMapping"](any<Instant>())
                }
            }
        }
    }
})