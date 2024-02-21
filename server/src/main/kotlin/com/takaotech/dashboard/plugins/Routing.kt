package com.takaotech.dashboard.plugins

import com.takaotech.dashboard.route.github.githubRoute
import com.takaotech.dashboard.route.loginRoute
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.resources.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Application.configureRouting() {
	install(Resources)
	githubRoute()
	loginRoute()
}

@Serializable
@Resource("/articles")
class Articles(val sort: String? = "new")
