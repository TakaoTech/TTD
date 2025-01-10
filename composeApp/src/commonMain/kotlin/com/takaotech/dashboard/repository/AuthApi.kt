package com.takaotech.dashboard.repository

import com.takaotech.dashboard.model.session.RefreshTokenDao
import com.takaotech.dashboard.model.session.TokenPairDao
import de.jensklingenberg.ktorfit.http.*
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

    @POST("session/refresh")
    suspend fun refresh(
        @Body refreshToken: RefreshTokenDao,
        @ReqBuilder ext: HttpRequestBuilder.() -> Unit,
    ): TokenPairDao
}
