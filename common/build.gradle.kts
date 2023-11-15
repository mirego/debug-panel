plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.ksp)
    alias(libs.plugins.mirego.publish)
    alias(libs.plugins.mirego.release)
    `maven-publish`
}

group = "com.mirego.debugpanel"

kotlin {
    @Suppress("OPT_IN_USAGE")
    targetHierarchy.default()

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
        publishLibraryVariants("release")
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
        all {
            languageSettings.optIn("com.russhwolf.settings.ExperimentalSettingsApi")
            languageSettings.optIn("kotlinx.coroutines.FlowPreview")
            languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.startup.runtime)
                implementation(libs.androidx.preference.ktx)
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(libs.viewmodels.declarative.flow)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.multiplatform.settings)
                implementation(libs.multiplatform.settings.coroutines)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.androidx.monitor)
                implementation(libs.androidx.junit)
                implementation(libs.robolectric)
                implementation(libs.kotlinx.coroutines.test)
                implementation(projects.annotations)
                implementation(libs.mockk.android)
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

    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    add("kspAndroidTest", projects.debugpanelprocessor)
}

ktlint {
    filter {
        exclude { element -> element.file.path.contains("generated/") }
    }
}
