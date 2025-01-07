package com.takaotech.dashboard.configuration

data class CredentialConfig(
    val googleOauth2Config: GoogleOauth2Config,
	val googleJwtConfig: GoogleJwtConfig,
    val takaoJwtConfig: TakaoJwtConfig
)

data class GoogleOauth2Config(
    val redirectEndpoint: String,
    val clientId: String,
    val clientSecret: String,
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