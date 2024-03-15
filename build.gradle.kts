plugins {
	// this is necessary to avoid the plugins to be loaded multiple times
	// in each subproject's classloader
	alias(libs.plugins.androidApplication) apply false
	alias(libs.plugins.androidLibrary) apply false
	alias(libs.plugins.jetbrainsCompose) apply false
	alias(libs.plugins.kotlinJvm) apply false
	alias(libs.plugins.kotlinMultiplatform) apply false
	alias(libs.plugins.ksp) apply false
	alias(libs.plugins.serialization) apply false
	alias(libs.plugins.ktorfit) apply false
	alias(libs.plugins.buildkonfig) apply false
	alias(libs.plugins.kover) apply false
	alias(libs.plugins.depscredit) apply false

}

val projectPackage: String by rootProject.extra { "com.takaotech.dashboard" }
