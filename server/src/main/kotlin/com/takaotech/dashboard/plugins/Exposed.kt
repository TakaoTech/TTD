package com.takaotech.dashboard.plugins

import com.takaotech.dashboard.utils.HikariDatabase
import io.ktor.server.application.*
import org.koin.ktor.ext.get

fun Application.initExposed() {
	val database = get<HikariDatabase>()

	database.connect()

}