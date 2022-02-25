dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://www.jitpack.io")
        google()
        mavenCentral()
    }
}
rootProject.name = "android-communicate"
include(":app")
include(":app-java")
include(":communicate")
include("communicate-kmm")

