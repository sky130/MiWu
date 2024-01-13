import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
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
        minSdk = 26 // 来个人把Base64换掉就能改成21了
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }
    buildTypes {
        getByName("release") {
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

val lifecycle_version = "2.7.0-rc02"

dependencies {
    implementation("com.github.bumptech.glide:glide:4.15.1")
    ksp("com.github.bumptech.glide:compiler:4.15.1")


    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")


    implementation("com.github.KndroidX:KndroidX:de68f7d259")


    //noinspection GradleDependency
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycle_version")
    //noinspection LifecycleAnnotationProcessorWithJava8
    ksp("androidx.lifecycle:lifecycle-compiler:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")


    implementation("com.loongwind.ardf:recyclerview-ext:1.3.3")


    implementation("com.microsoft.appcenter:appcenter-analytics:4.4.5")
    implementation("com.microsoft.appcenter:appcenter-crashes:4.4.5")


    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.okio:okio:3.4.0")


    implementation("androidx.wear:wear:1.4.0-alpha01")



    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.android.material:material:1.11.0")

    implementation(project(":MiotSDK"))

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}