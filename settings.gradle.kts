enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://s3.amazonaws.com/mirego-maven/public")
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "mirego") {
                useModule("mirego:${requested.id.name}-plugin:${requested.version}")
            }
        }
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "DebugPanel"
include(":core")
include(":debugpanelprocessor")
include(":annotations")
include(":sample", ":sample:android", ":sample:common")
