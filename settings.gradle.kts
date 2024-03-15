import java.net.URI

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
        maven { url = uri("https://jitpack.io") }
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
include(":feature:pnl")
include(":feature:symbol")
