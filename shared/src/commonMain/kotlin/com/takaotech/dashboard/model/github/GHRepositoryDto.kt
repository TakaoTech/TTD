package com.takaotech.dashboard.model.github

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GHRepositoriesDto(
    @SerialName("data")
    val data: List<GHRepositoryDto>,
)

@Serializable
data class GHRepositoryDto(
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
    // TODO In teoria è obbligatorio,
    @SerialName("user")
    val user: GHUserDto,
    @SerialName("languages")
    override val languages: List<GHLanguageDto>,
    @SerialName("updatedAt")
    override val updatedAt: Instant,
    @SerialName("tags")
    override val tags: List<TagDto>,
    @SerialName("mainCategory")
    val mainCategory: MainCategoryDto = MainCategoryDto.NONE,
    @SerialName("readmeUrl")
    val readmeUrl: String?,
) : GHRepositoryBaseDto()

@Serializable
data class GHRepositoryMiniDto(
    @SerialName("id")
    override val id: Long,
    @SerialName("fullName")
    override val fullName: String,
    @SerialName("license")
    override val license: String?,
    @SerialName("languages")
    override val languages: List<GHLanguageDto>,
    @SerialName("updatedAt")
    override val updatedAt: Instant,
    @SerialName("tags")
    override val tags: List<TagDto>,
) : GHRepositoryBaseDto()

@Serializable
sealed class GHRepositoryBaseDto {
    @SerialName("id")
    abstract val id: Long

    @SerialName("fullName")
    abstract val fullName: String

    @SerialName("license")
    abstract val license: String?

    @SerialName("languages")
    abstract val languages: List<GHLanguageDto>

    @SerialName("updatedAt")
    abstract val updatedAt: Instant

    @SerialName("tags")
    abstract val tags: List<TagDto>
}