package com.takaotech.dashboard.route.github

import com.takaotech.dashboard.model.TagDao
import com.takaotech.dashboard.model.TagNewDao
import com.takaotech.dashboard.route.github.controller.GithubController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

inline fun Route.adminTagsRoute() {
	val controller by inject<GithubController>()

	get<AdminGithubRoute.Tags> {
		call.respond(controller.getTags(null, null))
	}
	get<AdminGithubRoute.Tags.Id> {
		val tag = controller.getTagById(it.id)
		if (tag != null) {
			call.respond(tag)
		} else {
			call.respond(HttpStatusCode.NotFound)
		}

	}

	put<AdminGithubRoute.Tags> {
		val newTag = call.receive<TagNewDao>()

		try {
			controller.addTag(newTag)
			call.respond(HttpStatusCode.Created)
		} catch (ex: Exception) {
			call.respond(HttpStatusCode.InternalServerError)
		}
	}

	post<AdminGithubRoute.Tags> {
		val tag = call.receive<TagDao>()

		try {
			controller.updateTag(tag)
			call.respond(HttpStatusCode.OK)
		} catch (ex: Exception) {
			call.respond(HttpStatusCode.InternalServerError)
		}
	}

	delete<AdminGithubRoute.Tags.Id> {
		try {
			controller.removeTagById(it.id)
			call.respond(HttpStatusCode.OK)
		} catch (ex: Exception) {
			call.respond(HttpStatusCode.BadRequest)
		}
	}
}