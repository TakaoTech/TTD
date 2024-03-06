package com.takaotech.dashboard.utils

import com.takaotech.dashboard.configuration.CredentialConfig
import com.takaotech.dashboard.configuration.DbConfiguration
import com.takaotech.dashboard.configuration.GithubConfiguration
import com.takaotech.dashboard.configuration.SessionConfig
import com.takaotech.dashboard.di.getGeneralModule
import io.ktor.util.logging.*
import org.koin.ksp.generated.defaultModule

val LOGGER = KtorSimpleLogger("TestLogger")

fun getBaseTestKoin() = listOf(
	getGeneralModule(
		log = LOGGER,
		dbConfiguration = DbConfiguration(
			url = System.getenv("DB_URL"),
			driver = System.getenv("DB_DRIVER"),
			user = System.getenv("DB_USER"),
			password = System.getenv("DB_PASSWORD")
		),
		githubConfiguration = GithubConfiguration(
			githubToken = System.getenv("GITHUB_TOKEN")
		),
		credentialConfig = CredentialConfig(
			digestAlgorithm = System.getenv("auth.digest.alg"),
			digest = System.getenv("auth.digest"),
			username = System.getenv("auth.username"),
			password = System.getenv("auth.password"),
			sessionConfig = SessionConfig(
				name = System.getenv("auth.session.name"),
				realm = System.getenv("auth.session.realm")
			)
		)
	), defaultModule
)