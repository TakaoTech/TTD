package com.takaotech.dashboard.di

import com.takaotech.dashboard.configuration.CredentialConfig
import com.takaotech.dashboard.configuration.DbConfiguration
import com.takaotech.dashboard.configuration.GithubConfiguration
import com.takaotech.dashboard.configuration.GoogleJwtConfig
import com.takaotech.dashboard.configuration.GoogleOauth2Config
import com.takaotech.dashboard.configuration.RedisConfiguration
import com.takaotech.dashboard.configuration.SqlDbConfiguration
import com.takaotech.dashboard.configuration.TakaoJwtConfig
import io.ktor.server.application.*
import org.koin.dsl.module

fun Application.getConfigurationModule() = module {
    val dbConfiguration = DbConfiguration(
        sqlDbConfiguration = SqlDbConfiguration(
            url = environment.config.property(SqlDbConfiguration.Keys.URL).getString(),
            driver = environment.config.property(SqlDbConfiguration.Keys.DRIVER).getString(),
            user = environment.config.property(SqlDbConfiguration.Keys.USER).getString(),
            password = environment.config.property(SqlDbConfiguration.Keys.PASSWORD).getString(),
        ),
        redisConfiguration = RedisConfiguration(
            url = environment.config.property(RedisConfiguration.Keys.URL).getString(),
        ),
    )

    val githubConfiguration = GithubConfiguration(
        githubToken = environment.config.property(GithubConfiguration.Key.TOKEN).getString(),
    )

    val credentialConfig = CredentialConfig(
        googleOauth2Config = GoogleOauth2Config(
            redirectEndpoint = environment.config.property(GoogleOauth2Config.Keys.REDIRECT).getString(),
            clientId = environment.config.property(GoogleOauth2Config.Keys.CLIENT_ID).getString(),
            clientSecret = environment.config.property(GoogleOauth2Config.Keys.CLIENT_SECRET).getString(),
        ),
        googleJwtConfig = GoogleJwtConfig(
            issuer = environment.config.property(GoogleJwtConfig.Keys.ISSUER).getString(),
            audience = environment.config.property(GoogleJwtConfig.Keys.AUDIENCE).getString()
                .split(",")
                .toTypedArray(),
        ),
        takaoJwtConfig = TakaoJwtConfig(
            version = environment.config.property(TakaoJwtConfig.Keys.VERSION).getString().toInt(),
            secret = environment.config.property(TakaoJwtConfig.Keys.SECRET).getString(),
            issuer = environment.config.property(TakaoJwtConfig.Keys.ISSUER).getString(),
            audience = environment.config.property(TakaoJwtConfig.Keys.AUDIENCE).getString(),
            realm = environment.config.property(TakaoJwtConfig.Keys.REALM).getString(),
            accessLifetime = environment.config.property(TakaoJwtConfig.Keys.ACCESS_LIFETIME).getString(),
            refreshLifetime = environment.config.property(TakaoJwtConfig.Keys.REFRESH_LIFETIME).getString(),
        ),
    )

    single { credentialConfig }
    single { githubConfiguration }
    single { dbConfiguration.redisConfiguration }
    single { dbConfiguration.sqlDbConfiguration }
}
