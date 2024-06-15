package com.takaotech.dashboard.route.administration.controller

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Payload
import com.takaotech.dashboard.configuration.CredentialConfig
import com.takaotech.dashboard.model.jwt.TAKAO_JWT_PERMISSION
import com.takaotech.dashboard.model.jwt.TAKAO_JWT_USER
import com.takaotech.dashboard.model.jwt.TAKAO_JWT_VERSION
import com.takaotech.dashboard.model.session.TokenPairDao
import com.takaotech.dashboard.route.administration.data.user.UserEntity
import com.takaotech.dashboard.route.administration.repository.SessionRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import org.koin.core.annotation.Single
import java.util.*
import kotlin.time.Duration

@Single
class SessionController(
    private val sessionRepository: SessionRepository,
    private val userController: UserController,
    credentialConfig: CredentialConfig
) {
    private val takaoJwtConfig = credentialConfig.takaoJwtConfig

    suspend fun generateTokenPairFromGoogle(googlePayload: Payload): TokenPairDao {
        if (googlePayload.getClaim("email_verified").asBoolean() == true) {
            val user = userController.getUserByGoogle(googlePayload.getEmail())
                ?: throw Exception("User not found for generate tokens")
            return generateTokenPairFromUser(user)
        } else {
            //TODO Email not verified
            throw Exception()
        }
    }

    private suspend fun generateTokenPairFromUser(
        user: UserEntity,
        oldToken: String? = null,
        update: Boolean = false
    ): TokenPairDao {
        with(takaoJwtConfig) {
            val accessToken = JWT.create()
                .withAudience(audience)
                .withIssuer(issuer)
                .withClaim(TAKAO_JWT_VERSION, version)
                .withClaim(TAKAO_JWT_USER, user.id.value)
                .withClaim(
                    TAKAO_JWT_PERMISSION,
                    user.roles.toList().map { it.id.value.name }
                )
                .withExpiresAt((Clock.System.now() + Duration.parse(takaoJwtConfig.accessLifetime)).toJavaInstant())
                .sign(Algorithm.HMAC512(secret))

            val refreshToken = generateRefreshToken()
            if (update) {
                check(oldToken != null)
                sessionRepository.updateRefreshToken(oldToken, refreshToken)
            } else {
                sessionRepository.saveNewToken(userId = user.id.value, refreshToken = refreshToken)
            }

            return TokenPairDao(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        }
    }

    suspend fun checkJwtIsValid(tokenExpire: Instant): Boolean {
        return tokenExpire > Clock.System.now()
    }

    suspend fun refreshToken(
        oldToken: String
    ): TokenPairDao {
        val isValid = sessionRepository.checkTokenIsValid(oldToken)
        if (isValid) {
            val user = sessionRepository.getUserIdByToken(oldToken)?.let {
                userController.getUserById(it)
            } ?: throw Exception("User not found for refresh tokens")

            return generateTokenPairFromUser(user = user, oldToken = oldToken, update = true)
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