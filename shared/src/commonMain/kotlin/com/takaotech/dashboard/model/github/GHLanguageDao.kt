package com.takaotech.dashboard.model.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GHLanguageDao(
	@SerialName("name")
	val name: String,
	@SerialName("lines")
	val lines: Long,
	@SerialName("weight")
	val weight: Float = 0f,
	@SerialName("colorCode")
	val colorCode: String? = null
)
