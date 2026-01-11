plugins {
    alias(libs.plugins.kotlin.multiplatform)
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
    withSourcesJar()
    jvmToolchain(21)
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

//dependencies {
//    implementation(project(":miot-api"))
//    implementation(libs.converter.kotlinx.serialization)
//    implementation(libs.kotlinx.serialization.json)
//    implementation(libs.koin.core)
//    implementation(libs.json.json)
//    implementation(libs.retrofit)
//    implementation(libs.okhttp)
//    // implementation(libs.converter.gson)
//    // implementation(libs.gson)
//    // implementation(libs.converter.scalars)
//    implementation(libs.okio)
//    implementation(libs.jetbrains.kotlinx.coroutines.core)
//}