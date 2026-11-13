plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.convx.shared.lyrics.data"
    compileSdk = 37
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin { jvmToolchain(21) }

dependencies {
    api(project(":lyrics-core"))
    api(libs.kotlinx.coroutines.core)
    implementation(project(":innertube"))
    implementation(libs.ktor.client.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.atomicfu)
    implementation(libs.ksoup)
    implementation(libs.ktor.client.okhttp)
    testImplementation(libs.junit)
    testImplementation(kotlin("test-junit"))
}
