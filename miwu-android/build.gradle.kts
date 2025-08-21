plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    `maven-publish`
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

val miwuVersion = libs.versions.miwu.get()

android {
    namespace = "miwu.android"
    compileSdk = 35

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
            group = "com.github.sky130"
            version = libs.versions.miwu.get()
        }
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

kotlin{
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/debug/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/debug/kotlin/")
    }
}

dependencies {
    implementation(project(":miot-api"))
    implementation(project(":miwu-support"))
    implementation(project(":miwu-support-annotation"))

    ksp(project(":miwu-android-processor"))
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