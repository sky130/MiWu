import org.jetbrains.kotlin.fir.declarations.builder.buildTypeAlias

plugins {
    kotlin("jvm")
    `maven-publish`
}

kotlin {
    jvmToolchain(8)
}

dependencies {
    implementation("org.json:json:20231013")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.okio:okio:3.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}
