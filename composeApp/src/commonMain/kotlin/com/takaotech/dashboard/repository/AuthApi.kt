package com.takaotech.dashboard.repository

import com.takaotech.dashboard.model.session.RefreshTokenDao
import com.takaotech.dashboard.model.session.TokenPairDao
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.ReqBuilder
import io.ktor.client.request.*

interface AuthApi {
    @GET("session/login")
    suspend fun login(
        @Header("x-nonce") hashedNonce: String,
        @ReqBuilder ext: HttpRequestBuilder.() -> Unit,
    ): TokenPairDao

    @GET("session/signup")
    suspend fun signup(
        @Header("x-nonce") hashedNonce: String,
        @ReqBuilder ext: HttpRequestBuilder.() -> Unit,
    ): TokenPairDao

    @POST(SESSION_REFRESH_PATH)
    suspend fun refresh(
        @Body refreshToken: RefreshTokenDao,
        @ReqBuilder ext: HttpRequestBuilder.() -> Unit,
    ): TokenPairDao

    companion object {
        const val SESSION_REFRESH_PATH = "session/refresh"
    }
}
