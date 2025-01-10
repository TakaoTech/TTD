package com.takaotech.dashboard.plugins.auth

import com.takaotech.dashboard.configuration.CredentialConfig
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.util.logging.*

fun AuthenticationConfig.configureGoogleOAuth(
    logger: Logger,
    config: CredentialConfig,
    applicationHttpClient: HttpClient,
) {
    val redirects = mutableMapOf<String, String>()
    oauth("oauth-google") {
        // Configure oauth authentication
        with(config.googleOauth2Config) {
            urlProvider = { redirectEndpoint }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = clientId,
                    clientSecret = clientSecret,
                    defaultScopes =
                        listOf(
                            "https://www.googleapis.com/auth/userinfo.profile",
                            "https://www.googleapis.com/auth/userinfo.email",
                        ),
                    extraAuthParameters = listOf("access_type" to "offline"),
                    onStateCreated = { call, state ->
                        // saves new state with redirect url value
                        call.request.queryParameters["redirectUrl"]?.let {
                            redirects[state] = it
                        }
                    },
                )
            }
        }
        client = applicationHttpClient
    }
}
