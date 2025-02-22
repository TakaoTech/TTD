package com.takaotech.dashboard.configuration

data class CredentialConfig(
    val googleOauth2Config: GoogleOauth2Config,
    val googleJwtConfig: GoogleJwtConfig,
    val takaoJwtConfig: TakaoJwtConfig,
)

data class GoogleOauth2Config(
    val redirectEndpoint: String,
    val clientId: String,
    val clientSecret: String,
) {
    object Keys {
        const val REDIRECT = "authentication.google.oauth2.redirect"
        const val CLIENT_ID = "authentication.google.oauth2.clientId"
        const val CLIENT_SECRET = "authentication.google.oauth2.clientSecret"
    }
}

data class GoogleJwtConfig(
    val issuer: String,
    val audience: Array<String>,
) {

    object Keys {
        const val ISSUER = "authentication.google.jwt.issuer"
        const val AUDIENCE = "authentication.google.jwt.audience"
    }

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
    val refreshLifetime: String,
) {
    object Keys {
        const val VERSION = "authentication.ttd.tjwt.version"
        const val SECRET = "authentication.ttd.tjwt.secret"
        const val ISSUER = "authentication.ttd.tjwt.issuer"
        const val AUDIENCE = "authentication.ttd.tjwt.audience"
        const val REALM = "authentication.ttd.tjwt.realm"
        const val ACCESS_LIFETIME = "authentication.ttd.tjwt.accessLifetime"
        const val REFRESH_LIFETIME = "authentication.ttd.tjwt.refreshLifetime"
    }
}
