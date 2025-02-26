package com.takaotech.dashboard.utils

import com.redis.testcontainers.RedisContainer
import com.takaotech.dashboard.configuration.DbConfiguration
import com.takaotech.dashboard.configuration.RedisConfiguration
import com.takaotech.dashboard.configuration.SqlDbConfiguration
import com.takaotech.dashboard.di.getGeneralModule
import com.zaxxer.hikari.HikariDataSource
import io.kotest.core.extensions.install
import io.kotest.core.spec.Spec
import io.kotest.extensions.testcontainers.ContainerExtension
import io.kotest.extensions.testcontainers.ContainerLifecycleMode
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerExtension
import io.ktor.util.logging.*
import org.koin.ksp.generated.defaultModule
import org.testcontainers.Testcontainers
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.startupcheck.IsRunningStartupCheckStrategy
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy
import org.testcontainers.utility.DockerImageName

const val GITHUB_TEST_RESOURCE_PATH = "/src/test/resources/github"
const val POSTGRESQL_DRIVER = "org.postgresql.Driver"

val LOGGER = KtorSimpleLogger("TestLogger")

fun Spec.installRedis() = install(
    ContainerExtension(
        container = RedisContainer(
            DockerImageName.parse("redis/redis-stack").withTag(RedisContainer.DEFAULT_TAG)
        ).apply {
            withStartupCheckStrategy(
                IsRunningStartupCheckStrategy()
            )
            setWaitStrategy(HostPortWaitStrategy())
            withStartupAttempts(5)
        },
        mode = ContainerLifecycleMode.Spec,
    ),
) {
    Testcontainers.exposeHostPorts(redisPort)
}

fun Spec.installPostgres() = install(
    JdbcDatabaseContainerExtension(
        container = PostgreSQLContainer("postgres").apply {
            withStartupCheckStrategy(
                IsRunningStartupCheckStrategy()
            )
            setWaitStrategy(HostPortWaitStrategy())
            withStartupAttempts(5)
        },
        mode = ContainerLifecycleMode.Spec,
    )
)

fun getSqlDbConfiguration(postgres: HikariDataSource) =
    SqlDbConfiguration(
        url = postgres.jdbcUrl,
        driver = POSTGRESQL_DRIVER,
        user = postgres.username,
        password = postgres.password,
    )

fun getRedisConfiguration(redisURI: String) =
    RedisConfiguration(
        url = redisURI,
    )

fun getDbConfiguration(
    postgres: HikariDataSource,
    redisURI: String
) = DbConfiguration(
    sqlDbConfiguration = getSqlDbConfiguration(postgres),
    redisConfiguration = getRedisConfiguration(redisURI),
)

fun getBaseTestKoin() =
    listOf(
        getGeneralModule(
            log = LOGGER,
        ),
        defaultModule,
    )

class TestCustomException : Exception()