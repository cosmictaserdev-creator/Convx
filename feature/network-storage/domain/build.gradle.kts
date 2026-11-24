plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
}

group = "com.convx.shared"
version = "0.1.0"
kotlin {
    android {
        namespace = "com.convx.storage.core"
        compileSdk = 37
        minSdk = 26
    }
    iosArm64()
    iosSimulatorArm64()
    jvm("desktop")
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.coroutines.core)
            implementation("io.ktor:ktor-http:${libs.versions.ktor.get()}")
            api("org.jetbrains.kotlinx:kotlinx-io-core:0.8.2")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
