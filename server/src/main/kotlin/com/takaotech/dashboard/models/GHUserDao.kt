package com.takaotech.dashboard.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GHUserDao(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("url")
    val url: String,
    @SerialName("avatarUrl")
    val avatarUrl: String?,
)