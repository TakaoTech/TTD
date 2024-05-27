package com.takaotech.dashboard.repository

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import io.ktor.client.statement.*

interface AuthApi {
    @GET("/session/login")
    suspend fun login(
        @Header("x-nonce") hashedNonce: String,
        @Header("Authorization") googleToken: String
    ): HttpResponse

    @GET("/session/signup")
    suspend fun signup(
        @Header("x-nonce") hashedNonce: String,
        @Header("Authorization") googleToken: String
    ): HttpResponse
}