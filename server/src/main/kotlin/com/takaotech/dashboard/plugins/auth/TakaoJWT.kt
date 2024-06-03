package com.takaotech.dashboard.plugins.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.takaotech.dashboard.configuration.TakaoJwtConfig
import com.takaotech.dashboard.route.administration.controller.SessionController
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun AuthenticationConfig.configureTakaoJWT(
    sessionController: SessionController,
    config: TakaoJwtConfig,
) {
    //https://github.com/Slenkis/ktor-full-jwt/blob/master/src/Application.kt
    jwt("tjwt") {
        realm = config.realm
        verifier(
            sessionController.verifyToken()
        )

        validate { credential ->
            //TODO Add check?
            JWTPrincipal(credential.payload)

        }

        challenge { defaultScheme, realm ->
            call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
        }
    }
}