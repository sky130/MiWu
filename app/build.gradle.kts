import miwu.latestGitTag
import miwu.getVersionInt

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    kotlin("kapt")
    kotlin("plugin.serialization") version "2.3.0"
}

val tag = latestGitTag

android {
    namespace = "com.github.miwu"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.github.miwu"
        minSdk = 23
        targetSdk = 35
        versionName = tag
        versionCode = getVersionInt(tag)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        buildTypes {
            release {
                isMinifyEnabled = true
                isShrinkResources = true
                isDebuggable = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
//                applicationVariants.all {
//                    outputs.all {
//                        if (name.contains("release"))
//                            (this as BaseVariantOutputImpl).outputFileName =
//                                "$name-$versionName-$versionCode.apk"
//                    }
//                }
            }
            debug {
                isMinifyEnabled = false
                isShrinkResources = false
                isDebuggable = true
                applicationIdSuffix = ".dev"
                versionNameSuffix = "-DEV"
            }
        }
    }
    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {

    implementation(project(":miwu-support"))
    implementation(project(":miwu-support-annotation"))

    implementation(project(":miot-api"))
    implementation(project(":miot-api-common"))
    implementation(project(":miot-api-impl"))
    implementation(project(":miot-api-kmp-impl"))

    implementation(project(":miwu-android"))


    implementation(libs.kotlinx.serialization.json)


    implementation(libs.glide)
    implementation(libs.androidx.wear.tiles.tooling.preview)
    debugImplementation(libs.androidx.wear.tiles.tooling)
    ksp(libs.glide.compiler)


    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)


    implementation(libs.kndroidx.core)
    implementation(libs.kndroidx.databinding)
    implementation(libs.kndroidx.recycler.databinding)
    implementation(libs.kndroidx.wear.recycler)
    implementation(libs.kndroidx.wear.tile)

//    implementation(libs.recyclerview.ext)

    implementation(libs.androidx.lifecycle.common.java8)
    ksp(libs.androidx.lifecycle.compiler)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.gridlayout)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore)


    implementation(libs.alexzhirkevich.custom.qr.generator)


    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.okhttp)
    // implementation(libs.converter.gson)
    implementation(libs.squareup.retrofit.converter.scalars)
    implementation(libs.squareup.okio)


    implementation(libs.androidx.wear)
    implementation(libs.androidx.wear.tiles)
    implementation(libs.androidx.wear.protolayout)
    implementation(libs.androidx.wear.protolayout.material)
    implementation(libs.androidx.wear.protolayout.expression)
    debugImplementation(libs.androidx.wear.tiles.tooling)
    debugImplementation(libs.androidx.wear.tiles.tooling.preview)
    implementation(libs.google.guava)
    implementation(libs.androidx.concurrent.futures)
    // implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.guava)
    implementation(libs.google.horologist.compose.tools)
    implementation(libs.google.horologist.tiles)

    implementation(libs.kotlinx.coroutines.core)


    // implementation(libs.gson)
    implementation(libs.google.material)
    implementation(libs.google.zxing.core)


    implementation(libs.androidx.concurrent.futures)
    implementation(libs.google.flexbox)


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)


    implementation(libs.resultat)


    implementation(libs.koin.core)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.android.compat)


    implementation(libs.logback.android)
    implementation(libs.slf4j.api)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

afterEvaluate {
    tasks.named("kspDebugKotlin") {
        dependsOn("dataBindingGenBaseClassesDebug")
    }
    tasks.named("kspReleaseKotlin") {
        dependsOn("dataBindingGenBaseClassesRelease")
    }
}
