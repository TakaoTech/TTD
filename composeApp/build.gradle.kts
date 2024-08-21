import com.codingfeline.buildkonfig.compiler.FieldSpec
import com.google.firebase.appdistribution.gradle.firebaseAppDistribution
import java.io.FileInputStream
import java.net.URL
import java.util.*

val projectPackage: String by project

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.serialization)
    alias(libs.plugins.depscredit)
    alias(libs.plugins.cfu)
    alias(libs.plugins.gms)
    alias(libs.plugins.firebase.appdistribution)
}

val localProps: Properties? = try {
    Properties().apply {
        load(FileInputStream(File(rootProject.rootDir, "local.properties")))
    }
} catch (ex: Exception) {
    null
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
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    compilerOptions {
        if (extra["development"].toString().toBoolean()) {
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

    sourceSets {

        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            api(compose.preview)
            api(compose.uiTooling)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)

            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.googleid)
            implementation(libs.androidx.credentials.playservices)

            implementation(libs.androidx.browser)
            implementation(libs.ktor.client.okhttp)

        }
        val commonMain by getting {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")

            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.uiToolingPreview)
                implementation(compose.materialIconsExtended)
                implementation(compose.components.resources)
                //https://github.com/DevSrSouza/compose-icons
                implementation(projects.shared)

                implementation(libs.androidx.datastore.preferences)

                implementation(libs.kotlin.datetime)
                implementation(libs.kotlin.atomicfu)
                implementation(libs.kermit)

                implementation(libs.ktorfit.lib)
                implementation(libs.ktor.client.contentnegotiation)
                implementation(libs.ktor.client.serialization)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.auth)

                implementation(libs.koin.core)
                implementation(libs.koin.annotation)

                implementation(libs.voyager.navigator)
                implementation(libs.voyager.screenmodel)
                implementation(libs.voyager.navigator.bottomsheet)
                implementation(libs.voyager.navigator.tab)
                implementation(libs.voyager.transitions)
                implementation(libs.voyager.koin)

                implementation(libs.depscredit.core)
                implementation(libs.depscredit.uim3)

                implementation(libs.haze)
                implementation(libs.haze.materials)

                implementation(libs.coil)
                implementation(libs.coil.ktor)
                implementation(libs.coil.compose)

                implementation("net.sergeych:mp_stools:1.4.7")

                implementation("app.cash.paging:paging-compose-common:3.3.0-alpha02-0.5.1")
                implementation("io.github.thechance101:chart:Beta-0.0.5")
                //implementation("app.cash.paging:paging-testing:3.3.0-alpha02-0.5.1")

            }

        }
    }
}

android {
    namespace = projectPackage
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = projectPackage
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        manifestPlaceholders["useClearTraffic"] = URL(getEnvProperty("ENDPOINT_URL")).protocol != "https"

        proguardFiles(file(projectDir.absolutePath + "/src/androidMain/proguard-rules.pro"))
    }

    signingConfigs {
        create("config") {
            keyAlias = getEnvProperty("KEY_ALIAS")
            keyPassword = getEnvProperty("KEY_PASSWORD")
            storeFile = file(rootDir.absolutePath + "/TTD-App-Keystore.jks")
            storePassword = getEnvProperty("KEYSTORE_PASSWORD")
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
    dependencies {
//		debugImplementation(libs.compose.ui.tooling)
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")
    ksp(libs.koin.compilerksp)
    ksp(libs.ktorfit.ksp)

//    implementation(project.dependencies.platform(libs.firebase.bom))
}

// WORKAROUND: ADD this dependsOn("kspCommonMainKotlinMetadata") instead of above dependencies
//tasks.withType<KotlinCompile<*>>().configureEach {
//	if (name != "kspCommonMainKotlinMetadata") {
//		dependsOn("kspCommonMainKotlinMetadata")
//	}
//}
//afterEvaluate {
//	tasks.filter {
//		it.name.contains("SourcesJar", true)
//	}?.forEach {
//		println("SourceJarTask====>${it.name}")
//		it.dependsOn("kspCommonMainKotlinMetadata")
//	}
//}

buildkonfig {
    packageName = projectPackage
    objectName = "AppBuildKonfig"
    defaultConfigs {
        buildConfigField(
            FieldSpec.Type.STRING,
            "baseUrl",
            getEnvProperty("ENDPOINT_URL")
        )

        buildConfigField(
            FieldSpec.Type.STRING,
            "googleWebAuth",
            getEnvProperty("AUTH_GOOGLE_CLIENT_ID_ANDROID_WEB")
        )

        buildConfigField(
            FieldSpec.Type.STRING,
            "SESSION_KEY_ALIAS",
            getEnvProperty("SESSION_KEY_ALIAS")
        )

        buildConfigField(
            FieldSpec.Type.BOOLEAN,
            "debug",
            "true"
        )

        buildConfigField(
            FieldSpec.Type.STRING,
            "CERT_PIN1",
            getEnvProperty("CERT_PIN1")
        )
    }
}

fun getEnvProperty(envName: String): String {
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

    localProps?.getProperty(envName).also {
        if (it != null) {
            return it
        }
    }

    throw GradleException("Missing environment variable $envName")
}

//compose.experimental {
//    web.application {}
//}