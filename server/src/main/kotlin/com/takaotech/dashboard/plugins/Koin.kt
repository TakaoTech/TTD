package com.takaotech.dashboard.plugins

import com.takaotech.dashboard.configuration.CredentialConfig
import com.takaotech.dashboard.configuration.DbConfiguration
import com.takaotech.dashboard.configuration.GithubConfiguration
import com.takaotech.dashboard.di.getConfigurationModule
import com.takaotech.dashboard.di.getGeneralModule
import io.ktor.server.application.*
import org.koin.ksp.generated.defaultModule
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin(
    dbConfiguration: DbConfiguration,
    githubConfiguration: GithubConfiguration,
    credentialConfig: CredentialConfig,
) {
    install(Koin) {
        slf4jLogger()
        modules(
            defaultModule,
            getConfigurationModule(dbConfiguration, githubConfiguration, credentialConfig),
            getGeneralModule(log)
        )
    }
}