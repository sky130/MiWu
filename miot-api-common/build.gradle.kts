plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("miwu-publish")
}

kotlin {
    jvm()
    js {
        browser()
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
            }
        }
    }
}

miwuPublishing {
    name = "Miot API Common"
    group = "io.github.sky130.miwu"
    artifactId = "miot-api-common"
    version = autoVersion()
    description = "Miot API Common definitions"
    inceptionYear = "2026"
}
