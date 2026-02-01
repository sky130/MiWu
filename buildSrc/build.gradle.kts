import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    `maven-publish`
    `kotlin-dsl`
}

group = "miwu.publish.plugin"
version = "1.0.0"

gradlePlugin {
    plugins {
        create("miwu-publish") {
            id = "miwu-publish"
            implementationClass = "miwu.MiwuPublishingPlugin"
        }
    }
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation(libs.android.plugin)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.plugin)
    implementation(libs.vanniktech.maven.publish)
}

publishing {
    repositories {
        mavenLocal()
    }
}

kotlin {
    jvmToolchain(21)
}