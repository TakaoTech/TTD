import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import java.util.*

fun getEnvProperty(envName: String, project: Project): String {
    (project.findProperty(envName) as? String).also {
        if (it != null) {
            return it
        }
    }

    System.getenv(envName).also {
        if (it != null) {
            return it
        }
    }

    (project.extra.get("localProps") as? Properties)
        ?.getProperty(envName).also {
            if (it != null) {
                return it
            }
        }

    throw GradleException("Missing environment variable $envName")
}