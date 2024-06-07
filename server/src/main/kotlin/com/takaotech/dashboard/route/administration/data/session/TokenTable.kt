package com.takaotech.dashboard.route.administration.data.session

import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

data class RefreshTokenFromDB(val userId: String, val refreshToken: String, val expiresAt: Instant)

object TokenTable: Table(){
    var userId = varchar("user", 512)
    var refreshToken = varchar("refreshToken", 300)
    var expiresAt = timestamp("expiresAt")
}


fun ResultRow.toToken() = RefreshTokenFromDB(
    this[TokenTable.userId],
    this[TokenTable.refreshToken],
    this[TokenTable.expiresAt],
)
