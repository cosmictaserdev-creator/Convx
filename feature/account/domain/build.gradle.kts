plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
}

group = "com.convx.shared"
version = "0.1.0"

kotlin {
    android {
        namespace = "com.convx.shared.account"
        compileSdk = 37
        minSdk = 26
    }

    iosArm64()
    iosSimulatorArm64()
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
