package com.takaotech.dashboard.repository.api

import com.takaotech.dashboard.model.GHRepositoryDao
import com.takaotech.dashboard.model.MainCategory
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface GHApi {

	@GET("github")
	suspend fun getRepositories(
		@Query("category") category: MainCategory? = null
	): List<GHRepositoryDao>
}