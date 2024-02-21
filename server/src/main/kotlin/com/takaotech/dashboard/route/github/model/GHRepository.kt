package com.takaotech.dashboard.route.github.model

import kotlinx.serialization.Serializable

@Serializable
data class GHRepository(
	val id: Long,
	val name: String,
	val fullName: String,
	val url: String,
	val user: GHUser,
	val languages: Map<String, Long>
)