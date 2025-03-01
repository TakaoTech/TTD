package com.takaotech.dashboard.model.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class MainCategoryDto {
    @SerialName("NONE")
    NONE,

    @SerialName("KOTLIN")
    KOTLIN,

    @SerialName("SELF_HOSTED")
    SELF_HOSTED,

    @SerialName("OTHER")
    OTHER,
}
