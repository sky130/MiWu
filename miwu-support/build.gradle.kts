plugins {
    kotlin("jvm")
    alias(libs.plugins.ksp)
    id("miwu-publish")
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
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin/")
    }
}

ksp {
    arg("miwu.spec.enabled", "true")
    arg("miwu.icon.enabled", "true")
    arg("miwu.icon.filePath", "$projectDir/icons.txt")
}

dependencies {
    ksp(project(":miwu-support-processor"))
    api(project(":miot-api-common"))
    implementation(project(":miwu-support-annotation"))
    implementation(project(":miot-api"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.squareup.okio)
    implementation(libs.kotlinx.coroutines.core)
}

miwuPublishing {
    name = "MiWu Support"
    group = "io.github.sky130.miwu"
    artifactId = "miwu-support"
    version = autoVersion()
    description = "Core widget system and device management for MiWu"
    inceptionYear = "2026"
}