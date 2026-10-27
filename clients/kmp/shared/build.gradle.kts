plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

compose.resources {
    packageOfResClass = "com.convx.music.client.shared.generated.resources"
    generateResClass = always
}

kotlin {
    android {
        namespace = "com.convx.music.client.shared"
        compileSdk = 37
        minSdk = 26
    }

    val iosTargets = listOf(iosArm64(), iosSimulatorArm64())
    jvm("desktop")

    iosTargets.forEach { target ->
        target.compilations.getByName("main").cinterops.create("avPlayerCompat") {
            defFile(project.file("src/iosMain/nativeInterop/cinterop/av-player-compat.def"))
            compilerOpts("-I${project.file("src/iosMain/nativeInterop/cinterop").absolutePath}")
        }
        target.compilations.getByName("main").cinterops.create("equalizerAudioTapCompat") {
            defFile(project.file("src/iosMain/nativeInterop/cinterop/equalizer-audio-tap-compat.def"))
            compilerOpts("-I${project.file("src/iosMain/nativeInterop/cinterop").absolutePath}")
        }
        target.compilations.getByName("main").cinterops.create("keychainCompat") {
            defFile(project.file("src/iosMain/nativeInterop/cinterop/keychain-compat.def"))
            compilerOpts("-I${project.file("src/iosMain/nativeInterop/cinterop").absolutePath}")
        }
    }

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().configureEach {
        binaries.framework {
            baseName = "ConvxShared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":catalog-presentation"))
                api(project(":network-storage-core"))
                api(project(":network-storage-transports"))
                api(project(":network-storage-presentation"))
                api(project(":modules-runtime"))
                api(project(":modules-presentation"))
                implementation(project(":account-presentation"))
                implementation(project(":innertube"))
                implementation(project(":playback-core"))
                implementation(project(":playback-data"))
                implementation(project(":playback-presentation"))
                implementation(project(":library-data"))
                implementation(libs.compose.runtime)
                implementation(libs.compose.ui)
                implementation(compose.components.resources)
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.atomicfu)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.room.runtime)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
            }
        }
        iosMain {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
    }
}
