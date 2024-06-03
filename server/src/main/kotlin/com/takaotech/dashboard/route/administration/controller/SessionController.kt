package com.takaotech.dashboard.route.administration.controller

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Payload
import com.takaotech.dashboard.configuration.TakaoJwtConfig
import com.takaotech.dashboard.model.session.TokenPair
import com.takaotech.dashboard.route.administration.repository.SessionRepository
import com.takaotech.dashboard.route.login.getEmail
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import org.koin.core.annotation.Single
import java.util.*
import kotlin.time.Duration

@Single
class SessionController(
    private val sessionRepository: SessionRepository,
    private val userController: UserController,
    private val takaoJwtConfig: TakaoJwtConfig
) {
    suspend fun generateTokenPair(googlePayload: Payload): TokenPair {
        if (googlePayload.getClaim("email_verified").asBoolean() == true) {
            val user = userController.getUserById(googlePayload.getEmail())
            if (user != null) {
                with(takaoJwtConfig) {
                    val accessToken = JWT.create()
                        .withAudience(audience)
                        .withIssuer(issuer)
                        .withClaim("user", user.id.value)
                        .withExpiresAt((Clock.System.now() + Duration.parse(takaoJwtConfig.accessLifetime)).toJavaInstant())
                        .sign(Algorithm.HMAC512(secret))

                    val refreshToken = generateRefreshToken()
                    sessionRepository.saveNewToken(userId = user.id.value, refreshToken = refreshToken)

                    return TokenPair(
                        accessToken = accessToken,
                        refreshToken = refreshToken
                    )

                }
            } else {
                //TODO User not found
                throw Exception()
            }
        } else {
            //TODO Email not verified
            throw Exception()
        }
    }

    suspend fun refreshToken(
        oldToken: String
    ): String {
        val isValid = sessionRepository.checkTokenIsValid(oldToken)
        if (isValid) {
            val newToken = generateRefreshToken()
            sessionRepository.saveRefreshToken(oldToken = oldToken, newToken = newToken)

            return newToken
        } else {
            //TODO Token Not valid or exired
            throw Exception()
        }
    }

    fun verifyToken(): JWTVerifier {
        //TODO Align clamis
        return JWT.require(Algorithm.HMAC512(takaoJwtConfig.secret))
            .withAudience(takaoJwtConfig.audience)
            .withIssuer(takaoJwtConfig.issuer)
            .build()
    }

    private fun generateRefreshToken() = UUID.randomUUID().toString()
}