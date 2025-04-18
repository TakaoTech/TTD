import com.codingfeline.buildkonfig.compiler.FieldSpec
import com.google.firebase.appdistribution.gradle.firebaseAppDistribution
import org.apache.http.client.utils.URIBuilder
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.net.URI
import java.net.URL

val projectPackage: String by project

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
//    alias(libs.plugins.cocoaPods)
    alias(libs.plugins.compose.jetbrains)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.skie)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.serialization)
    alias(libs.plugins.depscredit)
    alias(libs.plugins.cfu)
    alias(libs.plugins.gms)
    alias(libs.plugins.firebase.appdistribution)
}

val devMode = runCatching { getEnvProperty("development", rootProject) }.onFailure {
    println("Development variable not found, switch to false")
}.getOrNull()?.toBoolean() == true

val useLocalEndpoint = runCatching {
    getEnvProperty("USE_LOCAL_ENDPOINT", rootProject)
}.getOrNull()?.toBoolean() == true

val endpoint = getEnvProperty("ENDPOINT_URL", rootProject).let { endpointUrl ->
    if (useLocalEndpoint) {
        URIBuilder()
            .apply {
                val mEndpointUrl = URI(endpointUrl)
                port = mEndpointUrl.port
                scheme = mEndpointUrl.scheme
                host =
                    getLocalIPv4().first().also {
                        println("Local Address $it")
                    }
                path = "/"
            }.toString()
    } else {
        endpointUrl
    }
}


if (devMode) {
    println(endpoint)
}

kotlin {
//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        moduleName = "composeApp"
//        browser {
//            commonWebpackConfig {
//                outputFileName = "composeApp.js"
//            }
//        }
//        binaries.executable()
//    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
        }
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        if (devMode) {
            freeCompilerArgs.add("-Xdebug")
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
//
//    cocoapods {
//        version = "1.0"
//        summary = "Some description for a Kotlin/Native module"
//        homepage = "Link to a Kotlin/Native module homepage"
//        ios.deploymentTarget = "16.0"
//
//        // Optional properties
//        // Configure the Pod name here instead of changing the Gradle project name
//        name = "ComposeAppPod"
//
//        framework {
//            baseName = "ComposeApp"
//            isStatic = true
//        }
//
//        xcodeConfigurationToNativeBuildType["CUSTOM_DEBUG"] = NativeBuildType.DEBUG
//        xcodeConfigurationToNativeBuildType["CUSTOM_RELEASE"] = NativeBuildType.RELEASE
//    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.activity.compose)
//            implementation(libs.koin.android)

            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.googleid)
            implementation(libs.androidx.credentials.playservices)

            implementation(libs.androidx.browser)
            implementation(libs.ktor.client.okhttp)
        }

        appleMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        val commonMain by getting {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")

            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
//                implementation(compose.material3)
                implementation("org.jetbrains.compose.material3:material3:1.8.0-alpha03")
                implementation(compose.material3AdaptiveNavigationSuite)
                implementation(compose.ui)
                implementation(libs.compose.ui.adaptive)
                implementation(libs.compose.ui.adaptive.layout)
                implementation(libs.compose.ui.adaptive.navigation)
                implementation(libs.compose.ui.adaptive.windowSizeClass)
                implementation(compose.components.uiToolingPreview)
                implementation(compose.materialIconsExtended)
                implementation(compose.components.resources)
                implementation(libs.androidx.compose.navigation)
                // https://github.com/DevSrSouza/compose-icons
                implementation(projects.shared)

                implementation(libs.androidx.datastore.preferences)

                implementation(libs.kotlin.datetime)
                implementation(libs.kotlin.atomicfu)
                implementation(libs.kermit)

                implementation(libs.ktorfit.lib)
                implementation(libs.ktorfit.converter)
                implementation(libs.ktor.client.contentnegotiation)
                implementation(libs.ktor.client.serialization)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.auth)

                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(project.dependencies.platform(libs.koin.annotation.bom))
                implementation(libs.koin.core)
                implementation(libs.koin.annotation)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)
                implementation(libs.koin.compose.navigation)

                implementation(libs.depscredit.core)
                implementation(libs.depscredit.uim3)

                implementation(libs.haze)
                implementation(libs.haze.materials)

                implementation(libs.coil)
                implementation(libs.coil.ktor)
                implementation(libs.coil.compose)

                implementation("net.sergeych:mp_stools:1.4.7")

                implementation("app.cash.paging:paging-compose-common:3.3.0-alpha02-0.5.1")
                implementation("io.github.ehsannarmani:compose-charts:0.1.0")
                // implementation("app.cash.paging:paging-testing:3.3.0-alpha02-0.5.1")
            }
        }
    }
}

android {
    namespace = projectPackage
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = projectPackage
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.android.targetSdk
                .get()
                .toInt()
        versionCode = 1
        versionName = "1.0"

        manifestPlaceholders["useClearTraffic"] = URL(endpoint).protocol != "https"

        proguardFiles(file(projectDir.absolutePath + "/src/androidMain/proguard-rules.pro"))
    }

    signingConfigs {
        create("config") {
            keyAlias = getEnvProperty("KEY_ALIAS", rootProject)
            keyPassword = getEnvProperty("KEY_PASSWORD", rootProject)
            storeFile = file(rootDir.absolutePath + "/TTD-App-Keystore.jks")
            storePassword = getEnvProperty("KEYSTORE_PASSWORD", rootProject)
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
        }
        create("staging") {
            applicationIdSuffix = ".staging"
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("config")

            firebaseAppDistribution {
                artifactType = "APK"
                serviceCredentialsFile = "${project.rootDir}/firebase-distribution.json"
            }
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    dependencies {
        debugApi(compose.preview)
        debugApi(compose.uiTooling)
// 		debugImplementation(libs.compose.ui.tooling)
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")
    ksp(libs.koin.compilerksp)

//    implementation(project.dependencies.platform(libs.firebase.bom))
}

// WORKAROUND: ADD this dependsOn("kspCommonMainKotlinMetadata") instead of above dependencies
// tasks.withType<KotlinCompile<*>>().configureEach {
// 	if (name != "kspCommonMainKotlinMetadata") {
// 		dependsOn("kspCommonMainKotlinMetadata")
// 	}
// }
// afterEvaluate {
// 	tasks.filter {
// 		it.name.contains("SourcesJar", true)
// 	}?.forEach {
// 		println("SourceJarTask====>${it.name}")
// 		it.dependsOn("kspCommonMainKotlinMetadata")
// 	}
// }

buildkonfig {
    packageName = projectPackage
    objectName = "AppBuildKonfig"
    defaultConfigs {
        buildConfigField(
            FieldSpec.Type.STRING,
            "baseUrl",
            endpoint,
        )

        buildConfigField(
            FieldSpec.Type.STRING,
            "googleWebAuth",
            getEnvProperty("AUTH_GOOGLE_CLIENT_ID_ANDROID_WEB", rootProject),
        )

        buildConfigField(
            FieldSpec.Type.STRING,
            "SESSION_KEY_ALIAS",
            getEnvProperty("SESSION_KEY_ALIAS", rootProject),
        )

        buildConfigField(
            FieldSpec.Type.BOOLEAN,
            "debug",
            "true",
        )

        buildConfigField(
            FieldSpec.Type.STRING,
            "CERT_PIN1",
            getEnvProperty("CERT_PIN1", rootProject),
        )
    }
}

// compose.experimental {
//    web.application {}
// }
