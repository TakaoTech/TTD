package com.takaotech.dashboard.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoogleSignUpData(
    @SerialName("verified_email")
    val verifiedEmail: Boolean,
    @SerialName("email")
    val email: String,
    @SerialName("name")
    val name: String,
    @SerialName("picture")
    val picture: String
)