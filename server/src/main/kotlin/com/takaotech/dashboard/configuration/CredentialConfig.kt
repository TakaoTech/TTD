package com.takaotech.dashboard.configuration

data class CredentialConfig(
	val googleJwtConfig: GoogleJwtConfig,
    val takaoJwtConfig: TakaoJwtConfig
)

data class GoogleJwtConfig(
    val issuer: String,
    val audience: String,
)

data class TakaoJwtConfig(
    val version: Int,
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String,
    val accessLifetime: String,
    val refreshLifetime: String
)