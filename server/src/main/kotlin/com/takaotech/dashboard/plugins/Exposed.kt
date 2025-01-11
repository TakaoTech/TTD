package com.takaotech.dashboard.plugins

import com.takaotech.dashboard.utils.HikariDatabase
import com.takaotech.dashboard.utils.RedisDatabase
import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking
import org.koin.ktor.ext.get

fun Application.initExposed() {
    val database = get<HikariDatabase>()

    database.connect()

    val redisDatabase = get<RedisDatabase>()
    runBlocking {
        redisDatabase.connect()
    }
}
