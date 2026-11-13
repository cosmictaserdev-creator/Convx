plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.convx.music.shared.playback.presentation"
    compileSdk = 37
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin { jvmToolchain(21) }

dependencies {
    api(project(":playback-core"))
    api(project(":lyrics-core"))
    api(libs.compose.runtime)
    api(libs.compose.foundation)
    api(libs.compose.ui)
    api(libs.hig.backdrop)
    implementation(project(":design-system"))
    implementation(project(":hig"))
    implementation(libs.compose.multiplatform.animation)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.ktor3)
    implementation(libs.jetpackSfSymbols)
    implementation(libs.koin.core)
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.junit)
    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.test)
}
