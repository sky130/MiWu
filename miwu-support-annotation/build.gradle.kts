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

miwuPublishing {
    name = "MiWu Support Annotation"
    group = "io.github.sky130.miwu"
    artifactId = "miwu-support-annotation"
    version = autoVersion()
    description = "Annotations for MiWu widget and device definitions"
    inceptionYear = "2026"
}