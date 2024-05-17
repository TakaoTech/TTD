package com.takaotech.dashboard.configuration

data class CredentialConfig(
	val googleJwtConfig: GoogleJwtConfig
)

data class GoogleJwtConfig(
    val issuer: String,
    val audience: String,
)