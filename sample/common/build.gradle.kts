import com.android.build.gradle.internal.tasks.factory.dependsOn

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.ksp)
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
        name = "TRIKOT_FRAMEWORK_NAME"
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        framework {
            baseName = "TRIKOT_FRAMEWORK_NAME"
            transitiveExport = true
            export(projects.debugPanel.common)
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.debugPanel.common)
                implementation(projects.annotations)
                api(libs.trikot.viewmodels.declarative.flow)
                api(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.datetime)
            }
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }
    }
}

android {
    namespace = "com.mirego.debugpanel.sample"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    add("kspCommonMainMetadata", projects.debugpanelprocessor)
}

ktlint {
    filter {
        exclude { element -> element.file.path.contains("generated/") }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>>().all {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

tasks.named("runKtlintCheckOverCommonMainSourceSet").dependsOn("kspCommonMainKotlinMetadata")
tasks.named("runKtlintFormatOverCommonMainSourceSet").dependsOn("kspCommonMainKotlinMetadata")
