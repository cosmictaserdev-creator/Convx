pluginManagement {
    resolutionStrategy {
        eachPlugin {
            val version = requested.version
            when (requested.id.id) {
                "com.android.application",
                "com.android.kotlin.multiplatform.library" ->
                    useModule("com.android.tools.build:gradle:$version")

                "org.jetbrains.kotlin.jvm",
                "org.jetbrains.kotlin.multiplatform" ->
                    useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:$version")

                "org.jetbrains.kotlin.plugin.compose" ->
                    useModule("org.jetbrains.kotlin:compose-compiler-gradle-plugin:$version")

                "org.jetbrains.compose" ->
                    useModule("org.jetbrains.compose:compose-gradle-plugin:$version")
            }
        }
    }

    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "convx-kmp-clients"
include(":shared", ":androidHost", ":desktopApp")
include(":network-storage-core")
project(":network-storage-core").projectDir = file("../../feature/network-storage/domain")
include(":network-storage-transports")
project(":network-storage-transports").projectDir = file("../../feature/network-storage/data")
include(":network-storage-presentation")
project(":network-storage-presentation").projectDir = file("../../feature/network-storage/ui")
include(":modules-core", ":modules-runtime", ":modules-presentation")
project(":modules-core").projectDir = file("../../feature/modules/domain")
project(":modules-runtime").projectDir = file("../../feature/modules/data")
project(":modules-presentation").projectDir = file("../../feature/modules/ui")
include(":lyrics-core", ":canvas-core", ":playback-core", ":playback-data", ":playback-presentation", ":history-core", ":library-core", ":library-data", ":search-core", ":catalog-core", ":account-core", ":account-presentation", ":catalog-presentation", ":design-system", ":hig")
include(":innertube")
include(":lyrics-data")
include(":canvas")
project(":canvas").projectDir = file("../../canvas")
project(":lyrics-data").projectDir = file("../../feature/lyrics/data")
project(":innertube").projectDir = file("../../core/innertube")
project(":lyrics-core").projectDir = file("../../feature/lyrics/domain")
project(":canvas-core").projectDir = file("../../feature/canvas/domain")
project(":playback-core").projectDir = file("../../feature/playback/domain")
project(":playback-data").projectDir = file("../../feature/playback/data")
project(":playback-presentation").projectDir = file("../../feature/playback/ui")
project(":history-core").projectDir = file("../../feature/history/domain")
project(":library-core").projectDir = file("../../feature/library/domain")
project(":library-data").projectDir = file("../../feature/library/data")
project(":search-core").projectDir = file("../../feature/search/domain")
project(":catalog-core").projectDir = file("../../feature/catalog/domain")
project(":account-core").projectDir = file("../../feature/account/domain")
project(":account-presentation").projectDir = file("../../feature/account/ui")
project(":catalog-presentation").projectDir = file("../../feature/catalog/ui")
project(":design-system").projectDir = file("../../core/design-system")
project(":hig").projectDir = file("../../core/hig")
include(":listen-together-core")
project(":listen-together-core").projectDir = file("../../feature/listen-together/domain")
