package com.takaotech.dashboard.plugins

import com.takaotech.dashboard.model.role.TakaoRole
import com.takaotech.dashboard.plugins.auth.withAnyRole
import com.takaotech.dashboard.route.github.adminGithubRouter
import com.takaotech.dashboard.route.github.adminTagsRoute
import com.takaotech.dashboard.route.github.githubRouter
import com.takaotech.dashboard.route.login.sessionRoute
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(Resources)
    routing {
        route("/admin") {
            authenticate("tjwt") {
                withAnyRole(TakaoRole.ADMINISTRATOR) {
                    adminGithubRouter()
                    adminTagsRoute()
                }
            }
        }
        githubRouter()
    }
    sessionRoute()

}
