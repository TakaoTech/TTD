package com.takaotech.dashboard.route.github

import com.takaotech.dashboard.route.github.controller.GithubController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.githubRouter() {
	val controller by inject<GithubController>()


	get<GithubRoute> {
		if (it.tagId != null) {
			call.respond(controller.getRepositoryByTag(it.page ?: 1, it.size ?: 1, it.tagId))
		} else {
			call.respond(controller.getRepositoryMini(it.page!!, it.size!!))
		}
	}

	get<GithubRoute.Id> {
		val repository = controller.getRepositoryById(it.id)
		if (repository != null) {
			call.respond(repository)
		} else {
			call.respond(HttpStatusCode.NotFound)
		}
	}

	get<GithubRoute.Tags> {
		call.respond(controller.getTags(it.parent.page, it.parent.size))
	}
}