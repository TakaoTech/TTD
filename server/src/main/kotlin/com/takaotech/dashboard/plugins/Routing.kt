package com.takaotech.dashboard.plugins

import com.takaotech.dashboard.route.github.adminGithubRouter
import com.takaotech.dashboard.route.github.adminTagsRoute
import com.takaotech.dashboard.route.github.githubRouter
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
			adminGithubRouter()
			adminTagsRoute()
		}
		githubRouter()
	}
	loginRoute()

}

@Serializable
@Resource("/articles")
class Articles(val sort: String? = "new")
