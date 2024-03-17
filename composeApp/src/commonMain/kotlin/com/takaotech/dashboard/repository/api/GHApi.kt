package com.takaotech.dashboard.repository.api

import com.takaotech.dashboard.model.GHRepositoryMiniDao
import com.takaotech.dashboard.model.TagDao
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface GHApi {
	@GET("github")
	suspend fun getRepositories(
		@Query("page") page: Int,
		@Query("size") size: Int
	): List<GHRepositoryMiniDao>

	@GET("github/tags")
	suspend fun getTags(
		@Query("page") page: Int?,
		@Query("size") size: Int?
	): List<TagDao>
}