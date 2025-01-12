package com.takaotech.dashboard.utils

import com.takaotech.dashboard.configuration.DbConfiguration
import com.takaotech.dashboard.configuration.RedisConfiguration
import com.takaotech.dashboard.configuration.SqlDbConfiguration
import com.takaotech.dashboard.di.getGeneralModule
import io.github.serpro69.kfaker.Faker
import io.github.serpro69.kfaker.lorem.LoremFaker
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.ktor.util.logging.*
import io.mockk.every
import io.mockk.mockk
import okio.IOException
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GHUser
import org.koin.ksp.generated.defaultModule
import java.net.URL
import java.util.*

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

fun getGHRepositoryExternalGenerator(
    ghUsers: List<GHUser>,
    mDateMock: Date,
    ownerModifier: GHFieldModifier? = null,
    languagesModifier: GHFieldModifier? = null,
    licenseModifier: GHFieldModifier? = null,
): Arb<GHRepository> {
    val faker = Faker()
    val fakerLorem = LoremFaker()
    return arbitrary { rs ->
        mockk<GHRepository>().also {
            every { it.id } returns faker.random.nextLong()
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

fun getGHUserExternalGenerator(): Arb<GHUser> {
    val faker = Faker()
    return arbitrary { rs ->
        mockk<GHUser>().also {
            every { it.id } returns rs.random.nextLong()
            every { it.login } returns faker.name.name()
            every { it.url } returns URL("https://${faker.internet.domain()}")
            every { it.avatarUrl } returns "https://${faker.internet.domain()}"
        }
    }
}