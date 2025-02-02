package com.takaotech.dashboard.route.administration.controller

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Payload
import com.github.kittinunf.result.Result
import com.takaotech.dashboard.configuration.CredentialConfig
import com.takaotech.dashboard.model.exception.SessionRefreshException
import com.takaotech.dashboard.model.jwt.TAKAO_JWT_PERMISSION
import com.takaotech.dashboard.model.jwt.TAKAO_JWT_USER
import com.takaotech.dashboard.model.jwt.TAKAO_JWT_VERSION
import com.takaotech.dashboard.model.role.TakaoRole
import com.takaotech.dashboard.model.session.TokenPairDao
import com.takaotech.dashboard.route.administration.data.user.UserEntity
import com.takaotech.dashboard.route.administration.repository.SessionRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.annotation.Single
import java.util.*
import kotlin.time.Duration

@Single
class SessionController(
    private val sessionRepository: SessionRepository,
    private val userController: UserController,
    credentialConfig: CredentialConfig,
) {
    private val takaoJwtConfig = credentialConfig.takaoJwtConfig

    suspend fun generateTokenPairFromGoogle(googlePayload: Payload): TokenPairDao {
        if (googlePayload.getClaim("email_verified").asBoolean() == true) {
            val user =
                googlePayload.getEmail()?.let { userController.getUserByGoogle(it) }
                    ?: throw Exception("User not found for generate tokens")
            return generateTokenPairFromUser(user)
        } else {
            // TODO Email not verified
            throw Exception()
        }
    }

    suspend fun generateTokenPairFromGoogle(json: JsonObject): TokenPairDao {
        if (json["verified_email"]!!.jsonPrimitive.boolean) {
            val user =
                json["email"]?.jsonPrimitive?.content?.let { userController.getUserByGoogle(it) }
                    ?: throw Exception("User not found for generate tokens")
            return generateTokenPairFromUser(user)
        } else {
            // TODO Email not verified
            throw Exception()
        }
    }

    private suspend fun generateTokenPairFromUser(
        user: UserEntity,
        oldToken: String? = null,
        update: Boolean = false,
    ): TokenPairDao {
        with(takaoJwtConfig) {
            val accessToken =
                JWT
                    .create()
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .withClaim(TAKAO_JWT_VERSION, version)
                    .withClaim(TAKAO_JWT_USER, user.id.value)
                    .withClaim(
                        TAKAO_JWT_PERMISSION,
                        user.roles.toList().map { it.id.value.name },
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
                refreshToken = refreshToken,
            )
        }
    }

    fun checkJwtIsValid(tokenExpire: Instant): Boolean = tokenExpire > Clock.System.now()

    suspend fun refreshToken(oldToken: String): Result<TokenPairDao, SessionRefreshException> = Result.of {
        val isValid = sessionRepository.checkTokenIsValid(oldToken)
        if (isValid) {
            val user =
                sessionRepository.getUserIdByToken(oldToken)?.let {
                    userController.getUserById(it)
                } ?: throw SessionRefreshException("User not found for refresh tokens")

            generateTokenPairFromUser(user = user, oldToken = oldToken, update = true)
        } else {
            throw SessionRefreshException("token expired")
        }
    }

    fun verifyToken(): JWTVerifier =
        with(takaoJwtConfig) {
            JWT
                .require(Algorithm.HMAC512(takaoJwtConfig.secret))
                .withAudience(takaoJwtConfig.audience)
                .withIssuer(takaoJwtConfig.issuer)
                .withClaim(
                    TAKAO_JWT_VERSION,
                ) { claim, jwt ->
                    claim.asInt() == version
                }.withClaim(TAKAO_JWT_USER) { claim, _ ->
                    claim.asString() != null
                }.withClaim(TAKAO_JWT_PERMISSION) { claim, _ ->
                    claim.asList(TakaoRole::class.java).isNotEmpty()
                }.build()
        }

    private fun generateRefreshToken() = UUID.randomUUID().toString()
}
