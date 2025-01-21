package com.takaotech.dashboard.utils

import com.takaotech.dashboard.configuration.SqlDbConfiguration
import com.takaotech.dashboard.model.role.TakaoRole
import com.takaotech.dashboard.route.administration.data.UserRoleTable
import com.takaotech.dashboard.route.administration.data.role.RoleEntity
import com.takaotech.dashboard.route.administration.data.role.RoleTable
import com.takaotech.dashboard.route.administration.data.session.TokenTable
import com.takaotech.dashboard.route.administration.data.user.UserTable
import com.takaotech.dashboard.route.github.data.GithubDepositoryTable
import com.takaotech.dashboard.route.github.data.GithubDepositoryTagsTable
import com.takaotech.dashboard.route.github.data.GithubUserTable
import com.takaotech.dashboard.route.github.data.TagsTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.util.logging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SchemaUtils.withDataBaseLock
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Singleton

@Singleton
class HikariDatabase(
    private val dbConfiguration: SqlDbConfiguration,
    private val logger: Logger,
) {
    lateinit var database: Database
    private lateinit var connection: HikariDataSource

    fun connect(hikariDataSource: HikariDataSource = hikari()) {
        logger.info("Initialising database")
        connection = hikariDataSource
        database = Database.connect(
            datasource = connection,
            databaseConfig = DatabaseConfig.invoke {
                keepLoadedReferencesOutOfTransaction = true
            },
        )
        setupSchema()
    }

    fun disconnect() {
        connection.close()
    }

    private fun hikari(): HikariDataSource {
        val config =
            HikariConfig().apply {
                with(dbConfiguration) {
                    driverClassName = driver
                    jdbcUrl = url
                    username = user
                    this@apply.password = password
                }
                maximumPoolSize = 3
                isAutoCommit = false
                transactionIsolation = "TRANSACTION_REPEATABLE_READ"
                validate()
            }
        return HikariDataSource(config)
    }

    private fun setupSchema() {
        transaction(database) {
            withDataBaseLock {
                SchemaUtils.createMissingTablesAndColumns(*dbTables)

                TakaoRole.entries
                    .mapNotNull {
                        if (RoleEntity.findById(it) != null) {
                            null
                        } else {
                            it
                        }
                    }.map {
                        RoleEntity.new(it) {}
                    }
            }
        }
    }

    suspend fun <T> dbExec(statement: suspend Transaction.() -> T): T =
        withContext(Dispatchers.IO) {
            newSuspendedTransaction(statement = statement)
        }
}

val dbTables =
    arrayOf(
        TagsTable,
        GithubDepositoryTable,
        GithubUserTable,
        GithubDepositoryTagsTable,
        UserTable,
        RoleTable,
        UserRoleTable,
        TokenTable,
    )
