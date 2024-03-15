package com.takaotech.dashboard.di

import com.takaotech.dashboard.configuration.CredentialConfig
import com.takaotech.dashboard.configuration.DbConfiguration
import com.takaotech.dashboard.configuration.GithubConfiguration
import com.takaotech.dashboard.utils.OkHttpLoggerAdapter
import io.ktor.server.auth.*
import io.ktor.util.*
import io.ktor.util.logging.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.exposed.sql.Database
import org.kohsuke.github.GitHubBuilder
import org.kohsuke.github.extras.okhttp3.OkHttpGitHubConnector
import org.koin.core.module.Module
import org.koin.dsl.module

fun getGeneralModule(
	log: Logger,
	dbConfiguration: DbConfiguration,
	githubConfiguration: GithubConfiguration,
	credentialConfig: CredentialConfig
): Module = module {
	single { credentialConfig }
	single { githubConfiguration }
	single { dbConfiguration }

	single<Logger> {
		log
	}

	single {
		GitHubBuilder().apply {
			withConnector(
				OkHttpGitHubConnector(
					OkHttpClient.Builder()
						.addInterceptor(HttpLoggingInterceptor(
							OkHttpLoggerAdapter(get())
						).apply {
							level = HttpLoggingInterceptor.Level.BODY
						}
						)
						.build()
				)
			)
			withOAuthToken(githubConfiguration.githubToken)
		}.build()
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