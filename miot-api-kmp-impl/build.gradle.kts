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
    js {
        browser()
        binaries.executable()
    }
    withSourcesJar()
    jvmToolchain(21)
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":miot-api"))
                implementation(project.dependencies.platform(libs.kotlincrypto.hash.bom))


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