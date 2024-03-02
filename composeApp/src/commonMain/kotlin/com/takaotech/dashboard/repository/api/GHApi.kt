package com.takaotech.dashboard.repository.api

import com.takaotech.dashboard.model.GHRepositoryDao
import com.takaotech.dashboard.model.MainCategory
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

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
}