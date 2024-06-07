package com.takaotech.dashboard.model.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GHRefreshStatus(
    @SerialName("active")
    val active: Boolean?
)