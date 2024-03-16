package com.takaotech.dashboard.repository.api

import com.takaotech.dashboard.model.GHRepositoryDao
import com.takaotech.dashboard.model.MainCategory
import com.takaotech.dashboard.model.TagDao
import com.takaotech.dashboard.model.TagNewDao
import com.takaotech.dashboard.repository.api.ApiConstant.ADMIN_URL_PREFIX
import de.jensklingenberg.ktorfit.http.*

interface AdminGHApi {

	@GET("$ADMIN_URL_PREFIX/github")
	suspend fun getRepositories(
		@Query("category") category: MainCategory? = null
	): List<GHRepositoryDao>

	@POST("$ADMIN_URL_PREFIX/github/{id}/updateCategory")
	suspend fun updateRepositoryCategory(
		//TODO newCategory as query param?
		@Path("id") repositoryId: Long,
		@Query("category") category: MainCategory? = null
	)

	@GET("$ADMIN_URL_PREFIX/github/tags")
	suspend fun getTags(): List<TagDao>

	@GET("$ADMIN_URL_PREFIX/github/tags/{id}")
	suspend fun getTagById(@Path("id") tagId: Int): TagDao

	@PUT("$ADMIN_URL_PREFIX/github/tags")
	suspend fun addTag(@Body tag: TagNewDao)

	@POST("$ADMIN_URL_PREFIX/github/tags")
	suspend fun updateTag(@Body tag: TagDao)
}