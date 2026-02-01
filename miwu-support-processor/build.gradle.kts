plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("java-library")
    id("miwu-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
    }
}

dependencies {
    implementation(project(":miwu-support-annotation"))
    implementation(libs.squareup.kotlin.poet)
    implementation(libs.squareup.kotlinpoet.ksp)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.google.gson)
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.converter.gson)
    implementation(libs.google.ksp.symbol.processing.api)
}

miwuPublishing {
    name = "MiWu Support Processor"
    group = "io.github.sky130.miwu"
    artifactId = "miwu-support-processor"
    version = autoVersion()
    description = "KSP processors for MiWu widget and device code generation"
    inceptionYear = "2026"
}