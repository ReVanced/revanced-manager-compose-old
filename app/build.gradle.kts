plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("com.mikepenz.aboutlibraries.plugin") version "10.5.1"
    kotlin("plugin.serialization") version "1.7.20"
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
    buildToolsVersion = "33.0.0"

    defaultConfig {
        applicationId = "app.revanced.manager.compose"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "0.0.1"

        vectorDrawables.useSupportLibrary = true

        buildConfigField("String", "REVANCED_API_URL", "\"https://releases.revanced.app\"")
        buildConfigField("String", "GITHUB_API_URL", "\"https://api.github.com\"")
        buildConfigField("String", "SENTRY_DSN", "\"${System.getenv("SENTRY_DSN").orEmpty()}\"")
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
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn" + "-Xcontext-receivers"
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
    composeOptions.kotlinCompilerExtensionVersion = "1.3.2"
}

dependencies {
    // AndroidX core
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.core:core-splashscreen:1.0.0")

    // AndroidX activity
    implementation("androidx.activity:activity-compose:1.6.1")

    // Koin
    val koinVersion = "3.3.0"
    implementation("io.insert-koin:koin-android:$koinVersion")
    implementation("io.insert-koin:koin-androidx-compose:3.3.0")

    // Compose
    val composeVersion = "1.4.0-alpha01"
    implementation("androidx.compose.ui:ui:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.compose.material3:material3:1.1.0-alpha02")
    implementation("androidx.compose.material:material-icons-extended:${composeVersion}")

    // Accompanist
    val accompanistVersion = "0.27.0"
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-placeholder-material:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-drawablepainter:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-flowlayout:$accompanistVersion")

    // Coil (async image loading, network image)
    implementation("io.coil-kt:coil-compose:2.2.2")

    // KotlinX
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    // Navigatio
    implementation("dev.olshevski.navigation:reimagined:1.3.0")

    // ReVanced
    implementation("app.revanced:revanced-patcher:6.1.0")

    // Signing & aligning
    implementation("org.bouncycastle:bcpkix-jdk15on:1.70")
    implementation("com.android.tools.build:apksig:8.0.0-alpha08")

    // Licenses
    implementation("com.mikepenz:aboutlibraries-compose:10.5.1")

    // Ktor
    val ktorVersion = "2.1.3"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // Sentry
    implementation("io.sentry:sentry-android:6.8.0")
}
