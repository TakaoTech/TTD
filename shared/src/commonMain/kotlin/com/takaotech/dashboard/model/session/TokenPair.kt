package com.takaotech.dashboard.model.session

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenPair(
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("refreshToken")
    val refreshToken: String
) {
    override fun toString(): String {
        return "$accessToken|$refreshToken"
    }

    companion object {
        fun parse(mergedToken: String): TokenPair {
            val splitToken = mergedToken.split("|")
            return TokenPair(
                splitToken[0],
                splitToken[1]
            )
        }
    }
}

@Serializable
data class RefreshToken(
    @SerialName("refreshToken")
    val refreshToken: String
)