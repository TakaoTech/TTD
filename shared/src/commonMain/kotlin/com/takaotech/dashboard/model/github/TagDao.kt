package com.takaotech.dashboard.model.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TagDao(
	@SerialName("id")
	val id: Int,
	@SerialName("name")
	val name: String,
	@SerialName("description")
	val description: String? = null,
	@SerialName("color")
	val color: String? = null
)

@Serializable
data class TagNewDao(
	@SerialName("name")
	val name: String,
	@SerialName("description")
	val description: String? = null,
	@SerialName("color")
	val color: String? = null
)