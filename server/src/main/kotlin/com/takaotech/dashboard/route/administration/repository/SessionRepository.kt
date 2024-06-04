package com.takaotech.dashboard.route.administration.repository

import com.takaotech.dashboard.configuration.CredentialConfig
import com.takaotech.dashboard.configuration.TakaoJwtConfig
import com.takaotech.dashboard.route.administration.data.session.TokenTable
import com.takaotech.dashboard.route.administration.data.session.toToken
import com.takaotech.dashboard.utils.HikariDatabase
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Factory
import kotlin.time.Duration

@Factory
class SessionRepository(
    private val database: HikariDatabase,
    credentialConfig: CredentialConfig
) {
    private val takaoJwtConfig = credentialConfig.takaoJwtConfig

    suspend fun saveNewToken(
        userId: String,
        refreshToken: String
    ) {
        database.dbExec {
            TokenTable.insert {
                it[TokenTable.userId] = userId
                it[TokenTable.refreshToken] = refreshToken
                it[expiresAt] = Clock.System.now().plus(Duration.parse(takaoJwtConfig.refreshLifetime))
            }
        }
    }

    suspend fun saveRefreshToken(
        oldToken: String,
        newToken: String
    ) {
        database.dbExec {
            TokenTable.update({ TokenTable.refreshToken eq oldToken }) {
                it[refreshToken] = newToken
                it[expiresAt] = Clock.System.now().plus(Duration.parse(takaoJwtConfig.refreshLifetime))
            }
        }
    }

    suspend fun checkTokenIsValid(
        refreshToken: String
    ): Boolean {
        val token = database.dbExec {
            TokenTable.select { TokenTable.refreshToken eq refreshToken }
                .map { it.toToken() }.first()
        }

        return token.expiresAt > Clock.System.now()
    }
}