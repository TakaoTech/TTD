package com.takaotech.dashboard.utils

import com.redis.testcontainers.RedisContainer
import com.takaotech.dashboard.configuration.DbConfiguration
import com.takaotech.dashboard.configuration.RedisConfiguration
import com.takaotech.dashboard.configuration.SqlDbConfiguration
import com.takaotech.dashboard.di.getGeneralModule
import com.takaotech.dashboard.model.github.*
import com.takaotech.dashboard.route.github.repository.GithubColorControllerImpl.Companion.FALLBACK_COLOR
import com.zaxxer.hikari.HikariDataSource
import io.github.serpro69.kfaker.Faker
import io.github.serpro69.kfaker.lorem.LoremFaker
import io.kotest.common.DelicateKotest
import io.kotest.core.extensions.install
import io.kotest.core.spec.Spec
import io.kotest.extensions.testcontainers.ContainerExtension
import io.kotest.extensions.testcontainers.ContainerLifecycleMode
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerExtension
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.distinct
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.orNull
import io.ktor.util.logging.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okio.IOException
import org.kohsuke.github.GHRepository
import org.koin.ksp.generated.defaultModule
import org.testcontainers.Testcontainers
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.startupcheck.IsRunningStartupCheckStrategy
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy
import java.io.File
import java.net.URL
import java.nio.file.Paths
import java.util.*
import kotlin.math.abs
import org.kohsuke.github.GHUser as GHUserExternal

const val GITHUB_TEST_RESOURCE_PATH = "/src/test/resources/github"
const val POSTGRESQL_DRIVER = "org.postgresql.Driver"

val LOGGER = KtorSimpleLogger("TestLogger")

fun Spec.installRedis() = install(
    ContainerExtension(
        container = RedisContainer(RedisContainer.DEFAULT_IMAGE_NAME.withTag(RedisContainer.DEFAULT_TAG)),
        mode = ContainerLifecycleMode.Spec,
    ),
) {
    Testcontainers.exposeHostPorts(redisPort)
}

fun Spec.installPostgres() = install(
    JdbcDatabaseContainerExtension(
        container = PostgreSQLContainer("postgres").apply {
//                    withStartupCheckStrategy(
//                        IndefiniteWaitOneShotStartupCheckStrategy()
//                            .withTimeout(10.seconds.toJavaDuration())
//                    )
            // Colima Mitigation
            withStartupCheckStrategy(
                IsRunningStartupCheckStrategy()
            )
            setWaitStrategy(HostPortWaitStrategy())
            withStartupAttempts(5)
        },
        mode = ContainerLifecycleMode.Spec,
    )
)

fun getSqlDbConfiguration(postgres: HikariDataSource) =
    SqlDbConfiguration(
        url = postgres.jdbcUrl,
        driver = POSTGRESQL_DRIVER,
        user = postgres.username,
        password = postgres.password,
    )

fun getRedisConfiguration(redisURI: String) =
    RedisConfiguration(
        url = redisURI,
    )

fun getDbConfiguration(
    postgres: HikariDataSource,
    redisURI: String
) = DbConfiguration(
    sqlDbConfiguration = getSqlDbConfiguration(postgres),
    redisConfiguration = getRedisConfiguration(redisURI),
)

fun getBaseTestKoin() =
    listOf(
        getGeneralModule(
            log = LOGGER,
//        dbConfiguration = getDbConfiguration(),
//        githubConfiguration = GithubConfiguration(
//            githubToken = System.getenv("SERVER_GITHUB_TOKEN")
//        ),
//        credentialConfig = CredentialConfig(
//            googleJwtConfig = GoogleJwtConfig(
//                issuer = System.getenv("JWT_GOOGLE_ISSUER"),
//                audience = System.getenv("JWT_GOOGLE_AUDIENCE")
//            ),
//            takaoJwtConfig = TakaoJwtConfig(
//                version = System.getenv("JWT_TAKAO_VERSION").toInt(),
//                secret = System.getenv("JWT_TAKAO_SECRET"),
//                issuer = System.getenv("JWT_TAKAO_ISSUER"),
//                audience = System.getenv("JWT_TAKAO_AUDIENCE"),
//                realm = System.getenv("JWT_TAKAO_REALM"),
//                accessLifetime = System.getenv("JWT_TAKAO_ACCESS_LIFETIME"),
//                refreshLifetime = System.getenv("JWT_TAKAO_REFRESH_LIFETIME")
//            )
//        )
        ),
        defaultModule,
    )

class TestCustomException : Exception()

fun getGHRepositoryGenerator(
    ghUsers: List<GHUser>,
    languages: List<GHLanguageDao>,
    updatedAt: Instant,
    tags: List<TagDao>,
    mainCategory: MainCategory,
): Arb<GHRepositoryDao> {
    val faker = Faker()
    val fakerLorem = LoremFaker()
    return arbitrary {
        GHRepositoryDao(
            id = abs(faker.random.nextLong()),
            name = faker.name.name(),
            fullName = faker.name.name(),
            description = fakerLorem.lorem.words(),
            url = "https://${faker.internet.domain()}",
            license = faker.name.nameWithMiddle(),
            licenseUrl = "https://${faker.internet.domain()}",
            user = ghUsers.random(),
            languages = languages,
            updatedAt = updatedAt,
            tags = tags,
            mainCategory = mainCategory
        )
    }
}

fun getGHRepositoryExternalGenerator(
    ghUsers: List<GHUserExternal>,
    mDateMock: Date,
    ownerModifier: GHFieldModifier? = null,
    languagesModifier: GHFieldModifier? = null,
    licenseModifier: GHFieldModifier? = null,
): Arb<GHRepository> {
    val faker = Faker()
    val fakerLorem = LoremFaker()
    return arbitrary { rs ->
        mockk<GHRepository>().also {
            every { it.id } returns abs(faker.random.nextLong())
            every { it.owner }.also {
                when (ownerModifier) {
                    GHFieldModifier.AS_NULL -> it returns null

                    GHFieldModifier.IOEXCEPTION -> it answers {
                        throw IOException()
                    }

                    GHFieldModifier.OTHER_EXCEPTION -> it answers {
                        throw TestCustomException()
                    }

                    null -> it returns ghUsers.random()
                }
            }
            every { it.name } returns faker.name.name()
            every { it.fullName } returns faker.name.name()
            every { it.description } returns fakerLorem.lorem.words()
            every { it.htmlUrl } returns URL("https://${faker.internet.domain()}")
            every { it.license }.let {
                when (licenseModifier) {
                    GHFieldModifier.AS_NULL -> it returns null
                    GHFieldModifier.IOEXCEPTION -> it answers {
                        throw IOException()
                    }

                    GHFieldModifier.OTHER_EXCEPTION -> it answers {
                        throw TestCustomException()
                    }

                    null -> it returns mockk {
                        every { name } returns faker.name.nameWithMiddle()
                        every { htmlUrl } returns Arb.of(URL("https://${faker.internet.domain()}")).orNull().next()
                    }
                }
            }
            every { it.updatedAt } returns mDateMock
            every { it.listLanguages() }.let {
                val languagesGen = generateLanguages(faker.random.nextInt(0..10))

                when (languagesModifier) {
                    GHFieldModifier.AS_NULL -> it returns null
                    GHFieldModifier.IOEXCEPTION -> it answers {
                        throw IOException()
                    }

                    GHFieldModifier.OTHER_EXCEPTION -> it answers {
                        throw TestCustomException()
                    }

                    null -> it returns languagesGen.associate { ghLanguageDao ->
                        ghLanguageDao.name to ghLanguageDao.lines
                    }
                }
            }
        }
    }
}

@OptIn(DelicateKotest::class)
fun generateLanguages(
    count: Int,
    forcedLanguage: String? = null,
    languageModifier: GHLanguageLinesModifier? = null,
): List<GHLanguageDao> {
    val faker = Faker()
    val languages = getGHLanguagesGenerator().distinct().let {
        MutableList(count) { _ ->
            it.next()
        }
    }.apply {
        if (forcedLanguage != null) {
            remove(forcedLanguage)
            add(forcedLanguage)
        }
    }

    val linesList = (0..<count).map { _ ->
        abs(
            faker.random
                .nextLong(
                    min = 11,
                    max = Long.MAX_VALUE
                )
        )
    }.toMutableList().also {
        it.add(
            when (languageModifier) {
                GHLanguageLinesModifier.MAX -> it.max() + 10
                GHLanguageLinesModifier.MIN -> it.min() - 10
                GHLanguageLinesModifier.INSIDE -> faker.random
                    .nextLong(
                        min = it.min() + 1,
                        max = it.max() - 1
                    )

                null -> return@also
            }
        )
    }

    val totalLines = linesList.sum()

    return languages.zip(linesList) { name, lines ->
        val weight = (lines.toFloat() / totalLines) * 100
        GHLanguageDao(name, lines, weight)
    }
}

fun getGHLanguagesColor(language: String): String {
    return (Paths.get("").toAbsolutePath().toString() + GITHUB_TEST_RESOURCE_PATH).let {
        Json.parseToJsonElement(File(it, "githubColors.json").readText()).let {
            it.jsonObject[language]
                ?.jsonObject
                ?.get("color")
                ?.jsonPrimitive
                ?.contentOrNull ?: FALLBACK_COLOR
        }
    }
}

fun getGHLanguagesGenerator(
    languageNameModifier: GHLanguageNameModifier = GHLanguageNameModifier.WITHOUT_KOTLIN
): Arb<String> {
    val languages = (Paths.get("").toAbsolutePath().toString() + GITHUB_TEST_RESOURCE_PATH).let {
        Json.parseToJsonElement(File(it, "languages.json").readText()).jsonArray.toList()
    }.map {
        it.jsonPrimitive.content
    }.toMutableList()
        .also {
            when (languageNameModifier) {
                GHLanguageNameModifier.WITHOUT_KOTLIN -> {
                    it.remove("Kotlin")
                }

                else -> Unit
            }
        }


    return arbitrary {
        languages[it.random.nextInt(languages.lastIndex)]
    }
}


fun getGHUserGenerator(): Arb<GHUser> {
    val faker = Faker()
    return arbitrary { rs ->
        GHUser(
            id = abs(faker.random.nextLong()),
            name = faker.name.nameWithMiddle(),
            url = "https://${faker.internet.domain()}",
            avatarUrl = "https://${faker.internet.domain()}"
        )
    }
}

fun getGHUserExternalGenerator(): Arb<GHUserExternal> {
    val faker = Faker()
    return arbitrary { rs ->
        mockk<GHUserExternal>().also {
            every { it.id } returns abs(rs.random.nextLong())
            every { it.login } returns faker.name.name()
            every { it.url } returns URL("https://${faker.internet.domain()}")
            every { it.avatarUrl } returns "https://${faker.internet.domain()}"
        }
    }
}