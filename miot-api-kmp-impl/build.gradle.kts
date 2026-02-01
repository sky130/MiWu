plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    kotlin("plugin.serialization") version "2.3.0"
    id("miwu-publish")
}

kotlin {
    jvm()
    js(IR) {
        browser()
        binaries.executable()
    }
    jvmToolchain(21)
    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("reflect"))


                implementation(project(":miot-api"))
                implementation(project(":miot-api-common"))


                implementation(libs.squareup.okio)


                implementation(libs.koin.core)


                implementation(libs.ktorfit)


                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)


                implementation(libs.ktor.client.json)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.content.negotiation)
            }
        }
    }
}

miwuPublishing {
    name = "Miot API KMP Implementation"
    group = "io.github.sky130.miwu"
    artifactId = "miot-api-kmp-impl"
    version = autoVersion()
    description = "Ktorfit-based multiplatform implementation for Miot API"
    inceptionYear = "2026"
}


ktorfit {
    compilerPluginVersion = "2.3.3"
}

afterEvaluate {
    tasks.named("sourcesJar") {
        dependsOn(tasks.named("kspCommonMainKotlinMetadata"))
    }
}