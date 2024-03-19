package com.takaotech.dashboard.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GHRepositoryDao(
	@SerialName("id")
	override val id: Long,
	@SerialName("name")
	val name: String,
	@SerialName("fullName")
	override val fullName: String,
	@SerialName("description")
	val description: String?,
	@SerialName("url")
	val url: String,
	@SerialName("license")
	override val license: String?,
	@SerialName("licenseUrl")
	val licenseUrl: String?,
	//TODO In teoria è obbligatorio,
	@SerialName("user")
	val user: GHUser,
	@SerialName("languages")
	override val languages: List<GHLanguageDao>,
	@SerialName("updatedAt")
	override val updatedAt: Instant,
	@SerialName("tags")
	override val tags: List<TagDao>,
	@SerialName("mainCategory")
	val mainCategory: MainCategory = MainCategory.NONE
) : GHRepositoryBaseDao()

@Serializable
data class GHRepositoryMiniDao(
	@SerialName("id")
	override val id: Long,
	@SerialName("fullName")
	override val fullName: String,
	@SerialName("license")
	override val license: String?,
	@SerialName("languages")
	override val languages: List<GHLanguageDao>,
	@SerialName("updatedAt")
	override val updatedAt: Instant,
	@SerialName("tags")
	override val tags: List<TagDao>,
) : GHRepositoryBaseDao()


@Serializable
sealed class GHRepositoryBaseDao {
	@SerialName("id")
	abstract val id: Long

	@SerialName("fullName")
	abstract val fullName: String

	@SerialName("license")
	abstract val license: String?

	@SerialName("languages")
	abstract val languages: List<GHLanguageDao>

	@SerialName("updatedAt")
	abstract val updatedAt: Instant

	@SerialName("tags")
	abstract val tags: List<TagDao>
}