import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SourcesJar

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    id("miwu-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

android {
    namespace = "miwu.android"
    compileSdk = 36

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        viewBinding = true
    }
}

ksp {
    arg("miwu.icon.enabled", "false")
    arg("miwu.icon.filePath", "$projectDir/icons.txt")
}

dependencies {
    implementation(project(":miot-api"))
    implementation(project(":miwu-support"))
    implementation(project(":miwu-support-annotation"))

    ksp(project(":miwu-support-processor"))
    ksp(project(":miwu-icon-android-processor"))


    implementation(libs.kndroidx.core)

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recyclerview)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

miwuPublishing {
    name = "MiWu Android Support"
    group = "io.github.sky130.miwu"
    artifactId = "miwu-android"
    version = autoVersion()
    description = "Android UI wrappers and view bindings for MiWu widget system"
    inceptionYear = "2026"
}