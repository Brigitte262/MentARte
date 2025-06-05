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
        maven { url = uri("https://repo.maven.apache.org/maven2/") } // 🆕 Añadimos el Maven directo
        maven { url = uri("https://jitpack.io") } // 🆕 Añadimos Jitpack para futuros recursos

    }
}

rootProject.name = "AprendeJugando"
include(":app", ":unityLibrary")



