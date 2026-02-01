plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("miwu-publish")
}

kotlin {
    jvm()
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":miwu-support-annotation"))
                implementation(libs.squareup.kotlin.poet)
                implementation(libs.squareup.kotlinpoet.ksp)
                implementation(libs.google.ksp.symbol.processing.api)
            }
        }
    }
}


miwuPublishing {
    name = "MiWu Icon Android Processor"
    group = "io.github.sky130.miwu"
    artifactId = "miwu-icon-android-processor"
    version = autoVersion()
    description = "KSP processor for Android icon generation in MiWu"
    inceptionYear = "2026"
}