package com.takaotech.dashboard.route.administration.controller

import com.auth0.jwt.interfaces.Payload
import com.takaotech.dashboard.model.exception.SignUpException
import com.takaotech.dashboard.model.role.TakaoRole
import com.takaotech.dashboard.models.GoogleSignUpData
import com.takaotech.dashboard.route.administration.data.user.UserEntity
import com.takaotech.dashboard.route.administration.repository.UserRepository
import com.takaotech.dashboard.utils.sha256
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import org.koin.core.annotation.Factory

@Factory
class UserController(
    private val userRepository: UserRepository,
) {
    suspend fun getUserByGoogle(id: String): UserEntity? = userRepository.getUser(id.sha256())

    suspend fun getUserById(id: String): UserEntity? = userRepository.getUser(id)

    suspend fun getUserRolesById(id: String): Set<TakaoRole>? =
        userRepository.getUserRolesById(id)?.map { it.id.value }?.toSet()

    suspend fun signUpByGoogle(payload: Payload) {
        if (payload.getClaim("email_verified").asBoolean() != true) {
            throw SignUpException.EmailNotVerified()
        }

        val email = payload.getClaim("email").asString() ?: throw SignUpException.EmailNotFound()
        if (getUserByGoogle(email) != null) throw SignUpException.UserAlreadyExists()

        val name = payload.getClaim("name").asString() ?: throw SignUpException.InvalidUserData("name")
        val picture = payload.getClaim("picture").asString() ?: throw SignUpException.InvalidUserData("picture")

        userRepository.createUser(email, name, picture)
    }

    suspend fun signUpByGoogle(json: JsonObject) {
        val data = runCatching {
            Json.decodeFromJsonElement<GoogleSignUpData>(json)
        }.getOrElse { throw SignUpException.InvalidUserData("malformed data") }

        if (!data.verifiedEmail) throw SignUpException.EmailNotVerified()
        if (getUserByGoogle(data.email) != null) throw SignUpException.UserAlreadyExists()

        userRepository.createUser(data.email, data.name, data.picture)
    }
}
