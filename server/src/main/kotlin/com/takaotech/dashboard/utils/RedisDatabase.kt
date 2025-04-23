package com.takaotech.dashboard.utils

import com.takaotech.dashboard.configuration.RedisConfiguration
import eu.vendeli.rethis.ReThis
import eu.vendeli.rethis.types.common.Url
import org.koin.core.annotation.Singleton

@Singleton
class RedisDatabase(
    dbConfiguration: RedisConfiguration,
) {
    private val redisConfiguration: RedisConfiguration = dbConfiguration
    val client: ReThis = ReThis(Url(redisConfiguration.url)) {}

    fun connect() {
        if (client.isDisconnected) {
            client.reconnect()
        }
    }

    suspend fun disconnect() {
        client.disconnect()
    }
}
