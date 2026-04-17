pluginManagement {
    repositories {
        maven("https://maven.kikugie.dev/releases") {
            name = "KikuGie Releases"
        }
        maven("https://maven.kikugie.dev/snapshots") {
            name = "KikuGie Snapshots"
        }
        maven("https://maven.neoforged.net/releases") {
            name = "NeoForged Plugins"
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.kikugie.stonecutter") version "0.9.1-beta.2"
}

rootProject.name = rootDir.name

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    create(getRootProject()) {
        versions("26.1")
        vcsVersion = "26.1"
    }
}
