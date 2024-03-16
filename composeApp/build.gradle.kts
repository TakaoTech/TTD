import com.codingfeline.buildkonfig.compiler.FieldSpec

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

			implementation(libs.androidx.browser)
		}
		val commonMain by getting {
			kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")

			dependencies {
				implementation(compose.runtime)
				implementation(compose.foundation)
				implementation(compose.material)
				implementation(compose.material3)
				implementation(compose.ui)
				implementation(compose.components.uiToolingPreview)
				implementation(compose.materialIconsExtended)
				implementation(compose.components.resources)
				//https://github.com/DevSrSouza/compose-icons
				implementation(projects.shared)

				implementation(libs.kotlin.result)
				implementation(libs.kotlin.datetime)
				implementation(libs.kermit)

				implementation(libs.ktorfit.lib)
				implementation(libs.ktor.client.content.negotiation)
				implementation(libs.ktor.client.serialization)
				implementation(libs.ktor.client.logging)

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
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
	buildTypes {
		getByName("release") {
			isMinifyEnabled = false
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
	buildFeatures {
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
	ksp(libs.koin.compilerksp)
	ksp(libs.ktorfit.ksp)
//	with(libs.ktorfit.ksp.get()) {
//		add("kspCommonMainMetadata", this)
//        add("kspJvm", this)
//        add("kspJvmTest", this)
//		add("kspAndroid", this)
//		add("kspAndroidTest", this)
//		add("kspIosX64", this)
//		add("kspIosX64Test", this)
//		add("kspIosArm64", this)
//		add("kspIosArm64Test", this)
//		add("kspIosSimulatorArm64", this)
//		add("kspIosSimulatorArm64Test", this)
//        add("kspJs", this)
//        add("kspJsTest", this)
//	}
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
//    // exposeObjectWithName = 'YourAwesomePublicConfig'
//
	defaultConfigs {
		buildConfigField(FieldSpec.Type.STRING, "baseUrl", "https://takaotech.com")
	}
}
//i18n4k {
//	packageName = projectPackage
//	inputDirectory = "src/commonMain/resources/i18n"
//	sourceCodeLocales = listOf("en", "it")
//}

//compose.experimental {
//    web.application {}
//}