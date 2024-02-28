package com.takaotech.dashboard.utils

import com.takaotech.dashboard.configuration.DbConfiguration
import com.takaotech.dashboard.route.github.data.GithubDepositoryTable
import com.takaotech.dashboard.route.github.data.GithubDepositoryTagsTable
import com.takaotech.dashboard.route.github.data.GithubUserTable
import com.takaotech.dashboard.route.github.data.TagsTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Singleton

@Singleton
class HikariDatabase(
	private val dbConfiguration: DbConfiguration
) {
	lateinit var database: Database

	fun connect() {
		//log.info("Initialising database")
		val pool = hikari()
		database = Database.connect(pool)
		setupSchema()
	}

	private fun hikari(): HikariDataSource {
		val config = HikariConfig().apply {
			driverClassName = dbConfiguration.driver
			jdbcUrl = dbConfiguration.url
			maximumPoolSize = 3
			isAutoCommit = false
			transactionIsolation = "TRANSACTION_REPEATABLE_READ"
			validate()
		}
		return HikariDataSource(config)
	}

	private fun setupSchema() {
		transaction(database) {
			SchemaUtils.create(*dbTables)
		}
	}

	suspend fun <T> dbExec(
		statement: suspend Transaction.() -> T
	): T = withContext(Dispatchers.IO) {
		newSuspendedTransaction(statement = statement)
	}
}

val dbTables = arrayOf(TagsTable, GithubDepositoryTable, GithubUserTable, GithubDepositoryTagsTable)
