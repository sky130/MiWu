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
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            setUrl("https://jitpack.io/")
            isAllowInsecureProtocol = true
        }
    }
}

rootProject.name = "MiWu"
include(":app")
include(":miot-api-impl")
include(":miwu-support")
include(":miwu-support-processor")
include(":miwu-android-processor")
include(":miwu-icon-android-processor")
include(":miwu-support-annotation")
include(":miot-api")
include(":miwu-android")
