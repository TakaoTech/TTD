package com.takaotech.dashboard.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TagDao(
	@SerialName("id")
	val id: Int,
	@SerialName("name")
	val name: String,
	@SerialName("description")
	val description: String? = null
)

@Serializable
data class TagNewDao(
	@SerialName("name")
	val name: String,
	@SerialName("description")
	val description: String? = null
)