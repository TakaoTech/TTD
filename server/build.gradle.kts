import java.io.FileInputStream
import java.util.*

val projectPackage: String by project

plugins {
	alias(libs.plugins.kotlinJvm)
	alias(libs.plugins.ktor)
	alias(libs.plugins.ksp)
	alias(libs.plugins.serialization)
	alias(libs.plugins.kover)
	application
}

group = projectPackage
version = "0.1.0-preview"
application {
	mainClass.set("com.takaotech.dashboard.ApplicationKt")
	applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["development"] ?: "false"}")
}

repositories {
	mavenCentral()
	maven { setUrl("https://jitpack.io") }
}

val localProps = Properties().apply {
	load(FileInputStream(File(rootProject.rootDir, "test-server.properties")))
}


tasks.withType<Test>().configureEach {
	useJUnitPlatform()

	val propertiesMap = localProps.entries.map {
		println("Test env ${it.key} ${it.value}")
		it.key as String to it.value as String
	}.toTypedArray()

	environment(*propertiesMap)
}

kotlin {
	compilerOptions {
		if (extra["development"].toString().toBoolean()) {
			freeCompilerArgs.add("-Xdebug")
		}
	}
}

dependencies {
	implementation(projects.shared)
	implementation(libs.logback)
	implementation(libs.ktor.server.core)
//    implementation(libs.ktor.server.netty)
	implementation(libs.ktor.server.host.common)
	implementation(libs.ktor.server.resources)
	implementation(libs.ktor.server.openapi)
	implementation(libs.ktor.server.swagger)
	implementation(libs.ktor.server.call.logging)
	implementation(libs.ktor.server.call.id)
	implementation(libs.ktor.server.metrics)
	implementation(libs.ktor.server.metrics.micrometer)
	implementation(libs.micrometer.registry.prometheus)
	implementation(libs.ktor.server.content.negotiation)
	implementation(libs.ktor.server.serialization)
	implementation(libs.ktor.server.tomcat)
	implementation(libs.ktor.server.auth)
	implementation(libs.ktor.server.auth.jwt)
	implementation(libs.ktor.server.sessions)
	implementation(libs.ktor.server.cors)

	implementation(libs.ktor.client)
	implementation(libs.ktor.client.cio)
	implementation(libs.ktor.client.contentnegotiation)
	testImplementation(libs.ktor.client.test)

	implementation(libs.exposed.core)
	implementation(libs.exposed.dao)
	implementation(libs.exposed.jdbc)
	implementation(libs.exposed.json)
	implementation(libs.exposed.datetime)
	implementation(libs.db.h2)
	implementation(libs.db.postgresql)
	implementation(libs.hikari)

	implementation(libs.koin.ktor)
	implementation(libs.koin.logger)
	implementation(libs.koin.annotation)
	ksp(libs.koin.compilerksp)

	implementation(libs.github.api)
	implementation(platform(libs.okhttp.bom))
	implementation(libs.okhttp.logging)
	implementation(libs.okhttp.client)

	testImplementation(libs.ktor.server.tests) {
		exclude(group = "org.hamcrest", module = "hamcrest-core")
	}
	testImplementation(libs.kotlin.test.junit)
	testImplementation(libs.kotest.runner)
	testImplementation(libs.kotest.koin)
	testImplementation(libs.kotest.extension)
	testImplementation(libs.kotest.ktor)
	testImplementation(libs.koin.test)
	testImplementation(libs.koin.junit)
	testImplementation(libs.mockk)
}

ktor {
	fatJar {

	}
}

koverReport {
	filters {
		excludes {
			//TODO Not Work this exclusion
			classes("com.takaotech.dashboard.route.github.repository.GithubClientImpl")
			packages("org.koin.ksp.generated", "com.takaotech.dashboard.di")
		}
	}

	verify {
		// verification rules for all reports
	}

	defaults {

	}
}