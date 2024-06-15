package com.takaotech.dashboard

import com.takaotech.dashboard.configuration.*
import com.takaotech.dashboard.plugins.*
import com.takaotech.dashboard.plugins.auth.configureAuth
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.tomcat.*

fun main() {
	embeddedServer(Tomcat, port = 8080, host = "0.0.0.0", module = Application::module)
		.start(wait = true)
}

fun Application.module() {
	//https://ktor.io/docs/connection-pooling-caching.html#connection-settings-config
	val dbConfiguration = DbConfiguration(
		url = System.getenv("DB_URL"),
		driver = System.getenv("DB_DRIVER"),
		user = System.getenv("DB_USER"),
		password = System.getenv("DB_PASSWORD")
	)

	val githubConfiguration = GithubConfiguration(
		githubToken = System.getenv("SERVER_GITHUB_TOKEN")
	)

	val credentialConfig = CredentialConfig(
		googleJwtConfig = GoogleJwtConfig(
			issuer = System.getenv("jwt.google.issuer"),
			audience = System.getenv("jwt.google.audience")
		),
		takaoJwtConfig = TakaoJwtConfig(
			version = System.getenv("jwt.takao.version").toInt(),
			secret = System.getenv("jwt.takao.secret"),
			issuer =  System.getenv("jwt.takao.issuer"),
			audience = System.getenv("jwt.takao.audience"),
			realm = System.getenv("jwt.takao.realm"),
			accessLifetime = System.getenv("jwt.takao.access.lifetime"),
			refreshLifetime = System.getenv("jwt.takao.refresh.lifetime")
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
