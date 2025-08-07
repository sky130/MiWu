// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.android.library) apply false
    kotlin("kapt") version "1.9.10"
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "2.1.21"))
    }
}