package com.takaotech.dashboard.route.administration.repository

import com.takaotech.dashboard.route.administration.data.role.RoleEntity
import com.takaotech.dashboard.route.administration.data.user.UserEntity
import com.takaotech.dashboard.utils.HikariDatabase
import io.ktor.util.logging.*
import org.koin.core.annotation.Factory

@Factory
class UserRepository(
    private val database: HikariDatabase,
    private val logger: Logger,
) {
    suspend fun getRoles(): List<RoleEntity> {
        return database.dbExec {
            //TODO Add mapping
            RoleEntity.all().toList()
        }
    }

    suspend fun getUsers(){
        database.dbExec {

        }
    }

    suspend fun getUserRolesById(id: String): List<RoleEntity>? {
       return database.dbExec {
            UserEntity.findById(id)?.roles?.toList()
        }
    }
}