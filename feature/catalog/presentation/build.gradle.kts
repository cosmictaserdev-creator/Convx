plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.convx.music.shared.catalog.presentation"
    compileSdk = 37
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin { jvmToolchain(21) }

dependencies {
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
    testImplementation(libs.junit)
    testImplementation(kotlin("test-junit"))
    testImplementation(libs.kotlinx.coroutines.test)
}
