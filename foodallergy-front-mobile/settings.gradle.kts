pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // For GitHub dependencies
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") } // For snapshot versions
    }
}

rootProject.name = "Food Allergy App"
include(":app")
