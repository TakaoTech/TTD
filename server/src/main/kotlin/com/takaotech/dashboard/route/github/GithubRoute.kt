package com.takaotech.dashboard.route.github

import com.takaotech.dashboard.model.MainCategory
import com.takaotech.dashboard.route.github.controller.GithubController
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import org.koin.ktor.ext.inject

fun Application.githubRoute() {
	val controller by inject<GithubController>()
	val logger by inject<Logger>()

	routing {
		get<GithubRoute> {
			try {
				call.respond(controller.getRepository(it.category))
			} catch (ex: Exception) {
				call.respond(HttpStatusCode.BadRequest)
			}
		}

		get<GithubRoute.Refresh> {
			//TODO Catch exception for correct error body
			try {
				controller.getStarsFromZeroAndStore()

				call.respond(HttpStatusCode.OK)
			} catch (ex: Exception) {
				logger.error(ex)
				call.respond(HttpStatusCode.BadRequest)
			}
		}

		get<GithubRoute.Id> {
			try {
				val id = it.id
				if (id == null) {
					call.respond(HttpStatusCode.BadRequest)
					return@get
				}

				val repository = controller.getRepositoryById(it.id)
				if (repository == null) {
					call.respond(HttpStatusCode.NotFound)
				} else {
					call.respond(repository)
				}
			} catch (ex: Exception) {
				logger.error(ex)
				call.respond(HttpStatusCode.BadRequest)
			}
		}

		post<GithubRoute.Id.UpdateCategory> {
			val id = it.parent.id
			if (id == null) {
				call.respond(HttpStatusCode.BadRequest)
			} else {
				controller.updateMainCategoryAtRepository(it.parent.id, it.newCategory)
				call.respond(HttpStatusCode.OK)
			}
		}
	}
}

@Resource("/github")
class GithubRoute(
	val category: MainCategory? = null
) {
	@Resource("{id}")
	class Id(val parent: GithubRoute = GithubRoute(), val id: Long? = null) {
		@Resource("updateCategory")
		//TODO newCategory as query param?
		class UpdateCategory(val parent: Id = Id(), val newCategory: MainCategory)
	}

	@Resource("/refresh")
	class Refresh(val parent: GithubRoute = GithubRoute())
}