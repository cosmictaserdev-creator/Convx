plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.kotlin.serialization)
}
group = "com.convx.shared"
version = "0.1.0"
kotlin {
    android {
        namespace = "com.convx.music.modules.core"
        compileSdk = 37
        minSdk = 26
    }
    iosArm64()
    iosSimulatorArm64()
    jvm("desktop")
    sourceSets {
        commonMain.dependencies {
            api(project(":playback-core"))
            api(libs.kotlinx.coroutines.core)
            api(libs.kotlinx.serialization.json)
            api(libs.ktor.client.core)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
