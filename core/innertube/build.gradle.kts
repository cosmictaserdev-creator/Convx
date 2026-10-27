plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    android {
        namespace = "com.music.innertube"
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
                implementation(libs.newpipeextractor)
            }
        }
        val iosMain = create("iosMain") {
            dependsOn(getByName("commonMain"))
        }

        commonMain.dependencies {
            api(project(":account-core"))
            api(project(":catalog-core"))
            api(project(":library-core"))
            api(project(":playback-core"))
            api(libs.ktor.client.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.encoding)
            implementation(libs.ktor.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
            api(libs.koin.core)
        }

        getByName("androidMain").dependsOn(jvmMain)
        getByName("iosArm64Main").dependsOn(iosMain)
        getByName("iosSimulatorArm64Main").dependsOn(iosMain)

        getByName("desktopMain") {
            dependsOn(jvmMain)
            dependencies {
                implementation(libs.ktor.client.cio)
            }
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        getByName("desktopTest").dependencies {
            implementation(kotlin("test"))
            implementation(libs.junit)
        }
    }
}
