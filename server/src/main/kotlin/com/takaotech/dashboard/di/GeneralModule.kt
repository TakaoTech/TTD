package com.takaotech.dashboard.di

import com.takaotech.dashboard.configuration.CredentialConfig
import com.takaotech.dashboard.configuration.DbConfiguration
import com.takaotech.dashboard.configuration.GithubConfiguration
import io.ktor.server.auth.*
import io.ktor.util.*
import io.ktor.util.logging.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.kohsuke.github.GitHubBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

fun getGeneralModule(
	log: Logger,
	dbConfiguration: DbConfiguration,
	githubConfiguration: GithubConfiguration,
	credentialConfig: CredentialConfig
): Module = module {
	single { credentialConfig }

	single<Logger> {
		log
	}

	single {
		GitHubBuilder().apply {
			withOAuthToken(githubConfiguration.githubToken)
		}.build()
	}

	single<Database> {
		runBlocking {
			connectToDatabase(dbConfiguration)
		}
	}

	single<UserHashedTableAuth> {
		val digestFunction = getDigestFunction(credentialConfig.digestAlgorithm) { credentialConfig.digest + it.length }

		UserHashedTableAuth(
			table = mapOf(
				credentialConfig.username to digestFunction(credentialConfig.password),
			),
			digester = digestFunction
		)
	}
}

fun connectToDatabase(dbConfiguration: DbConfiguration): Database {
	return Database.connect(dbConfiguration.url, dbConfiguration.driver)
}