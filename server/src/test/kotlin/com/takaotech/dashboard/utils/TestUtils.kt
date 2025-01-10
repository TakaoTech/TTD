package com.takaotech.dashboard.utils

import com.takaotech.dashboard.configuration.DbConfiguration
import com.takaotech.dashboard.configuration.RedisConfiguration
import com.takaotech.dashboard.configuration.SqlDbConfiguration
import com.takaotech.dashboard.di.getGeneralModule
import io.ktor.util.logging.*
import org.koin.ksp.generated.defaultModule

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
