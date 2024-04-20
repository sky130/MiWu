plugins {
    kotlin("jvm")
    `maven-publish`
}

kotlin {
    jvmToolchain(8)
}

dependencies {
    implementation("org.json:json:20231013")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.okio:okio:3.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                groupId = "com.github.sky233.miwu"
                artifactId = "miot"
                version = latestGitTag()

                afterEvaluate {
                    from(components["release"])
                }
            }
        }
        repositories {
            mavenLocal()
        }
    }
}

fun latestGitTag(): String {
    val process = ProcessBuilder("git", "describe", "--tags", "--abbrev=0").start()
    return process.inputStream.bufferedReader().use { bufferedReader ->
        bufferedReader.readText().trim().replace("v", "").ifEmpty { "debug" }
    }
}