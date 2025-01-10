import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.serialization)
}

kotlin {

    androidTarget {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
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
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(project.dependencies.platform(libs.koin.annotation.bom))
            implementation(libs.koin.core)
            implementation(libs.serialization)
            api(libs.kotlin.result)
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test")) {
                    exclude(group = "org.hamcrest")
                }
                // https://mvnrepository.com/artifact/org.hamcrest/hamcrest
            }
        }

        val androidUnitTest by getting {
            dependencies {
// 				implementation("org.hamcrest:hamcrest:2.2")
            }
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

/*
Mitigation for error
A problem was found with the configuration of task ':shared:runKtlintFormatOverCommonMainSourceSet' (type 'KtLintFormatTask').
:shared:runKtlintFormatOverCommonMainSourceSet' uses this output of task ':shared:kspCommonMainKotlinMetadata' without declaring an explicit or implicit dependency.
This can lead to incorrect results being produced, depending on what order the tasks are executed.
 */
tasks.named("runKtlintFormatOverCommonMainSourceSet") {
    mustRunAfter(tasks.named("kspCommonMainKotlinMetadata"))
}

// dependencies {
// 	with(libs.ktorfit.ksp.get()) {
// 		add("kspCommonMainMetadata", this)
// 		add("kspJvm", this)
// 		add("kspJvmTest", this)
// 		add("kspAndroid", this)
// 		add("kspAndroidTest", this)
// //		add("kspIosX64", this)
// //		add("kspIosX64Test", this)
// //		add("kspIosArm64", this)
// //		add("kspIosArm64Test", this)
// //		add("kspIosSimulatorArm64", this)
// //		add("kspIosSimulatorArm64Test", this)
// //        add("kspJs", this)
// //        add("kspJsTest", this)
// 	}
// }

android {
    namespace = "com.takaotech.dashboard.shared"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()
    defaultConfig {
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
    }
}
