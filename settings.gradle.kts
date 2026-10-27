@file:Suppress("UnstableApiUsage")

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            val version = requested.version
            when (requested.id.id) {
                "com.android.application",
                "com.android.library",
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
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://maven.aliyun.com/repository/public") }
    }
}

// F-Droid doesn't support foojay-resolver plugin
// plugins {
//     id("org.gradle.toolchains.foojay-resolver-convention") version("1.0.0")
// }

rootProject.name = "convx"
include(":app")
include(":canvas")
include(":innertube")
project(":innertube").projectDir = file("shared-kmp/data/innertube")
include(":lyrics")
include(":integrations")
include(":spotify")
include(":artwork")
include(":network-storage")
include(":network-storage-core")
project(":network-storage-core").projectDir = file("shared-kmp/domain/network-storage")
include(":network-storage-transports")
project(":network-storage-transports").projectDir = file("shared-kmp/data/network-storage")
include(":network-storage-presentation")
project(":network-storage-presentation").projectDir = file("shared-kmp/presentation/network-storage")
include(":spine")
include(":modules-core")
project(":modules-core").projectDir = file("shared-kmp/domain/modules")
include(":hig")
include(":lyrics-core", ":canvas-core", ":playback-core", ":playback-presentation", ":history-core", ":library-core", ":library-data", ":search-core", ":catalog-core", ":account-core", ":account-presentation", ":catalog-presentation", ":design-system")
project(":lyrics-core").projectDir = file("shared-kmp/domain/lyrics")
project(":canvas-core").projectDir = file("shared-kmp/domain/canvas")
project(":playback-core").projectDir = file("shared-kmp/domain/playback")
project(":playback-presentation").projectDir = file("shared-kmp/presentation/playback")
project(":history-core").projectDir = file("shared-kmp/domain/history")
project(":library-core").projectDir = file("shared-kmp/domain/library")
project(":library-data").projectDir = file("shared-kmp/data/library")
project(":search-core").projectDir = file("shared-kmp/domain/search")
project(":catalog-core").projectDir = file("shared-kmp/domain/catalog")
project(":account-core").projectDir = file("shared-kmp/domain/account")
project(":account-presentation").projectDir = file("shared-kmp/presentation/account")
project(":catalog-presentation").projectDir = file("shared-kmp/presentation/catalog")
project(":design-system").projectDir = file("shared-kmp/presentation/design-system")

// Use a local copy of NewPipe Extractor by uncommenting the lines below.
// We assume, that convx and NewPipe Extractor have the same parent directory.
// If this is not the case, please change the path in includeBuild().
//
// For this to work you also need to change the implementation in shared-kmp/data/innertube/build.gradle.kts
// to one which does not specify a version.
// From:
//      implementation(libs.newpipe.extractor)
// To:
//      implementation("com.github.teamnewpipe:NewPipeExtractor")
//includeBuild("../NewPipeExtractor") {
//    dependencySubstitution {
//        substitute(module("com.github.teamnewpipe:NewPipeExtractor")).using(project(":extractor"))
//    }
//}
