package com.takaotech.dashboard.repository.api

import com.takaotech.dashboard.model.TakaoPaging
import com.takaotech.dashboard.model.github.*
import com.takaotech.dashboard.model.github.request.TagsUpdateRequest
import com.takaotech.dashboard.repository.api.ApiConstant.ADMIN_URL_PREFIX
import de.jensklingenberg.ktorfit.http.*

interface AdminGHApi {

    @GET("$ADMIN_URL_PREFIX/github/refresh")
    suspend fun refreshRepositories(
        @Query("mock") mock: Boolean = false,
    )

    @GET("$ADMIN_URL_PREFIX/github/refresh/status")
    suspend fun refreshRepositoriesStatus(
    ): GHRefreshStatus

    @GET("$ADMIN_URL_PREFIX/github")
    suspend fun getRepositories(
        @Query("category") category: MainCategory? = null,
    ): GHRepositoriesDao

    @GET("$ADMIN_URL_PREFIX/github/{id}")
    suspend fun getRepository(@Path("id") repositoryId: Long): GHRepositoryDao

    @POST("$ADMIN_URL_PREFIX/github/{id}/updateCategory")
    suspend fun updateRepositoryCategory(
        //TODO newCategory as query param?
        @Path("id") repositoryId: Long,
        @Query("category") category: MainCategory? = null,
    )

    @POST("$ADMIN_URL_PREFIX/github/{id}/updateTags")
    suspend fun updateRepositoryTags(
        @Path("id") repositoryId: Long,
        @Body tagsUpdateRequest: TagsUpdateRequest,
    )

    @GET("$ADMIN_URL_PREFIX/github/tags")
    suspend fun getTags(): TakaoPaging<TagDao>

    @GET("$ADMIN_URL_PREFIX/github/tags/{id}")
    suspend fun getTagById(
        @Path("id") tagId: Int,
    ): TagDao

    @PUT("$ADMIN_URL_PREFIX/github/tags")
    suspend fun addTag(
        @Body tag: TagNewDao,
    )

    @POST("$ADMIN_URL_PREFIX/github/tags")
    suspend fun updateTag(
        @Body tag: TagDao,
    )
}