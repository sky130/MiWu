plugins {
    kotlin("jvm")
    id("miwu-publish")
    kotlin("plugin.serialization") version "2.3.0"
}

java {
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
    implementation(libs.squareup.okio)
    implementation(libs.kotlinx.coroutines.core)
}

miwuPublishing {
    name = "Miot API Implementation"
    group = "io.github.sky130.miwu"
    artifactId = "miot-api-impl"
    version = autoVersion()
    description = "Retrofit-based implementation for Miot API"
    inceptionYear = "2026"
}