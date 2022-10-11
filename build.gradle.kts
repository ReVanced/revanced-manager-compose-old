buildscript {
    extra.apply {
        // Global variable for some dependencies
        set("compose_version", "1.3.0-rc01")
        set("ktor_version", "2.1.2")
        set("room_version", "2.7.1")
    }
    repositories {
        google()
    }
}

plugins {
    id("com.android.application") version "7.4.0-beta02" apply false
    id("com.android.library") version "7.4.0-beta02" apply false
    id("org.jetbrains.kotlin.android") version "1.7.20" apply false
    id("com.google.devtools.ksp") version "1.7.20-+" apply false
}
repositories {
    google()
}
