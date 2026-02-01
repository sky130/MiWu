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
        }
    }
}

miwuPublishing {
    name = "MiWu Support Annotation"
    group = "io.github.sky130.miwu"
    artifactId = "miwu-support-annotation"
    version = autoVersion()
    description = "Annotations for MiWu widget and device definitions"
    inceptionYear = "2026"
}