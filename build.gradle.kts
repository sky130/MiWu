plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.android.lint) apply false
    alias(libs.plugins.vanniktech.maven.publish) apply false
    id("miwu-publish") apply false
    kotlin("kapt")
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "2.3.0"))
    }
}
