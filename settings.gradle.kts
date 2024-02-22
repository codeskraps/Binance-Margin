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
    }
}

rootProject.name = "Binance"
include(":app")
include(":core:client")
include(":core:realm")
include(":core:domain")
include(":feature:account")
include(":feature:trades")
include(":feature:settings")
