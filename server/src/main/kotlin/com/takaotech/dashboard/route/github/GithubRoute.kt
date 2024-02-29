package com.takaotech.dashboard.route.github

import com.takaotech.dashboard.model.MainCategory
import com.takaotech.dashboard.route.github.controller.GithubController
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

fun Application.githubRoute() {
	val controller by inject<GithubController>()

	routing {
		get<GithubRoute> {
			try {
				call.respond(controller.getStoredRepository(it.category))
			} catch (ex: Exception) {
				call.respond(HttpStatusCode.BadRequest)
			}
		}

		get<GithubRoute.Refresh>() {
			//TODO Catch exception for correct error body
			try {
				controller.getStarsFromZeroAndStore()

				call.respond(HttpStatusCode.OK)
			} catch (ex: Exception) {
				call.respond(HttpStatusCode.BadRequest)
			}
		}
	}
}

@Serializable
@Resource("/github")
class GithubRoute(
	val category: MainCategory? = null
) {
	@Serializable
	@Resource("/refresh")
	class Refresh(val parent: GithubRoute = GithubRoute())
}