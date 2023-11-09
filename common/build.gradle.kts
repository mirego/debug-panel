plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    @Suppress("OPT_IN_USAGE")
    targetHierarchy.default()

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Debug panel"
        homepage = "https://www.mirego.com"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        framework {
            baseName = "common"
        }
    }

    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation("androidx.startup:startup-runtime:1.1.1")
                implementation("androidx.preference:preference-ktx:1.2.1")
            }
        }
        val commonMain by getting {
            dependencies {
                implementation("com.mirego.trikot:viewmodels-declarative-flow:4.5.0-dev2652")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("com.russhwolf:multiplatform-settings:1.1.0")
                implementation("com.russhwolf:multiplatform-settings-coroutines:1.1.0")
            }
        }
    }
}

android {
    namespace = "com.mirego.debugpanel"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
