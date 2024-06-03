package com.takaotech.dashboard.route.login

import com.auth0.jwt.interfaces.Payload
import com.takaotech.dashboard.route.administration.controller.SessionController
import com.takaotech.dashboard.route.administration.controller.UserController
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.sessionRoute() {
    routing {
        val userController by inject<UserController>()
        val sessionController by inject<SessionController>()
        //https://ktor.io/docs/server-jwt.html#realm
        //https://codersee.com/ktor-app-with-jwt-refresh-token-flow/
        //https://github.com/Slenkis/ktor-full-jwt
        authenticate("google") {
            get<SessionRoute.Login> {
                val userPrincipal = call.principal<JWTPrincipal>()
                val userPayload = userPrincipal?.payload

                if (userPayload != null) {
                    val token = sessionController.generateTokenPair(userPayload)

                    call.respond(
                        mapOf(
                            "accessToken" to token.accessToken,
                            "refreshToken" to token.refreshToken
                        )
                    )
                } else {
                    //TODO Login Error
                }
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

                            val token = sessionController.generateTokenPair(newUser.payload)

                            call.respond(
                                mapOf(
                                    "accessToken" to token.accessToken,
                                    "refreshToken" to token.refreshToken
                                )
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

fun Payload.getEmail() = getClaim("email").asString()