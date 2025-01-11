package com.takaotech.dashboard.di

import com.takaotech.dashboard.configuration.CredentialConfig
import com.takaotech.dashboard.configuration.DbConfiguration
import com.takaotech.dashboard.configuration.GithubConfiguration
import org.koin.dsl.module

fun getConfigurationModule(
    dbConfiguration: DbConfiguration,
    githubConfiguration: GithubConfiguration,
    credentialConfig: CredentialConfig,
) = module {
    single { credentialConfig }
    single { githubConfiguration }
    single { dbConfiguration.redisConfiguration }
    single { dbConfiguration.sqlDbConfiguration }
}
