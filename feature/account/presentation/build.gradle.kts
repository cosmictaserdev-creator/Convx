plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.convx.music.account.presentation"
    compileSdk = 37
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin { jvmToolchain(21) }

dependencies {
    api(project(":account-core"))
    api(libs.kotlinx.coroutines.core)
    api(libs.compose.runtime)
    api(libs.compose.foundation)
    api(libs.compose.ui)
    implementation(project(":design-system"))
    implementation(project(":hig"))
    implementation(libs.compose.multiplatform.animation)
    implementation(libs.koin.core)
    testImplementation(libs.junit)
    testImplementation(kotlin("test-junit"))
    testImplementation(libs.kotlinx.coroutines.test)
}
