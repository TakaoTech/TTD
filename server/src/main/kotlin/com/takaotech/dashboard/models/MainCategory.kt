package com.takaotech.dashboard.models

import com.takaotech.dashboard.model.github.MainCategoryDto
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
    OTHER,
}

fun MainCategoryDto.toMainCategory(): MainCategory {
    return when (this) {
        MainCategoryDto.NONE -> MainCategory.NONE
        MainCategoryDto.KOTLIN -> MainCategory.KOTLIN
        MainCategoryDto.SELF_HOSTED -> MainCategory.SELF_HOSTED
        MainCategoryDto.OTHER -> MainCategory.OTHER
    }
}