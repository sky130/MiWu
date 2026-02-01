plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.ktorfit)
    kotlin("plugin.serialization") version "2.3.0"
    id("miwu-publish")
}

kotlin {
    jvm() // KSP library only supports JVM
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":miwu-support-annotation"))
                implementation(project(":miot-api"))
                implementation(project(":miot-api-common"))
                implementation(project(":miot-api-kmp-impl"))
                implementation(libs.squareup.kotlin.poet)
                implementation(libs.squareup.kotlinpoet.ksp)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.google.ksp.symbol.processing.api)
            }
        }
    }
}

ktorfit {
    compilerPluginVersion = "2.3.3"
}

miwuPublishing {
    name = "MiWu Support Processor"
    group = "io.github.sky130.miwu"
    artifactId = "miwu-support-processor"
    version = autoVersion()
    description = "KSP processors for MiWu widget and device code generation"
    inceptionYear = "2026"
}

afterEvaluate {
    tasks.named("sourcesJar") {
        dependsOn(tasks.named("kspCommonMainKotlinMetadata"))
    }
}