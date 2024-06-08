package com.takaotech.dashboard.repository

import com.takaotech.dashboard.model.session.TokenPair
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.ReqBuilder
import io.ktor.client.request.*

interface AuthApi {
    @GET("session/login")
    suspend fun login(
        @Header("x-nonce") hashedNonce: String,
        @ReqBuilder ext: HttpRequestBuilder.() -> Unit
    ): TokenPair

    @GET("session/signup")
    suspend fun signup(
        @Header("x-nonce") hashedNonce: String,
        @ReqBuilder ext: HttpRequestBuilder.() -> Unit
    ): TokenPair

    @GET("session/refresh")
    suspend fun refresh(
        @ReqBuilder ext: HttpRequestBuilder.() -> Unit
    ): TokenPair
}