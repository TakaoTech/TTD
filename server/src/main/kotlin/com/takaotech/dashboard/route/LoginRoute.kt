package com.takaotech.dashboard.route

import com.takaotech.dashboard.plugins.UserSession
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Application.loginRoute() {
	routing {
		authenticate("takao-auth-basic") {
			get("/login") {
				val userName = call.principal<UserIdPrincipal>()?.name.toString()
				call.sessions.set(UserSession(name = userName))
			}
		}
	}
}