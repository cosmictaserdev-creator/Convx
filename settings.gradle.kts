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
project(":innertube").projectDir = file("core/innertube")
include(":lyrics-data")
project(":lyrics-data").projectDir = file("feature/lyrics/data")
include(":integrations")
include(":spotify")
include(":artwork")
include(":network-storage")
include(":network-storage-core")
project(":network-storage-core").projectDir = file("feature/network-storage/domain")
include(":network-storage-transports")
project(":network-storage-transports").projectDir = file("feature/network-storage/data")
include(":network-storage-presentation")
project(":network-storage-presentation").projectDir = file("feature/network-storage/presentation")
include(":spine")
include(":modules-core")
project(":modules-core").projectDir = file("feature/modules/domain")
include(":hig")
project(":hig").projectDir = file("core/hig")
include(":listen-together-core")
project(":listen-together-core").projectDir = file("feature/listen-together/domain")
include(":lyrics-core", ":canvas-core", ":playback-core", ":playback-presentation", ":history-core", ":library-core", ":library-data", ":search-core", ":catalog-core", ":account-core", ":account-presentation", ":catalog-presentation", ":design-system")
project(":lyrics-core").projectDir = file("feature/lyrics/domain")
project(":canvas-core").projectDir = file("feature/canvas/domain")
project(":playback-core").projectDir = file("feature/playback/domain")
project(":playback-presentation").projectDir = file("feature/playback/presentation")
project(":history-core").projectDir = file("feature/history/domain")
project(":library-core").projectDir = file("feature/library/domain")
project(":library-data").projectDir = file("feature/library/data")
project(":search-core").projectDir = file("feature/search/domain")
project(":catalog-core").projectDir = file("feature/catalog/domain")
project(":account-core").projectDir = file("feature/account/domain")
project(":account-presentation").projectDir = file("feature/account/presentation")
project(":catalog-presentation").projectDir = file("feature/catalog/presentation")
project(":design-system").projectDir = file("core/design-system")

// Use a local copy of NewPipe Extractor by uncommenting the lines below.
// We assume, that convx and NewPipe Extractor have the same parent directory.
// If this is not the case, please change the path in includeBuild().
//
// For this to work you also need to change the implementation in core/innertube/build.gradle.kts
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
