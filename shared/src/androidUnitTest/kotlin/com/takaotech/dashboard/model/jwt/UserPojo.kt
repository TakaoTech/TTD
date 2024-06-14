package com.takaotech.dashboard.model.jwt

import kotlinx.serialization.Serializable

@Serializable
internal data class UserPojo(private val name: String, private val id: Int)