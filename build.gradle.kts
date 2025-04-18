import java.io.FileInputStream
import java.util.*

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.compose.jetbrains) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.skie) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.cocoaPods) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.ktorfit) apply false
    alias(libs.plugins.buildkonfig) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.depscredit) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.firebase.appdistribution) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.cfu)
}

//detekt {
//    source.setFrom(
//        "composeApp/src/commonMain/kotlin",
//        "composeApp/src/androidMain/kotlin",
//        "shared/src/commonMain/kotlin",
//        "server/src/main/kotlin",
//    )
//
//    config.setFrom("$projectDir/detekt.yml")
//    ignoreFailures = true
//
//    ignoredBuildTypes = listOf("release")
//    basePath = projectDir.absolutePath
//}


//val detekt by configurations.creating

//val detektTask = tasks.register<JavaExec>("detekt") {
//    mainClass = "io.gitlab.arturbosch.detekt.cli.Main"
//    classpath = detekt
//
//    val input = projectDir
//    val config = "$projectDir/detekt.yml"
//    val exclude = ".*/build/*,.*/resources/*"
//    val params = listOf("-i", input, "-c", config, "-ex", exclude)
//
//    args(params)
//}
//
//dependencies {
//    detekt("io.gitlab.arturbosch.detekt:detekt-cli:1.23.7")
//}


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
