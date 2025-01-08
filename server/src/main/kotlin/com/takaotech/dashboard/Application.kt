package com.takaotech.dashboard

import com.takaotech.dashboard.configuration.*
import com.takaotech.dashboard.plugins.*
import com.takaotech.dashboard.plugins.auth.configureAuth
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.tomcat.jakarta.*

fun main() {
    embeddedServer(Tomcat, port = 8080, host = System.getenv("SERVER_URL"), module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    //https://ktor.io/docs/connection-pooling-caching.html#connection-settings-config
    val dbConfiguration = DbConfiguration(
        sqlDbConfiguration = SqlDbConfiguration(
            url = System.getenv("DB_URL"),
            driver = System.getenv("DB_DRIVER"),
            user = System.getenv("DB_USER"),
            password = System.getenv("DB_PASSWORD")
        ),
        redisConfiguration = RedisConfiguration(
            url = System.getenv("REDIS_URL")
        )
    )

    val githubConfiguration = GithubConfiguration(
        githubToken = System.getenv("SERVER_GITHUB_TOKEN")
    )

    val credentialConfig = CredentialConfig(
        googleOauth2Config = GoogleOauth2Config(
            redirectEndpoint = System.getenv("OAUTH_GOOGLE_REDIRECT"),
            clientId = System.getenv("OAUTH_GOOGLE_CLIENT_ID"),
            clientSecret = System.getenv("OAUTH_GOOGLE_CLIENT_SECRET"),
        ),
        googleJwtConfig = GoogleJwtConfig(
            issuer = System.getenv("JWT_GOOGLE_ISSUER"),
            audience = System.getenv("JWT_GOOGLE_AUDIENCE")
        ),
        takaoJwtConfig = TakaoJwtConfig(
            version = System.getenv("JWT_TAKAO_VERSION").toInt(),
            secret = System.getenv("JWT_TAKAO_SECRET"),
            issuer = System.getenv("JWT_TAKAO_ISSUER"),
            audience = System.getenv("JWT_TAKAO_AUDIENCE"),
            realm = System.getenv("JWT_TAKAO_REALM"),
            accessLifetime = System.getenv("JWT_TAKAO_ACCESS_LIFETIME"),
            refreshLifetime = System.getenv("JWT_TAKAO_REFRESH_LIFETIME")
        )
    )

    installCors()
    configureKoin(
        dbConfiguration = dbConfiguration,
        githubConfiguration = githubConfiguration,
        credentialConfig = credentialConfig
    )
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    initExposed()
    configureAuth()
    configureRouting()
}
