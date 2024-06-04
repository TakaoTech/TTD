package com.takaotech.dashboard.route.administration.controller

import com.auth0.jwt.interfaces.Payload
import com.takaotech.dashboard.route.administration.data.role.TakaoRole
import com.takaotech.dashboard.route.administration.data.user.UserEntity
import com.takaotech.dashboard.route.administration.repository.UserRepository
import com.takaotech.dashboard.utils.sha256
import org.koin.core.annotation.Factory

@Factory
class UserController(
    private val userRepository: UserRepository
) {

    suspend fun getUserById(id: String): UserEntity? {
        return userRepository.getUser(id.sha256())
    }

    suspend fun getUserRolesById(id: String): Set<TakaoRole>? {
        return userRepository.getUserRolesById(id)?.map { it.id.value }?.toSet()
    }

    suspend fun signUp(payload: Payload) {
        //sub
        //email
        //email_verified
        //name (as display name)
        //picture
        with(payload) {
            if (getClaim("email_verified").asBoolean() == true) {
                val email = getEmail()
                val name = getClaim("name").asString()
                val picture = getClaim("picture").asString()

                userRepository.createUser(email, name, picture)
            }else{
                //TODO Email not verified
                throw Exception()
            }
        }
    }
}