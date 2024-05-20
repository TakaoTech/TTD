package com.takaotech.dashboard.route.administration.controller

import com.takaotech.dashboard.route.administration.data.role.TakaoRole
import com.takaotech.dashboard.route.administration.repository.UserRepository
import org.koin.core.annotation.Factory

@Factory
class UserController(
    private val userRepository: UserRepository
) {

    suspend fun getUserRolesById(id: String): Set<TakaoRole>? {
        return userRepository.getUserRolesById(id)?.map { it.id.value }?.toSet()
    }
}