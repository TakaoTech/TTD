package com.takaotech.dashboard.repository.api

import com.takaotech.dashboard.model.TakaoPaging
import com.takaotech.dashboard.model.github.GHRefreshStatusDto
import com.takaotech.dashboard.model.github.GHRepositoriesDto
import com.takaotech.dashboard.model.github.GHRepositoryDto
import com.takaotech.dashboard.model.github.MainCategoryDto
import com.takaotech.dashboard.model.github.TagDto
import com.takaotech.dashboard.model.github.TagNewDto
import com.takaotech.dashboard.model.github.request.TagsUpdateRequest
import com.takaotech.dashboard.repository.api.ApiConstant.ADMIN_URL_PREFIX
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface AdminGHApi {
    @GET("$ADMIN_URL_PREFIX/github/refresh")
    suspend fun refreshRepositories(
        @Query("mock") mock: Boolean = false,
    )

    @GET("$ADMIN_URL_PREFIX/github/refresh/status")
    suspend fun refreshRepositoriesStatus(): GHRefreshStatusDto

    @GET("$ADMIN_URL_PREFIX/github")
    suspend fun getRepositories(
        @Query("category") category: MainCategoryDto? = null,
    ): GHRepositoriesDto

    @GET("$ADMIN_URL_PREFIX/github/{id}")
    suspend fun getRepository(
        @Path("id") repositoryId: Long,
    ): GHRepositoryDto

    @POST("$ADMIN_URL_PREFIX/github/{id}/updateCategory")
    suspend fun updateRepositoryCategory(
        // TODO newCategory as query param?
        @Path("id") repositoryId: Long,
        @Query("category") category: MainCategoryDto? = null,
    )

    @POST("$ADMIN_URL_PREFIX/github/{id}/updateTags")
    suspend fun updateRepositoryTags(
        @Path("id") repositoryId: Long,
        @Body tagsUpdateRequest: TagsUpdateRequest,
    )

    @GET("$ADMIN_URL_PREFIX/github/tags")
    suspend fun getTags(): TakaoPaging<TagDto>

    @GET("$ADMIN_URL_PREFIX/github/tags/{id}")
    suspend fun getTagById(
        @Path("id") tagId: Int,
    ): TagDto

    @PUT("$ADMIN_URL_PREFIX/github/tags")
    suspend fun addTag(
        @Body tag: TagNewDto,
    )

    @POST("$ADMIN_URL_PREFIX/github/tags")
    suspend fun updateTag(
        @Body tag: TagDto,
    )
}
