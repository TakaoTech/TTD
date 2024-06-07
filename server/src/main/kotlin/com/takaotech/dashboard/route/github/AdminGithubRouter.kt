package com.takaotech.dashboard.route.github

import com.github.kittinunf.result.onFailure
import com.github.kittinunf.result.onSuccess
import com.takaotech.dashboard.model.github.GHRefreshStatus
import com.takaotech.dashboard.model.github.request.TagsUpdateRequest
import com.takaotech.dashboard.route.github.controller.GithubController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.kohsuke.github.GHRef
import org.koin.ktor.ext.inject
import kotlin.time.Duration.Companion.seconds

fun Route.adminGithubRouter() {
	val jobGithubRefreshMutex = Mutex()
	var jobGithubRefresh: Job? = null

	val controller by inject<GithubController>()
	val logger by inject<Logger>()

	get<AdminGithubRoute> {
		try {
			call.respond(controller.getRepository(it.category))
		} catch (ex: Exception) {
			call.respond(HttpStatusCode.BadRequest)
		}
	}

	get<AdminGithubRoute.Refresh> {
		//TODO Catch exception for correct error body

		jobGithubRefreshMutex.withLock {
			if (it.mock) {
				jobGithubRefresh = launch(Dispatchers.Default + SupervisorJob()) {
					delay(20.seconds)
					jobGithubRefresh = null
				}
				call.respond(HttpStatusCode.OK)
			} else {
				if (jobGithubRefresh != null && jobGithubRefresh?.isActive == true) {
					call.respond(HttpStatusCode.Conflict)
				} else {
					jobGithubRefresh = launch(Dispatchers.Default + SupervisorJob()) {
						try {
							controller.getStarsAndStore()
							jobGithubRefresh = null
						} catch (ex: Throwable) {
							logger.error(ex)
							jobGithubRefresh = null
						}
					}
					call.respond(HttpStatusCode.OK)
				}
			}
		}
	}

	get<AdminGithubRoute.Refresh.Cancel> {
		jobGithubRefreshMutex.withLock {
			val mJobGithubRefresh = jobGithubRefresh
			if (mJobGithubRefresh != null) {
				if (mJobGithubRefresh.isActive) {
					logger.info("Cancelling ")
					mJobGithubRefresh.cancel()
					jobGithubRefresh = null
					call.respond(HttpStatusCode.OK)
				}
			} else {
				call.respond(HttpStatusCode.NotModified)
			}
		}
	}

	get<AdminGithubRoute.Refresh.Status> {
		//TODO Catch exception for correct error body
		jobGithubRefreshMutex.withLock {
			val mJobGithubRefresh = jobGithubRefresh
			if (mJobGithubRefresh != null) {
				call.respond(GHRefreshStatus(mJobGithubRefresh.isActive))
			} else {
				call.respond(GHRefreshStatus(null))
			}
		}
	}

	get<AdminGithubRoute.Id> {
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

	post<AdminGithubRoute.Id.UpdateCategory> {
		val id = it.parent.id
		if (id == null) {
			call.respond(HttpStatusCode.BadRequest)
		} else {
			controller.updateMainCategoryAtRepository(it.parent.id, it.newCategory)
			call.respond(HttpStatusCode.OK)
		}
	}

	post<AdminGithubRoute.Id.UpdateTags> {
		val id = it.parent.id
		val tagIds = call.receive<TagsUpdateRequest>().tagIds
		if (id == null) {
			call.respond(HttpStatusCode.BadRequest)
			return@post
		}

		controller.updateRepositoryTags(id, tagIds)
			.onSuccess {
				call.respond(HttpStatusCode.OK)
			}.onFailure {
				call.respond(HttpStatusCode.InternalServerError)
			}
	}
}

