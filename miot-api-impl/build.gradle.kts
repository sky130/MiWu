plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.3.0"
    `maven-publish`
}

group = "com.github.sky130"
version = libs.versions.miwu.get()

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
    }
}

dependencies {
    implementation(project(":miot-api"))
    implementation(project(":miot-api-common"))
    implementation(libs.squareup.retrofit.converter.kotlinx.serialization)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.koin.core)
    implementation(libs.json)
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.okhttp)
    // implementation(libs.converter.gson)
    // implementation(libs.gson)
    // implementation(libs.converter.scalars)
    implementation(libs.squareup.okio)
    implementation(libs.kotlinx.coroutines.core)
}