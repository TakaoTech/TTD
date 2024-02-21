package com.takaotech.dashboard.plugins

import com.takaotech.dashboard.configuration.CredentialConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject
import kotlin.time.Duration.Companion.days

fun Application.configureAuth(credentialConfig: CredentialConfig = get()) {
	val userTable by inject<UserHashedTableAuth>()
	install(Sessions) {
		cookie<UserSession>(credentialConfig.sessionConfig.name) {
			cookie.path = "/"
			cookie.maxAge = 30.days
		}
	}

	authentication {
		basic("takao-auth-basic") {
			realm = "TTD Admin"
			validate { credentials ->
				userTable.authenticate(credentials)
			}
		}

		session<UserSession>(credentialConfig.sessionConfig.name) {
			validate { session ->
				if (session.name.startsWith(credentialConfig.sessionConfig.name)) {
					session
				} else {
					null
				}
			}
			challenge {
				call.respondRedirect("/login")
			}
		}
	}
}

data class UserSession(val name: String) : Principal
