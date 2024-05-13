package com.takaotech.dashboard.plugins.auth

import com.takaotech.dashboard.configuration.CredentialConfig
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject
import kotlin.time.Duration.Companion.days

fun Application.configureAuth(credentialConfig: CredentialConfig = get()) {
	//https://gist.github.com/nomisRev/a42110d095e3fd2c82d8137c995569b1
	//https://github.com/desmondtzq/ktor-auth-firebase/tree/master/library

	//https://ktor.io/docs/server-oauth.html#flow

	val userTable by inject<UserHashedTableAuth>()
	install(Sessions) {
		cookie<UserSession>(credentialConfig.sessionConfig.name)
	}

	val applicationHttpClient = HttpClient {
		install(ContentNegotiation) {
			json()
		}
	}

	val redirects = mutableMapOf<String, String>()

	authentication {
		oauth("auth-oauth-google") {
			urlProvider = { "http://localhost:8080/callback" }
			providerLookup = {
				OAuthServerSettings.OAuth2ServerSettings(
					name = "google",
					authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
					accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
					requestMethod = HttpMethod.Post,
					clientId = System.getenv("AUTH_GOOGLE_CLIENT_ID"),
					clientSecret = System.getenv("AUTH_GOOGLE_CLIENT_SECRET"),
					defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile"),
					extraAuthParameters = listOf("access_type" to "offline"),
					onStateCreated = { call, state ->
						//saves new state with redirect url value
						call.request.queryParameters["redirectUrl"]?.let {
							redirects[state] = it
						}
					}
				)
			}
			client = applicationHttpClient
		}
	}

	routing {
		authenticate("auth-oauth-google") {
			get("/callback") {
				val currentPrincipal: OAuthAccessTokenResponse.OAuth2? = call.principal()
				// redirects home if the url is not found before authorization
				currentPrincipal?.let { principal ->
					principal.state?.let { state ->
						call.sessions.set(UserSession(state, principal.accessToken))
						redirects[state]?.let { redirect ->
							call.respondRedirect(redirect)
							return@get
						}
					}
				}
				call.respondRedirect("/home")
			}
		}

	}
}

data class UserSession(val state: String, val token: String)