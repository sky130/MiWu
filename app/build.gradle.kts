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
        minSdk = 26
        targetSdk = 34
        versionCode = 5
        versionName = "2.0.3"
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
        debug{
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

val lifecycle_version = "2.7.0"

dependencies {
    implementation("com.github.bumptech.glide:glide:4.15.1")
    ksp("com.github.bumptech.glide:compiler:4.15.1")


    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")


    implementation("com.github.kndroidx:kndroidx:9e2095ac42")


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
    implementation("androidx.wear.tiles:tiles:1.3.0")
    implementation("androidx.wear.protolayout:protolayout:1.1.0")
    implementation("androidx.wear.protolayout:protolayout-material:1.1.0")
    implementation("androidx.wear.protolayout:protolayout-expression:1.1.0")


    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.android.material:material:1.11.0")


    implementation("com.google.guava:guava:31.0.1-android")
    implementation("androidx.concurrent:concurrent-futures:1.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.7.3")
    implementation("com.google.android.horologist:horologist-compose-tools:0.1.8")
    implementation("com.google.android.horologist:horologist-tiles:0.1.8")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation(project(":miot-api"))


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}