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
project(":network-storage-core").projectDir = file("../../shared-kmp/domain/network-storage")
include(":network-storage-transports")
project(":network-storage-transports").projectDir = file("../../shared-kmp/data/network-storage")
include(":network-storage-presentation")
project(":network-storage-presentation").projectDir = file("../../shared-kmp/presentation/network-storage")
include(":modules-core", ":modules-runtime", ":modules-presentation")
project(":modules-core").projectDir = file("../../shared-kmp/domain/modules")
project(":modules-runtime").projectDir = file("../../shared-kmp/data/modules")
project(":modules-presentation").projectDir = file("../../shared-kmp/presentation/modules")
include(":lyrics-core", ":canvas-core", ":playback-core", ":playback-data", ":playback-presentation", ":history-core", ":library-core", ":library-data", ":search-core", ":catalog-core", ":account-core", ":account-presentation", ":catalog-presentation", ":design-system", ":hig")
include(":innertube")
include(":lyrics-data")
include(":canvas")
project(":canvas").projectDir = file("../../canvas")
project(":lyrics-data").projectDir = file("../../shared-kmp/data/lyrics")
project(":innertube").projectDir = file("../../shared-kmp/data/innertube")
project(":lyrics-core").projectDir = file("../../shared-kmp/domain/lyrics")
project(":canvas-core").projectDir = file("../../shared-kmp/domain/canvas")
project(":playback-core").projectDir = file("../../shared-kmp/domain/playback")
project(":playback-data").projectDir = file("../../shared-kmp/data/playback")
project(":playback-presentation").projectDir = file("../../shared-kmp/presentation/playback")
project(":history-core").projectDir = file("../../shared-kmp/domain/history")
project(":library-core").projectDir = file("../../shared-kmp/domain/library")
project(":library-data").projectDir = file("../../shared-kmp/data/library")
project(":search-core").projectDir = file("../../shared-kmp/domain/search")
project(":catalog-core").projectDir = file("../../shared-kmp/domain/catalog")
project(":account-core").projectDir = file("../../shared-kmp/domain/account")
project(":account-presentation").projectDir = file("../../shared-kmp/presentation/account")
project(":catalog-presentation").projectDir = file("../../shared-kmp/presentation/catalog")
project(":design-system").projectDir = file("../../shared-kmp/presentation/design-system")
project(":hig").projectDir = file("../../hig")
