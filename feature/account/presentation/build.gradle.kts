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
        namespace = "com.convx.music.account.presentation"
        compileSdk = 37
        minSdk = 26
    }

    iosArm64()
    iosSimulatorArm64()
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            api(project(":account-core"))
            api(compose.runtime)
            api(compose.foundation)
            api(compose.ui)
            implementation(compose.animation)
            implementation(project(":design-system"))
            implementation(project(":hig"))
            api(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
