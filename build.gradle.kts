import java.io.FileInputStream
import java.util.*

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.compose.jetbrains) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.ktorfit) apply false
    alias(libs.plugins.buildkonfig) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.depscredit) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.firebase.appdistribution) apply false
    alias(libs.plugins.ktlint.gradle) apply false
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint") // Version should be inherited from parent

    // Optionally configure plugin
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("1.5.0")
        android.set(true)
        outputToConsole.set(true)
        ignoreFailures.set(true)
    }
}

val projectPackage: String by rootProject.extra { "com.takaotech.dashboard" }
val localProps: Properties? by rootProject.extra {
    try {
        Properties().apply {
            load(FileInputStream(File(rootProject.rootDir, "local.properties")))
        }
    } catch (ex: Exception) {
        null
    }
}
