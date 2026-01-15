plugins {
    alias(libs.plugins.kotlin.multiplatform)
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
