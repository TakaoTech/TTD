package com.takaotech.dashboard.model.session

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias AccessToken = String
typealias RefreshToken = String

@Serializable
data class TokenPairDao(
    @SerialName("accessToken")
    val accessToken: AccessToken,
    @SerialName("refreshToken")
    val refreshToken: RefreshToken,
) {
    override fun toString(): String = "$accessToken|$refreshToken"

    companion object {
        fun parse(mergedToken: String): TokenPairDao {
            val splitToken = mergedToken.split("|")
            return TokenPairDao(
                splitToken[0],
                splitToken[1],
            )
        }
    }
}

@Serializable
data class RefreshTokenDao(
    @SerialName("refreshToken")
    val refreshToken: String,
)
