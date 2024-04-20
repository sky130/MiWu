import org.jetbrains.kotlin.fir.declarations.builder.buildTypeAlias

plugins {
    kotlin("jvm")
    `maven-publish`
}

group = "com.github.sky130"
version = latestGitTag


publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()
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

val latestGitTag: String
    get() {
        val process = ProcessBuilder("git", "describe", "--tags", "--abbrev=0").start()
        return process.inputStream.bufferedReader().use { bufferedReader ->
            bufferedReader.readText().replace("v","").trim().ifEmpty { "debug" }
        }
    }