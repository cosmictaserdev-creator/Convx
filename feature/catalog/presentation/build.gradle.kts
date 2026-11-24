plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

group = "com.convx.shared.presentation"
version = "0.1.0"

kotlin {
    android {
        namespace = "com.convx.music.shared.catalog.presentation"
        compileSdk = 37
        minSdk = 26
    }

    iosArm64()
    iosSimulatorArm64()
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            api(project(":catalog-core"))
            api(project(":library-core"))
            api(project(":playback-core"))
            implementation(project(":playback-presentation"))
            api(libs.kotlinx.coroutines.core)
            api(libs.koin.core)
            api(libs.compose.runtime)
            api(libs.compose.ui)
            implementation(project(":design-system"))
            implementation(libs.compose.multiplatform.animation)
            implementation(libs.compose.foundation)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.koin.compose)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
