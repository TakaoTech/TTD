package com.takaotech.dashboard.route.login

import com.github.kittinunf.result.Result
import com.github.kittinunf.result.onFailure
import com.github.kittinunf.result.onSuccess
import com.takaotech.dashboard.model.session.RefreshTokenDao
import com.takaotech.dashboard.route.administration.controller.SessionController
import com.takaotech.dashboard.route.administration.controller.UserController
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.scope

fun Application.sessionRoute() {
    val sessionController by inject<SessionController>()
    routing {
        // https://ktor.io/docs/server-jwt.html#realm
        // https://codersee.com/ktor-app-with-jwt-refresh-token-flow/
        // https://github.com/Slenkis/ktor-full-jwt
        authenticate("google") {
            get<SessionRoute.Login> {
                val userPayload = call.principal<JWTPrincipal>()?.payload
                if (userPayload != null) {
                    val token = sessionController.generateTokenPairFromGoogle(userPayload)

                    call.respond(token)
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Missing Google payload for Login")
                }
            }

            get<SessionRoute.Signup> {
                val userController = call.scope.get<UserController>()

                val newUser = call.principal<JWTPrincipal>()?.payload

                if (newUser != null) {
                    userController.signUpByGoogle(newUser)

                    val token = sessionController.generateTokenPairFromGoogle(newUser)

                    call.respond(token)
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Missing Google payload for Signup")
                }
            }
        }

        post<SessionRoute.Refresh> {
            val refreshToken = call.receive<RefreshTokenDao>().refreshToken

            val refreshResult = sessionController.refreshToken(refreshToken)
            refreshResult.onSuccess {
                call.respond(it)
            }.onFailure {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }

        route("/google") {
            val applicationHttpClient =
                HttpClient {
                    install(ContentNegotiation) {
                        json()
                    }
                }

            authenticate("oauth-google") {
                get("/login") {
                    // Redirects to 'authorizeUrl' automatically
                }

                get("/callback") {
                    val userController = call.scope.get<UserController>()
                    val currentPrincipal: OAuthAccessTokenResponse.OAuth2? = call.principal()
                    // redirects home if the url is not found before authorization
                    currentPrincipal?.let { principal ->
                        principal.state?.let { state ->
                            val token = principal.accessToken

                            val userInfo =
                                applicationHttpClient
                                    .get("https://www.googleapis.com/oauth2/v2/userinfo") {
                                        headers {
                                            bearerAuth(token)
                                        }
                                    }.body<JsonElement>()

                            Result
                                .of<Unit, Exception> {
                                    userController.signUpByGoogle(userInfo.jsonObject)
                                }.onSuccess {
                                    val data = sessionController.generateTokenPairFromGoogle(userInfo.jsonObject)
                                    call.respond(data)
                                }.onFailure {
                                    val data = sessionController.generateTokenPairFromGoogle(userInfo.jsonObject)
                                    call.respond(data)
                                }
                        }
                    }
                }
            }
        }
    }
}
