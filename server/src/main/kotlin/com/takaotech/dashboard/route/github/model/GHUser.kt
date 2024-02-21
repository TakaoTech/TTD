package com.takaotech.dashboard.route.github.model

import kotlinx.serialization.Serializable

@Serializable
data class GHUser(
	val id: Long,
	val name: String,
	val url: String
)