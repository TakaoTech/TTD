package com.takaotech.dashboard.model.session

data class TokenPair(val accessToken: String, val refreshToken: String)
data class RefreshToken(val refreshToken: String)