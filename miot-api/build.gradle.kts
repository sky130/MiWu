plugins {
    alias(libs.plugins.kotlin.multiplatform)
    kotlin("plugin.serialization") version "2.3.0"
    id("miwu-publish")
}

kotlin {
    jvm()
    js {
        browser()
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
    }
}