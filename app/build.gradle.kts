import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import kotlin.math.pow

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    id("kotlin-kapt")
}

val miwuVersion = libs.versions.miwu.get()

android {
    namespace = "com.github.miwu"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.github.miwu"
        minSdk = 21
        targetSdk = 35
        versionCode = getVersionInt(miwuVersion)
        versionName = miwuVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
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
                applicationVariants.all {
                    outputs.all {
                        if (name.contains("release"))
                            (this as BaseVariantOutputImpl).outputFileName =
                                "$name-$versionName-$versionCode.apk"
                    }
                }
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

dependencies {

    implementation(project(":miwu-support"))
    implementation(project(":miwu-support-annotation"))

    implementation(project(":miot-api"))
    implementation(project(":miot-api-impl"))

    implementation(project(":miwu-android"))


    implementation(libs.glide)
    implementation(libs.androidx.tiles.tooling.preview)
    debugImplementation(libs.androidx.tiles.tooling)
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


    implementation(libs.alexzhirkevich.custom.qr.generator)


    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.converter.gson)
    implementation(libs.converter.scalars)
    implementation(libs.okio)


    implementation(libs.androidx.wear)
    implementation(libs.androidx.tiles)
    implementation(libs.androidx.protolayout)
    implementation(libs.protolayout.material)
    implementation(libs.protolayout.expression)
    implementation(libs.jetbrains.kotlinx.coroutines.core)
    debugImplementation(libs.androidx.tiles.tooling)
    debugImplementation(libs.androidx.tiles.tooling.preview)
    implementation(libs.google.guava)
    implementation(libs.androidx.concurrent.futures)
    // implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.guava)
    implementation(libs.google.horologist.compose.tools)
    implementation(libs.google.horologist.tiles)



    implementation(libs.gson)
    implementation(libs.material)
    implementation(libs.core)


    implementation(libs.androidx.concurrent.futures)
    implementation(libs.flexbox)


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

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

fun getVersionInt(ver: String): Int {
    var version = 0
    var times = 1
    ver.split(".").forEach {
        version += it.toInt() * 100f.pow(3 - times).toInt()
        times++
    }
    return version
}
