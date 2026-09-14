plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.koin.compiler)
    id("miwu-publish")
}

kotlin {
    jvm()
    linuxX64()
    jvmToolchain(21)
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.coroutines.core)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.annotations)
        }
    }
}

miwuPublishing {
    name = "MiWu Dispatchers"
    group = "io.github.sky130.miwu"
    artifactId = "miwu-dispatchers"
    version = autoVersion()
    description = "Shared coroutine dispatcher definitions for MiWu"
    inceptionYear = "2026"
}
