plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.kotlin.serialization)
}

group = "com.convx.shared"
version = "0.1.0"

kotlin {
    android {
        namespace = "com.convx.shared.lyrics.data"
        compileSdk = 37
        minSdk = 26
    }

    iosArm64()
    iosSimulatorArm64()
    jvm("desktop")

    sourceSets {
        val jvmMain = create("jvmMain") {
            dependsOn(getByName("commonMain"))
            dependencies {
                implementation(libs.ktor.client.okhttp)
            }
        }
        val iosMain = create("iosMain") {
            dependsOn(getByName("commonMain"))
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
        getByName("androidMain").dependsOn(jvmMain)
        getByName("desktopMain").dependsOn(jvmMain)
        getByName("iosArm64Main").dependsOn(iosMain)
        getByName("iosSimulatorArm64Main").dependsOn(iosMain)

        commonMain.dependencies {
            api(project(":lyrics-core"))
            api(libs.kotlinx.coroutines.core)
            implementation(project(":innertube"))
            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.atomicfu)
            implementation(libs.ksoup)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
