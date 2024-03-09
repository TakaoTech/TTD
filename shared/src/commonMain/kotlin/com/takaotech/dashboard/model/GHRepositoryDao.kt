package com.takaotech.dashboard.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GHRepositoryDao(
	@SerialName("id")
	val id: Long,
	@SerialName("name")
	val name: String,
	@SerialName("fullName")
	val fullName: String,
	@SerialName("description")
	val description: String?,
	@SerialName("url")
	val url: String,
	@SerialName("license")
	val license: String?,
	@SerialName("licenseUrl")
	val licenseUrl: String?,
	//TODO In teoria è obbligatorio,
	@SerialName("user")
	val user: GHUser,
	@SerialName("languages")
	val languages: Map<String, Long>,
	@SerialName("tags")
	val tags: List<TagDao>,
	@SerialName("mainCategory")
	val mainCategory: MainCategory = MainCategory.NONE
)