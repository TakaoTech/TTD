package com.takaotech.dashboard.utils

import com.takaotech.dashboard.configuration.RedisConfiguration
import io.github.crackthecodeabhi.kreds.commands.StringCommands
import io.github.crackthecodeabhi.kreds.connection.Endpoint
import io.github.crackthecodeabhi.kreds.connection.KredsClient
import io.github.crackthecodeabhi.kreds.connection.newClient
import io.github.crackthecodeabhi.kreds.pipeline.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Singleton

@Singleton
class RedisDatabase(
    private val redisConfiguration: RedisConfiguration,
) {
    private lateinit var mClient: KredsClient

    fun connect() {
        mClient = newClient(Endpoint.from(redisConfiguration.url))
    }

    fun sClient(): StringCommands = mClient

    suspend fun dbExec(
        statement: suspend Transaction.() -> Unit,
    ) = withContext(Dispatchers.IO) {
        mClient.transaction().apply {
            statement(this)
        }.exec()
    }

}