package com.takaotech.dashboard.route.github.repository

import com.takaotech.dashboard.utils.RedisDatabase
import com.takaotech.dashboard.utils.getGithubColorsFile
import com.takaotech.dashboard.utils.getRedisConfiguration
import com.takaotech.dashboard.utils.installRedis
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCaseOrder
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

class GithubColorControllerTest : FunSpec() {

    override fun testCaseOrder(): TestCaseOrder = TestCaseOrder.Sequential

    lateinit var colorController: GithubColorControllerImpl
    lateinit var redisDatabase: RedisDatabase

    init {
        coroutineTestScope = true
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

        beforeTest {
            callCounter = 0
        }

        beforeContainer {
            colorController = spyk(
                GithubColorControllerImpl(redisDatabase, httpClient),
                recordPrivateCalls = true
            )
        }

        afterTest {
            clearMocks(
                colorController,
                answers = false,
                recordedCalls = true,
                childMocks = false,
                verificationMarks = false,
                exclusionRules = false
            )
        }

        context("Data Colors start from remote") {
            test("Color from remote and get Kotlin") {
                colorController.getColorLanguageByName("Kotlin") shouldBe "#A97BFF"

                // This is bugged, method called multiple times https://github.com/mockk/mockk/issues/554
                coVerify {
                    colorController["checkNeedUpdate"]()
                    colorController["getColorLanguagesMappingRemote"]()
                    colorController["setColorLanguagesMappingLocal"](any<JsonObject>())
                    colorController["setLastUpdateMapping"](any<Instant>())
                }

                callCounter shouldBe 1
            }

            test("Color from local and get Kotlin") {
                colorController.getColorLanguageByName("Kotlin") shouldBe "#A97BFF"

                verify {
                    colorController["checkNeedUpdate"]()
                }

                verify(exactly = 0) {
                    colorController["setColorLanguagesMappingLocal"](any<JsonObject>())
                    colorController["setLastUpdateMapping"](any<Instant>())
                }


                callCounter shouldBe 0
            }

            test("Color from local and get NotExisted language and get default color") {
                colorController.getColorLanguageByName("NotExisted") shouldBe "#ededed"

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

        context("Data color start from local") {
            test("Color from local and get Kotlin") {
                colorController.getColorLanguageByName("Kotlin") shouldBe "#A97BFF"

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

            test("Color from local and get NotExisted language and get default color") {
                colorController.getColorLanguageByName("NotExisted") shouldBe "#ededed"

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

        context("Data color Refresh") {
            test("Data Colors updated after coutdown date") {
                mockkObject(Clock.System)
                every { Clock.System.now() } answers {
                    clock.instant().toKotlinInstant()
                }

                colorController.getColorLanguageByName("Kotlin") shouldBe "#A97BFF"

                verify {
                    colorController["checkNeedUpdate"]()
                    colorController["getColorLanguagesMappingLocal"]()
                }

                clock.plus(35.days)

                colorController.getColorLanguageByName("Kotlin") shouldBe "#A97BFF"

                verify {
                    colorController["checkNeedUpdate"]()
                    colorController["getColorLanguagesMappingRemote"]()
                    colorController["setColorLanguagesMappingLocal"](any<JsonObject>())
                    colorController["setLastUpdateMapping"](any<Instant>())
                }

                unmockkObject(Clock.System)

            }
        }
    }
}
