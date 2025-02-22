import com.redis.testcontainers.RedisContainer
import io.kotest.core.extensions.install
import io.kotest.core.spec.Spec
import io.kotest.extensions.testcontainers.ContainerExtension
import io.kotest.extensions.testcontainers.ContainerLifecycleMode
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerExtension
import org.testcontainers.Testcontainers
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.startupcheck.IsRunningStartupCheckStrategy
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy
import org.testcontainers.utility.DockerImageName

// TODO Need centralize

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
//                    withStartupCheckStrategy(
//                        IndefiniteWaitOneShotStartupCheckStrategy()
//                            .withTimeout(10.seconds.toJavaDuration())
//                    )
            // Colima Mitigation
            withStartupCheckStrategy(
                IsRunningStartupCheckStrategy()
            )
            setWaitStrategy(HostPortWaitStrategy())
            withStartupAttempts(5)
        },
        mode = ContainerLifecycleMode.Spec,
    )
)