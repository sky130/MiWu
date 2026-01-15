plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    kotlin("plugin.serialization") version "2.3.0"
    `maven-publish`
}

group = "com.github.sky130"
version = libs.versions.miwu.get()

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
        }
    }
}

kotlin {
    jvm()
    js(IR) {
        browser()
        binaries.executable()
    }
    withSourcesJar()
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

ktorfit {
    compilerPluginVersion = "2.3.3"
}

tasks.named("sourcesJar") {
    mustRunAfter(tasks.named("kspCommonMainKotlinMetadata"))
}

project.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin> {
    project.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec>().download = false
}