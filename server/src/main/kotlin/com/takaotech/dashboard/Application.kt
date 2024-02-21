package com.takaotech.dashboard

import com.takaotech.dashboard.configuration.CredentialConfig
import com.takaotech.dashboard.configuration.DbConfiguration
import com.takaotech.dashboard.configuration.GithubConfiguration
import com.takaotech.dashboard.configuration.SessionConfig
import com.takaotech.dashboard.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.tomcat.*
import org.jetbrains.exposed.sql.Database
import java.util.*

fun main() {
	embeddedServer(Tomcat, port = 8080, host = "0.0.0.0", module = Application::module)
		.start(wait = true)
}

fun Application.module() {
	val dbConfiguration = DbConfiguration(
		url = System.getenv("DB_URL"),
		driver = System.getenv("DB_DRIVER"),
		user = System.getenv("DB_USER"),
		password = System.getenv("DB_PASSWORD")
	)

	val githubConfiguration = GithubConfiguration(
		githubToken = System.getenv("GITHUB_TOKEN")
	)

	val credentialConfig = CredentialConfig(
		digestAlgorithm = System.getenv("auth.digest.alg"),
		digest = System.getenv("auth.digest"),
		username = System.getenv("auth.username"),
		password = System.getenv("auth.password"),
		sessionConfig = SessionConfig(
			name = System.getenv("auth.session.name"),
			realm = System.getenv("auth.session.realm")
		)
	)

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
