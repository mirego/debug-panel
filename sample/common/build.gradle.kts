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
        summary = "Sample app for the debug panel library"
        homepage = "https://github.com/mirego/debug-panel"
        license = "MIT license"
        version = "1.0"
        ios.deploymentTarget = "15.0"
        framework {
            baseName = "TRIKOT_FRAMEWORK_NAME"
            transitiveExport = true
            export(projects.debugPanel.core)
        }
        extraSpecAttributes = mutableMapOf(
            "prepare_command" to """
                <<-CMD
                    ../../gradlew :sample:common:generateDummyFramework
                CMD
            """.trimIndent(),
        )
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.debugPanel.core)
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
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    add("kspCommonMainMetadata", projects.processor)
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
