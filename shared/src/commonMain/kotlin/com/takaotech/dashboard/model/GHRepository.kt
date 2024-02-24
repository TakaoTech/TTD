package com.takaotech.dashboard.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GHRepository(
	@SerialName("id")
	val id: Long,
	@SerialName("name")
	val name: String,
	@SerialName("fullName")
	val fullName: String,
	@SerialName("url")
	val url: String,
	@SerialName("user")
	val user: GHUser,
	@SerialName("languages")
	val languages: Map<String, Long>,
	@SerialName("tags")
	val tags: List<Tag>,
	//TODO In teoria è obbligatorio,
	@SerialName("mainCategory")
	val mainCategory: MainCategory = MainCategory.NONE
)