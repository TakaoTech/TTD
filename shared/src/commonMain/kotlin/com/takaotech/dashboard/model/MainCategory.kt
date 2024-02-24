package com.takaotech.dashboard.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class MainCategory {
	@SerialName("NONE")
	NONE,

	@SerialName("KOTLIN")
	KOTLIN,

	@SerialName("SELF_HOSTED")
	SELF_HOSTED,

	@SerialName("OTHER")
	OTHER
}