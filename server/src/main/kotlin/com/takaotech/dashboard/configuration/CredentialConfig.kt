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
    val audience: Array<String>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GoogleJwtConfig

        if (issuer != other.issuer) return false
        if (!audience.contentEquals(other.audience)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = issuer.hashCode()
        result = 31 * result + audience.contentHashCode()
        return result
    }
}

data class TakaoJwtConfig(
    val version: Int,
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String,
    val accessLifetime: String,
    val refreshLifetime: String
)