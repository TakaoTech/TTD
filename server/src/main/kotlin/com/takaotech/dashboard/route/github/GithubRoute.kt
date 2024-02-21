package com.takaotech.dashboard.route.github

import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Application.githubRoute() {
	routing {
		get<GithubRoute>() {
			call.respond(HttpStatusCode.OK, "Ciao")
		}
	}
}

@Serializable
@Resource("/github")
class GithubRoute()