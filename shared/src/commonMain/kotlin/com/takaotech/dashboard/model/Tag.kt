package com.takaotech.dashboard.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Tag(
	@SerialName("name")
	val name: String,
	@SerialName("description")
	val description: String? = null
)