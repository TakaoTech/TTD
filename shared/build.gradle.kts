plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.androidLibrary)
	alias(libs.plugins.ksp)
	alias(libs.plugins.ktorfit)
	alias(libs.plugins.serialization)
}

kotlin {

	androidTarget {
		compilations.all {
			kotlinOptions {
				jvmTarget = "1.8"
			}
		}
	}

	iosX64()
	iosArm64()
	iosSimulatorArm64()

	jvm()

	sourceSets {
		commonMain.dependencies {
			// put your Multiplatform dependencies here
			implementation(libs.kotlin.datetime)
			implementation(libs.ktorfit.lib)
			implementation(libs.koin.core)
			implementation(libs.serialization)
		}
	}
}

dependencies {
	with(libs.ktorfit.ksp.get()) {
		add("kspCommonMainMetadata", this)
		add("kspJvm", this)
		add("kspJvmTest", this)
		add("kspAndroid", this)
		add("kspAndroidTest", this)
		add("kspIosX64", this)
		add("kspIosX64Test", this)
		add("kspIosArm64", this)
		add("kspIosArm64Test", this)
		add("kspIosSimulatorArm64", this)
		add("kspIosSimulatorArm64Test", this)
//        add("kspJs", this)
//        add("kspJsTest", this)
	}
}

android {
	namespace = "com.takaotech.dashboard.shared"
	compileSdk = libs.versions.android.compileSdk.get().toInt()
	defaultConfig {
		minSdk = libs.versions.android.minSdk.get().toInt()
	}
}
