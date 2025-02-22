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
) {
    object Keys {
        const val URL = "databases.database.url"
        const val DRIVER = "databases.database.driver"
        const val USER = "databases.database.user"
        const val PASSWORD = "databases.database.password"
    }
}

data class RedisConfiguration(
    val url: String,
) {
    object Keys {
        const val URL = "databases.redis.url"
    }
}
