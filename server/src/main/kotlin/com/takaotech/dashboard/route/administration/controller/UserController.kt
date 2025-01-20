package com.takaotech.dashboard.route.administration.controller

import com.auth0.jwt.interfaces.Payload
import com.takaotech.dashboard.model.role.TakaoRole
import com.takaotech.dashboard.route.administration.data.user.UserEntity
import com.takaotech.dashboard.route.administration.repository.UserRepository
import com.takaotech.dashboard.utils.sha256
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonPrimitive
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
        // sub
        // email
        // email_verified
        // name (as display name)
        // picture
        with(payload) {
            if (getClaim("email_verified").asBoolean() == true) {
                // Email not found
                val email = getEmail() ?: throw Exception()

                if (getUserByGoogle(email) == null) {
                    val name = getClaim("name").asString()
                    val picture = getClaim("picture").asString()

                    userRepository.createUser(email, name, picture)
                } else {
                    // TODO User Exist, use signin flow
                    throw Exception()
                }
            } else {
                // TODO Email not verified
                throw Exception()
            }
        }
    }

    @Suppress("UnsafeCallOnNullableType")
    suspend fun signUpByGoogle(json: JsonObject) {
        if (json["verified_email"]!!.jsonPrimitive.boolean) {
            val email = json["email"]?.jsonPrimitive?.content ?: throw Exception()

            if (getUserByGoogle(email) == null) {
                val name = json["name"]!!.jsonPrimitive.content
                val picture = json["picture"]!!.jsonPrimitive.content

                userRepository.createUser(email, name, picture)
            } else {
                // TODO User Exist, use signin flow
                throw Exception()
            }
        }
    }
}
