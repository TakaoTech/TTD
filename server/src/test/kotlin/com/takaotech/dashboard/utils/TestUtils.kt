package com.takaotech.dashboard.utils

import com.takaotech.dashboard.configuration.DbConfiguration
import com.takaotech.dashboard.configuration.RedisConfiguration
import com.takaotech.dashboard.configuration.SqlDbConfiguration
import com.takaotech.dashboard.di.getGeneralModule
import com.takaotech.dashboard.model.github.*
import com.takaotech.dashboard.route.github.repository.GithubColorControllerImpl.Companion.FALLBACK_COLOR
import io.github.serpro69.kfaker.Faker
import io.github.serpro69.kfaker.lorem.LoremFaker
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.ktor.util.logging.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.Instant
import kotlinx.serialization.json.*
import okio.IOException
import org.kohsuke.github.GHRepository
import org.koin.ksp.generated.defaultModule
import java.io.File
import java.net.URL
import java.nio.file.Paths
import java.util.*
import kotlin.math.abs
import org.kohsuke.github.GHUser as GHUserExternal

const val GITHUB_TEST_RESOURCE_PATH = "/src/test/resources/github"

val LOGGER = KtorSimpleLogger("TestLogger")

fun getSqlDbConfiguration() =
    SqlDbConfiguration(
        url = System.getenv("DB_URL"),
        driver = System.getenv("DB_DRIVER"),
        user = System.getenv("DB_USER"),
        password = System.getenv("DB_PASSWORD"),
    )

fun getRedisConfiguration(redisURI: String) =
    RedisConfiguration(
        url = redisURI,
    )

fun getDbConfiguration(redisURI: String) =
    DbConfiguration(
        sqlDbConfiguration = getSqlDbConfiguration(),
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

enum class GHFieldModifier {
    AS_NULL,
    IOEXCEPTION,
    OTHER_EXCEPTION,
}

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
                val languagesGen = Arb.map(Arb.pair(Arb.string(), Arb.long()), 1, 10)

                when (languagesModifier) {
                    GHFieldModifier.AS_NULL -> it returns null
                    GHFieldModifier.IOEXCEPTION -> it answers {
                        throw IOException()
                    }

                    GHFieldModifier.OTHER_EXCEPTION -> it answers {
                        throw TestCustomException()
                    }

                    null -> it returns languagesGen.next()
                }
            }
        }
    }
}

fun getGHLanguagesColorsGenerator(language: String): String {
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

fun getGHLanguagesGenerator(): Arb<String> {
    val languages = (Paths.get("").toAbsolutePath().toString() + GITHUB_TEST_RESOURCE_PATH).let {
        Json.parseToJsonElement(File(it, "languages.json").readText()).jsonArray.toList()
    }

    return arbitrary {
        languages[it.random.nextInt(languages.lastIndex)].jsonPrimitive.content
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