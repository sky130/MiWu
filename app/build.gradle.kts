import com.android.build.gradle.internal.api.BaseVariantOutputImpl


plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.ksp)
    id("kotlin-kapt")
}



android {
    namespace = "com.github.miwu"
    compileSdk = 34

    val javaVersion = 11
    compileOptions {
        sourceCompatibility(javaVersion)
        targetCompatibility(javaVersion)
    }
    kotlinOptions.jvmTarget = javaVersion.toString()
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = javaVersion.toString()
    }
    defaultConfig {
        applicationId = "com.github.miwu"
        minSdk = 26
        targetSdk = 34
        versionCode = 7
        versionName = "2.0.5"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }
    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }
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
    packagingOptions.resources.excludes.addAll(
        listOf(
            "**/*.kotlin_*",
            "META-INF/versions/**",
        )
    )
    lint.warning += "UnsafeOptInUsageError"
    flavorDimensions.add("miwu")
    sourceSets {
        this["release"].java.srcDir("build/generated/data_binding_base_class_source_out/release/out")
        this["debug"].java.srcDir("build/generated/data_binding_base_class_source_out/debug/out")
    }
}

val lifecycle_version = "2.7.0"

dependencies {
    implementation(libs.glide)
    ksp(libs.glide.compiler)


    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)


    implementation(libs.kndroidx)


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


    implementation(libs.recyclerview.ext)


    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.converter.gson)
    implementation(libs.converter.scalars)
    implementation(libs.okio)


    implementation(libs.androidx.wear)


    implementation(libs.gson)
    implementation(libs.material)
    implementation(libs.core)


    implementation(libs.androidx.concurrent.futures)
    implementation(libs.flexbox)


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

afterEvaluate {
    tasks.named("kspDebugKotlin") {
        dependsOn("dataBindingGenBaseClassesDebug")
    }
}
