package com.takaotech.dashboard.configuration

data class DbConfiguration(
    val sqlDbConfiguration: SqlDbConfiguration,
    val redisConfiguration: RedisConfiguration,
)

data class SqlDbConfiguration(
    val url: String,
    val driver: String,
    val user: String = "",
    val password: String = "",
)

data class RedisConfiguration(
    val url: String,
)
