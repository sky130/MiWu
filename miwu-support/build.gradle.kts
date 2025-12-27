plugins {
    kotlin("jvm")
    alias(libs.plugins.ksp)
    `maven-publish`
}

group = "com.github.sky130"
version = libs.versions.miwu.get()

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
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
    }
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin/")
    }
    sourceSets.test {
        // kotlin.srcDir("build/generated/ksp/main/kotlin/")
    }
}

ksp {
    arg("miwu.spec.enabled", "true")
    arg("miwu.icon.enabled", "true")
    arg("miwu.icon.filePath", "$projectDir\\icons.txt")
}

dependencies {
    ksp(project(":miwu-support-processor"))
    implementation(project(":miwu-support-annotation"))
    implementation(project(":miot-api"))
    implementation(libs.json.json)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.converter.gson)
    implementation(libs.gson)
    implementation(libs.converter.scalars)
    implementation(libs.okio)
    implementation(libs.jetbrains.kotlinx.coroutines.core)
}
