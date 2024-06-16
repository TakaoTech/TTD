package com.takaotech.dashboard.plugins.auth

import com.auth0.jwt.interfaces.Payload
import com.takaotech.dashboard.model.jwt.TAKAO_JWT_PERMISSION
import com.takaotech.dashboard.model.jwt.TAKAO_JWT_USER
import com.takaotech.dashboard.model.role.TakaoRole
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

class TakaoJWTPrincipal(
    payload: Payload,
    val user: String = payload.getClaim(TAKAO_JWT_USER).asString(),
    val roles: Set<TakaoRole> = payload
        .getClaim(TAKAO_JWT_PERMISSION)
        .asList(TakaoRole::class.java)
        .toSet(),
) : Principal, JWTPayloadHolder(payload)