plugins {
    kotlin("jvm")
    alias(libs.plugins.ktlint)
}

dependencies {
    implementation(projects.annotations)
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)
    implementation(libs.symbol.processing.api)
}
