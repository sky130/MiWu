import org.jetbrains.kotlin.fir.declarations.builder.buildTypeAlias

plugins {
    kotlin("jvm")
    `maven-publish`
}

kotlin {
    jvmToolchain(8)
}

dependencies {
    implementation(libs.json.json)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.converter.gson)
    implementation(libs.gson)
    implementation(libs.converter.scalars)
    implementation(libs.okio)
    implementation(libs.jetbrains.kotlinx.coroutines.core)
}
