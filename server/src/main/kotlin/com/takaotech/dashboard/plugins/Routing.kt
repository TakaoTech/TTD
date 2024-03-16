package com.takaotech.dashboard.plugins

import com.takaotech.dashboard.route.github.adminGithubRoute
import com.takaotech.dashboard.route.github.adminTagsRoute
import com.takaotech.dashboard.route.loginRoute
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.Resources
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Application.configureRouting() {
	install(Resources)
	routing {
		route("/admin") {
			adminGithubRoute()
			adminTagsRoute()
		}
	}
	loginRoute()

}

@Serializable
@Resource("/articles")
class Articles(val sort: String? = "new")
