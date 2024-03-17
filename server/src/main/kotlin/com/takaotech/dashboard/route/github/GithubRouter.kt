package com.takaotech.dashboard.route.github

import com.takaotech.dashboard.route.github.controller.GithubController
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.githubRouter() {
	val controller by inject<GithubController>()


	get<GithubRoute> {
		//TODO if page and size are null get all?
		call.respond(controller.getRepositoryMini(it.page!!, it.size!!))
	}

	get<GithubRoute.Tags> {
		call.respond(controller.getTags(it.parent.page, it.parent.size))
	}
}