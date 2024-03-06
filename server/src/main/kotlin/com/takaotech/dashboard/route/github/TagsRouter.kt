package com.takaotech.dashboard.route.github

import com.takaotech.dashboard.model.Tag
import com.takaotech.dashboard.route.github.controller.GithubController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.tagsRoute() {
	val controller by inject<GithubController>()

	routing {
		get<GithubRoute.Tags> {
			call.respond(controller.getTags())
		}

		put<GithubRoute.Tags> {
			val newTag = call.receive<Tag>()

			try {
				controller.addTag(newTag)
				call.respond(HttpStatusCode.OK)
			} catch (ex: Exception) {
				call.respond(HttpStatusCode.InternalServerError)
			}
		}

		delete<GithubRoute.Tags> {
			try {
				controller.removeTagById(it.tagName!!)
				call.respond(HttpStatusCode.OK)
			} catch (ex: Exception) {
				call.respond(HttpStatusCode.BadRequest)
			}
		}
	}
}