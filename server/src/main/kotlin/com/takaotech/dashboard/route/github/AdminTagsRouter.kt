package com.takaotech.dashboard.route.github

import com.takaotech.dashboard.model.github.TagDto
import com.takaotech.dashboard.model.github.TagNewDto
import com.takaotech.dashboard.models.TagDao
import com.takaotech.dashboard.models.TagNewDao
import com.takaotech.dashboard.route.github.controller.GithubController
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.adminTagsRoute() {
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
        val newTag = call.receive<TagNewDto>()

        try {
            controller.addTag(TagNewDao.fromTagDto(newTag))
            call.respond(HttpStatusCode.Created)
        } catch (ex: Exception) {
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    post<AdminGithubRoute.Tags> {
        val tag = call.receive<TagDto>()

        try {
            controller.updateTag(TagDao.fromTagDto(tag))
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
