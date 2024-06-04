package com.takaotech.dashboard.plugins.auth

import com.takaotech.dashboard.configuration.CredentialConfig
import com.takaotech.dashboard.route.administration.controller.SessionController
import com.takaotech.dashboard.route.administration.controller.UserController
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject

fun Application.configureAuth(credentialConfig: CredentialConfig = get()) {
	//https://gist.github.com/nomisRev/a42110d095e3fd2c82d8137c995569b1
	//https://github.com/desmondtzq/ktor-auth-firebase/tree/master/library

	//https://ktor.io/docs/server-oauth.html#flow



//	val applicationHttpClient = HttpClient {
//		install(ContentNegotiation) {
//			json()
//		}
//	}

//	val redirects = mutableMapOf<String, String>()
	val sessionController by inject<SessionController>()

	authentication {
		// and then api key provider
		configureGoogleJWT(credentialConfig)
		configureTakaoJWT(
			sessionController,
			credentialConfig.takaoJwtConfig
		)
	}

	//https://github.com/santansarah/ktor-city-api/blob/google-one-tap/src/main/kotlin/com/santansarah/plugins/JWT.kt
	//https://www.youtube.com/watch?v=Q7PgQdXfETU
//	authentication {
//		oauth("auth-oauth-google") {
//			urlProvider = { "http://localhost:8080/callback" }
//			providerLookup = {
//				OAuthServerSettings.OAuth2ServerSettings(
//					name = "google",
//					authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
//					accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
//					requestMethod = HttpMethod.Post,
//					clientId = System.getenv("AUTH_GOOGLE_CLIENT_ID"),
//					clientSecret = System.getenv("AUTH_GOOGLE_CLIENT_SECRET"),
//					defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile"),
//					extraAuthParameters = listOf("access_type" to "offline"),
//					onStateCreated = { call, state ->
//						//saves new state with redirect url value
//						call.request.queryParameters["redirectUrl"]?.let {
//							redirects[state] = it
//						}
//					}
//				)
//			}
//			client = applicationHttpClient
//		}
//	}
//
//	routing {
//		authenticate("auth-oauth-google") {
//			get("/callback") {
//				val currentPrincipal: OAuthAccessTokenResponse.OAuth2? = call.principal()
//				// redirects home if the url is not found before authorization
//				currentPrincipal?.let { principal ->
//					principal.state?.let { state ->
//						call.sessions.set(UserSession(state, principal.accessToken, "help"))
//						redirects[state]?.let { redirect ->
//							call.respondRedirect(redirect)
//							return@get
//						}
//					}
//				}
//				call.respondRedirect("/home")
//			}
//		}
//
//	}
}

//data class UserSession(val state: String, val token: String, val test: String)