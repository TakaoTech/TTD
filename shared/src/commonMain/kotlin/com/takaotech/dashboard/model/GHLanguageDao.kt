package com.takaotech.dashboard.model

import kotlinx.serialization.Serializable

@Serializable
data class GHLanguageDao(
	val name: String,
	val lines: Long,
	val weight: Float = 0f
)
