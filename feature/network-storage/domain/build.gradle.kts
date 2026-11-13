plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.convx.storage.core"
    compileSdk = 37
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin { jvmToolchain(21) }

dependencies {
    api(libs.kotlinx.coroutines.core)
    implementation("io.ktor:ktor-http:${libs.versions.ktor.get()}")
    api("org.jetbrains.kotlinx:kotlinx-io-core:0.8.2")
    testImplementation(libs.junit)
    testImplementation(kotlin("test-junit"))
    testImplementation(libs.kotlinx.coroutines.test)
}
