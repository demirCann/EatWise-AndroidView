pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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

rootProject.name = "EatWiseAndroidView"
include(":app")
include(":core")
include(":feature")
include(":core:data")
include(":core:model")
include(":core:network")
include(":core:database")
include(":feature:meals")
include(":feature:favorites")
include(":feature:chat")
include(":feature:detail")
include(":feature:search")
include(":core:testing")
