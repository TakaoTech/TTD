package com.takaotech.dashboard.model.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GHUser(
	@SerialName("id")
	val id: Long,
	@SerialName("name")
	val name: String,
	@SerialName("url")
	val url: String
)