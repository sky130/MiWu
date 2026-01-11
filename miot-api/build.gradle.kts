plugins {
    alias(libs.plugins.kotlin.multiplatform)
    kotlin("plugin.serialization") version "2.3.0"
    `maven-publish`
}

group = "com.github.sky130"
version = libs.versions.miwu.get()


kotlin {
    jvm()
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlin.stdlib)
                implementation(libs.jetbrains.kotlinx.coroutines.core)
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
        }
    }
}

kotlin {
    withSourcesJar()
    jvmToolchain(21)
}