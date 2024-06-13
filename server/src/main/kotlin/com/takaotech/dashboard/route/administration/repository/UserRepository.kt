package com.takaotech.dashboard.route.administration.repository

import com.takaotech.dashboard.route.administration.data.role.RoleEntity
import com.takaotech.dashboard.route.administration.data.role.TakaoRole
import com.takaotech.dashboard.route.administration.data.user.UserEntity
import com.takaotech.dashboard.utils.HikariDatabase
import com.takaotech.dashboard.utils.sha256
import io.ktor.util.logging.*
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.sql.SizedCollection
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

    suspend fun getUsers() {
        database.dbExec {

        }
    }

    suspend fun getUserRolesById(id: String): List<RoleEntity>? {
        return database.dbExec {
            UserEntity.findById(id)?.roles?.toList()
        }
    }

    suspend fun createUser(email: String, name: String, picture: String) {
        database.dbExec {
            UserEntity.new(email.sha256()) {
                this.email = email
                displayName = name
                profileImage = picture
                roles = SizedCollection(listOf(RoleEntity.findById(TakaoRole.USER)!!))
            }
        }
    }

    suspend fun getUser(id: String): UserEntity? {
        return database.dbExec {
            UserEntity.findById(id)?.load(UserEntity::roles)
        }
    }
}