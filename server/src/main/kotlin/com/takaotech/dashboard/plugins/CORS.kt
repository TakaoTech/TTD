package com.takaotech.dashboard.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.installCors() {
	install(CORS) {
		anyHost()
		allowHeader(HttpHeaders.ContentType)
	}
}