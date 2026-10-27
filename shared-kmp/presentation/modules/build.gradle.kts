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
        namespace = "com.convx.music.modules.presentation"
        compileSdk = 37
        minSdk = 26
    }
    iosArm64()
    iosSimulatorArm64()
    jvm("desktop")
    sourceSets {
        commonMain.dependencies {
            api(project(":modules-core"))
            implementation(project(":design-system"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
        }
    }
}
