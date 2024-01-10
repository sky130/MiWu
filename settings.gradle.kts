

pluginManagement {
    repositories {
        google()
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
//        maven("https://jitpack.io/")
    }
}

rootProject.name = "MiWu"
include(":app")

include(":MiotSDK")
