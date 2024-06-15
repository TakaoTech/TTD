package com.takaotech.dashboard.utils

import com.takaotech.dashboard.configuration.*
import com.takaotech.dashboard.di.getGeneralModule
import io.ktor.util.logging.*
import org.koin.ksp.generated.defaultModule

val LOGGER = KtorSimpleLogger("TestLogger")

fun getDbConfiguration() = DbConfiguration(
    url = System.getenv("DB_URL"),
    driver = System.getenv("DB_DRIVER"),
    user = System.getenv("DB_USER"),
    password = System.getenv("DB_PASSWORD")
)

fun getBaseTestKoin() = listOf(
    getGeneralModule(
        log = LOGGER,
        dbConfiguration = getDbConfiguration(),
        githubConfiguration = GithubConfiguration(
            githubToken = System.getenv("SERVER_GITHUB_TOKEN")
        ),
        credentialConfig = CredentialConfig(
            googleJwtConfig = GoogleJwtConfig(
                issuer = System.getenv("jwt.google.issuer"),
                audience = System.getenv("jwt.google.audience")
            ),
            takaoJwtConfig = TakaoJwtConfig(
                version = System.getenv("jwt.takao.version").toInt(),
                secret = System.getenv("jwt.takao.secret"),
                issuer = System.getenv("jwt.takao.issuer"),
                audience = System.getenv("jwt.takao.audience"),
                realm = System.getenv("jwt.takao.realm"),
                accessLifetime = System.getenv("jwt.takao.access.lifetime"),
                refreshLifetime = System.getenv("jwt.takao.refresh.lifetime")
            )
        )
    ), defaultModule
)