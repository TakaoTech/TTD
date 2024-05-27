package com.takaotech.dashboard.route.login

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Payload
import com.takaotech.dashboard.configuration.CredentialConfig
import com.takaotech.dashboard.route.administration.controller.UserController
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import org.koin.ktor.ext.inject
import kotlin.time.Duration.Companion.minutes

fun Application.sessionRoute() {
    routing {
        val userController by inject<UserController>()
        //https://ktor.io/docs/server-jwt.html#realm
        //https://codersee.com/ktor-app-with-jwt-refresh-token-flow/
        //https://github.com/Slenkis/ktor-full-jwt
        authenticate("google") {
            val credentialConfig by inject<CredentialConfig>()
            get<SessionRoute.Login> {
                val takaoJwtConfig = credentialConfig.takaoJwtConfig
                val userPrincipal = call.principal<JWTPrincipal>()

                val userPayload = userPrincipal?.payload

                if (userPayload != null) {
                    if (userPayload.getClaim("email_verified").asBoolean() == true) {
                        val user = userController.getUserById(userPrincipal.payload.getEmail())
                        if (user != null) {
                            with(takaoJwtConfig) {
                                val token = JWT.create()
                                    .withAudience(audience)
                                    .withIssuer(issuer)
                                    .withClaim("user", user.id.value)
                                    .withExpiresAt((Clock.System.now() + 10.minutes).toJavaInstant())
                                    .sign(Algorithm.HMAC512(secret))

                                call.respond(mapOf("token" to token))

                            }
                        }

                    }
                }
                //TODO Implement Login, load user role on RAM
            }

            get<SessionRoute.Signup> {
                val newUser = call.principal<JWTPrincipal>()

                if (newUser != null) {
                    //sub
                    //email
                    //email_verified
                    //name (as display name)
                    //picture
                    with(newUser.payload) {
                        if (getClaim("email_verified").asBoolean() == true) {
                            val email = getClaim("email").asString()
                            val name = getClaim("name").asString()
                            val picture = getClaim("picture").asString()

                            userController.signUp(
                                email,
                                name,
                                picture
                            )
                        } else {
                            //TODO Exit, email not verified
                        }
                    }

                }


            }
        }
    }
}

fun Payload.getEmail() =  getClaim("email").asString()