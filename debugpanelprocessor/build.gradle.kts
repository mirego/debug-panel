plugins {
    kotlin("jvm")
    alias(libs.plugins.ktlint)
}

dependencies {
    implementation(projects.annotations)
    implementation("com.squareup:kotlinpoet:1.14.2")
    implementation("com.squareup:kotlinpoet-ksp:1.14.2")
    implementation("com.google.devtools.ksp:symbol-processing-api:1.9.10-1.0.13")
}
