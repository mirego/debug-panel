plugins {
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.kotlinCocoapods).apply(false)
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.mirego.publish)
    alias(libs.plugins.mirego.release)
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        maven(url = "https://s3.amazonaws.com/mirego-maven/public")
    }
}

release {
    checkTasks = listOf(":annotations:check", ":core:check", ":debugpanelprocessor:check")
    buildTasks = listOf(":annotations:publish", ":core:publish", ":debugpanelprocessor:publish")
    updateVersionPart = 2
}
