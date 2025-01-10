package com.takaotech.dashboard.model.jwt

import com.takaotech.dashboard.model.role.TakaoRole
import com.takaotech.dashboard.model.session.AccessToken
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

class TakaoSession(
    json: Json,
    accessToken: AccessToken,
) {
    val roles: List<TakaoRole>
    val version: Int

    init {
        JWT(accessToken).also { jwt ->
            roles = jwt.getClaim(TAKAO_JWT_PERMISSION)?.let {
                json.decodeFromJsonElement<List<TakaoRole>>(it.value)
            } ?: listOf()

            version = jwt.getClaim(TAKAO_JWT_VERSION)?.asInt()!!
        }
    }
}

const val TAKAO_JWT_VERSION = "version"
const val TAKAO_JWT_USER = "user"
const val TAKAO_JWT_PERMISSION = "permission"
