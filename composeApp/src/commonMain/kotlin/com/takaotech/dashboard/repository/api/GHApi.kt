package com.takaotech.dashboard.repository.api

import com.takaotech.dashboard.model.TakaoPaging
import com.takaotech.dashboard.model.github.GHRepositoryDto
import com.takaotech.dashboard.model.github.GHRepositoryMiniDto
import com.takaotech.dashboard.model.github.TagDto
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface GHApi {
    @GET("github")
    suspend fun getRepositories(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("tagId") tagId: Int?,
    ): TakaoPaging<GHRepositoryMiniDto>

    @GET("github/{id}")
    suspend fun getRepository(
        @Path("id") id: Long,
    ): GHRepositoryDto

    @GET("github/tags")
    suspend fun getTags(
        @Query("page") page: Int?,
        @Query("size") size: Int?,
    ): TakaoPaging<TagDto>
}
