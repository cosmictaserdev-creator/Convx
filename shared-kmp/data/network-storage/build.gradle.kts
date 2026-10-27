plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.kotlin.serialization)
}

group = "com.convx.shared.data"
version = "0.1.0"
kotlin {
    android {
        namespace = "com.convx.storage.transports"
        compileSdk = 37
        minSdk = 26
    }
    iosArm64()
    iosSimulatorArm64()
    jvm("desktop")
    sourceSets {
        commonMain.dependencies {
            api(project(":network-storage-core"))
            implementation(libs.kotlinx.serialization.json)
            implementation("io.coil-kt.coil3:coil-core:${libs.versions.coil.get()}")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }
        val jvmSharedMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                api("com.squareup.okhttp3:okhttp:4.12.0")
                implementation("com.hierynomus:smbj:0.15.0")
            }
        }
        androidMain.get().dependsOn(jvmSharedMain)
        getByName("desktopMain").dependsOn(jvmSharedMain)
        getByName("desktopTest").dependencies {
            implementation(kotlin("test-junit"))
            implementation("junit:junit:4.13.2")
            implementation("com.squareup.okhttp3:mockwebserver:4.12.0")
        }
    }
}
