enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "DebugPanel"
include(":common")
include(":debugpanelprocessor")
include(":annotations")
include(":sample", ":sample:android", ":sample:common")
