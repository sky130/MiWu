import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.github.miwu"
    compileSdk = 33

    val javaVersion = 11
    compileOptions {
//        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility(javaVersion)
        targetCompatibility(javaVersion)
    }
    kotlinOptions.jvmTarget = javaVersion.toString()
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = javaVersion.toString()
    }
    defaultConfig {
        applicationId = "com.github.miwu"
        minSdk = 26 // 来个人把Base64换掉就能改成21了
        targetSdk = 33
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }
//    signingConfigs {
//        create("release") {
//            storeFile = file("")
//            storePassword = ""
//            keyAlias = ""
//            keyPassword = ""
//        }
//    }

    buildTypes {
        getByName("release") {
//            signingConfig = signingConfigs.getByName("release")
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
        getByName("debug") {
//            signingConfig = signingConfigs.getByName("release")
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
}

dependencies {
    implementation("com.github.bumptech.glide:glide:4.15.1")
    ksp("com.github.bumptech.glide:compiler:4.15.1")
    ksp("androidx.room:room-compiler:2.5.2")
    implementation("androidx.room:room-ktx:2.5.2")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.room:room-runtime:2.5.2")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}