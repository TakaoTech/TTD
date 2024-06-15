package com.takaotech.dashboard.plugins.auth

import com.auth0.jwt.interfaces.Payload
import com.takaotech.dashboard.model.role.TakaoRole
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

class TakaoJWTPrincipal(
    payload: Payload,
    val roles: Set<TakaoRole>,
) : Principal, JWTPayloadHolder(payload)
