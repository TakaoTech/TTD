package com.takaotech.dashboard.models

import com.takaotech.dashboard.model.github.TagDto
import com.takaotech.dashboard.model.github.TagNewDto
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
    val color: String? = null,
) {
    companion object {
        fun fromTagDto(tagDto: TagDto) = TagDao(
            id = tagDto.id,
            name = tagDto.name,
            description = tagDto.description,
            color = tagDto.color
        )
    }
}

@Serializable
data class TagNewDao(
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String? = null,
    @SerialName("color")
    val color: String? = null,
) {
    companion object {
        fun fromTagDto(tagDto: TagNewDto) = TagNewDao(
            name = tagDto.name,
            description = tagDto.description,
            color = tagDto.color
        )
    }
}