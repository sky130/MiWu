pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven {
            setUrl("https://jitpack.io/")
            isAllowInsecureProtocol = true
        }
        if (providers.gradleProperty("useMavenLocal").orNull == "true") {
            mavenLocal()
        }
    }
}
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        maven {
            setUrl("https://jitpack.io/")
            isAllowInsecureProtocol = true
        }
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
        if (providers.gradleProperty("useMavenLocal").orNull == "true") {
            mavenLocal()
        }
    }
}

rootProject.name = "MiWu"
include(":app")
include(":miot-api-impl")
include(":miot-api-kmp-impl")
include(":miwu-support")
include(":miwu-support-processor")
include(":miwu-icon-android-processor")
include(":miwu-support-annotation")
include(":miot-api")
include(":miwu-android")
include(":miot-api-common")
