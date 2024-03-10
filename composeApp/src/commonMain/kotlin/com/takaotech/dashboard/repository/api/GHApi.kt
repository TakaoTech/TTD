package com.takaotech.dashboard.repository.api

import com.takaotech.dashboard.model.GHRepositoryDao
import com.takaotech.dashboard.model.MainCategory
import com.takaotech.dashboard.model.TagDao
import com.takaotech.dashboard.model.TagNewDao
import de.jensklingenberg.ktorfit.http.*

interface GHApi {

	@GET("github")
	suspend fun getRepositories(
		@Query("category") category: MainCategory? = null
	): List<GHRepositoryDao>

	@POST("github/{id}/updateCategory")
	suspend fun updateRepositoryCategory(
		//TODO newCategory as query param?
		@Path("id") repositoryId: Long,
		@Query("category") category: MainCategory? = null
	)

	@GET("github/tags")
	suspend fun getTags(): List<TagDao>

	@GET("github/tags/{id}")
	suspend fun getTagById(@Path("id") tagId: Int): TagDao

	@PUT("github/tags")
	suspend fun addTag(@Body tag: TagNewDao)

	@POST("github/tags")
	suspend fun updateTag(@Body tag: TagDao)
}