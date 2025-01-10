package com.takaotech.dashboard.utils

import com.takaotech.dashboard.configuration.DbConfiguration
import com.takaotech.dashboard.configuration.RedisConfiguration
import eu.vendeli.rethis.ReThis
import eu.vendeli.rethis.types.core.Url
import org.koin.core.annotation.Singleton

@Singleton
class RedisDatabase(
    dbConfiguration: DbConfiguration,
) {
    private val redisConfiguration: RedisConfiguration = dbConfiguration.redisConfiguration
    lateinit var client: ReThis
        private set

    fun connect() {
        client = ReThis(Url(redisConfiguration.url))
    }
}
