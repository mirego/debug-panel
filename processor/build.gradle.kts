plugins {
    kotlin("jvm")
    alias(libs.plugins.ktlint)
    alias(libs.plugins.mirego.publish)
}

group = "com.mirego.debugpanel"

java {
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    implementation(projects.annotations)
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)
    implementation(libs.ksp.api)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "compiler"
            from(components["java"])
        }
    }
}
