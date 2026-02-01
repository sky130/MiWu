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
    implementation(libs.google.ksp.symbol.processing.api)
}

miwuPublishing {
    name = "MiWu Icon Android Processor"
    group = "io.github.sky130.miwu"
    artifactId = "miwu-icon-android-processor"
    version = autoVersion()
    description = "KSP processor for Android icon generation in MiWu"
    inceptionYear = "2026"
}