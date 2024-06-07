package com.takaotech.dashboard.repository.api

import com.takaotech.dashboard.model.github.*
import com.takaotech.dashboard.model.github.request.TagsUpdateRequest
import com.takaotech.dashboard.repository.api.ApiConstant.ADMIN_URL_PREFIX
import de.jensklingenberg.ktorfit.http.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

interface AdminGHApi {

    @GET("$ADMIN_URL_PREFIX/github/refresh")
    suspend fun refreshRepositories(
        @Query("mock") mock: Boolean = true,
        @ReqBuilder ext: HttpRequestBuilder.() -> Unit
    )

    @GET("$ADMIN_URL_PREFIX/github/refresh/status")
    suspend fun refreshRepositoriesStatus(
        @ReqBuilder ext: HttpRequestBuilder.() -> Unit
    ): GHRefreshStatus

    @GET("$ADMIN_URL_PREFIX/github")
    suspend fun getRepositories(
        @Query("category") category: MainCategory? = null,
        @ReqBuilder ext: HttpRequestBuilder.() -> Unit
    ): GHRepositoriesDao

    @GET("$ADMIN_URL_PREFIX/github/{id}")
    suspend fun getRepository(@Path("id") repositoryId: Long): GHRepositoryDao

    @POST("$ADMIN_URL_PREFIX/github/{id}/updateCategory")
    suspend fun updateRepositoryCategory(
        //TODO newCategory as query param?
        @Path("id") repositoryId: Long,
        @Query("category") category: MainCategory? = null,
        @ReqBuilder ext: HttpRequestBuilder.() -> Unit
    )

    @POST("$ADMIN_URL_PREFIX/github/{id}/updateTags")
    suspend fun updateRepositoryTags(
        @Path("id") repositoryId: Long,
        @Body tagsUpdateRequest: TagsUpdateRequest,
        @ReqBuilder ext: HttpRequestBuilder.() -> Unit
    )

    @GET("$ADMIN_URL_PREFIX/github/tags")
    suspend fun getTags(
        @ReqBuilder ext: HttpRequestBuilder.() -> Unit

    ): List<TagDao>

    @GET("$ADMIN_URL_PREFIX/github/tags/{id}")
    suspend fun getTagById(
        @Path("id") tagId: Int,
        @ReqBuilder ext: HttpRequestBuilder.() -> Unit
    ): TagDao

    @PUT("$ADMIN_URL_PREFIX/github/tags")
    suspend fun addTag(
        @Body tag: TagNewDao,
        @ReqBuilder ext: HttpRequestBuilder.() -> Unit
    )

    @POST("$ADMIN_URL_PREFIX/github/tags")
    suspend fun updateTag(
        @Body tag: TagDao,
        @ReqBuilder ext: HttpRequestBuilder.() -> Unit
    )
}