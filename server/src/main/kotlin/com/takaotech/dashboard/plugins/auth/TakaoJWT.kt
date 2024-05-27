package com.takaotech.dashboard.plugins.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.takaotech.dashboard.configuration.TakaoJwtConfig
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun AuthenticationConfig.configureTakaoJWT(
    config: TakaoJwtConfig,
) {
    jwt("tjwt") {
        realm = config.realm
        verifier(
            JWT
                .require(Algorithm.HMAC256(config.secret))
                .withAudience(config.audience)
                .withIssuer(config. issuer)
                .build()
        )

        validate { credential ->
            //TODO Add check
            JWTPrincipal(credential.payload)

        }

        challenge { defaultScheme, realm ->
            call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
        }
    }
}