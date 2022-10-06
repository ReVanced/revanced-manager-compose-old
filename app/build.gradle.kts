plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("com.mikepenz.aboutlibraries.plugin") version "10.4.0"
    kotlin("plugin.serialization") version "1.7.10"
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    google()
    maven {
        url = uri("https://maven.pkg.github.com/revanced/revanced-patcher")
        credentials {
            username = (project.findProperty("gpr.user") ?: System.getenv("GITHUB_ACTOR")) as String
            password = (project.findProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")) as String
        }
    }
}

android {
    namespace = "app.revanced.manager"
    compileSdk = 33

    lint {
        abortOnError = false
        disable += "DialogFragmentCallbacksDetector"
    }

    defaultConfig {
        applicationId = "app.revanced.manager.compose"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "0.0.1"

        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }

    packagingOptions {
        resources {
            excludes += "/prebuilt/**"
            excludes += "/**/*.version"
            excludes += "/kotlin-tooling-metadata.json"
            excludes += "/okhttp3/**"
            excludes += "/DebugProbesKt.bin"
        }
    }

    buildFeatures.compose = true
    composeOptions.kotlinCompilerExtensionVersion = "1.3.0-rc02"
}

dependencies {
    // AndroidX core
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.core:core-splashscreen:1.0.0")

    // AndroidX activity
    implementation("androidx.activity:activity-compose:1.6.0-rc02")
    implementation("androidx.work:work-runtime-ktx:2.7.1")

    // Koin
    val koinVersion = "3.2.0"
    implementation("io.insert-koin:koin-android:$koinVersion")
    implementation("io.insert-koin:koin-androidx-compose:$koinVersion")
    implementation("io.insert-koin:koin-androidx-workmanager:3.2.1")

    // Compose
    val composeVersion = "1.3.0-alpha03"
    implementation("androidx.compose.ui:ui:${composeVersion}")
    debugImplementation("androidx.compose.ui:ui-tooling:${composeVersion}")
    implementation("androidx.compose.material3:material3:1.0.0-beta02")
    implementation("androidx.compose.material:material-icons-extended:${composeVersion}")

    // Accompanist
    val accompanistVersion = "0.26.0-alpha"
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-placeholder-material:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-drawablepainter:$accompanistVersion")

    // Coil (async image loading)
    implementation("io.coil-kt:coil-compose:2.1.0")

    // KotlinX
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    // Taxi (navigation)
    implementation("com.github.X1nto:Taxi:1.2.0")

    val ktorVersion = "2.0.3"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-android:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // ReVanced
    implementation("app.revanced:revanced-patcher:6.0.0")

    // Coil for network image
    implementation("io.coil-kt:coil-compose:2.1.0")

    // Signing & aligning
    implementation("org.bouncycastle:bcpkix-jdk15on:1.70")
    implementation("com.android.tools.build:apksig:7.2.2")

    // Licenses
    implementation("com.mikepenz:aboutlibraries-compose:10.4.0")

    // ListenableFuture
    implementation("com.google.guava:guava:31.0.1-android")
    implementation("androidx.concurrent:concurrent-futures:1.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.6.0")
}
