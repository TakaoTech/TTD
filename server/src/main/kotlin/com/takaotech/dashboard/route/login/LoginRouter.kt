package com.takaotech.dashboard.route.login

import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import io.ktor.server.routing.get

fun Application.sessionRoute() {
    routing {
        get<SessionRoute.Login> {

        }
    }
}