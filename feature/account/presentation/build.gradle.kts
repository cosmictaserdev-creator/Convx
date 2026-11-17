plugins {
    alias(libs.plugins.android.library)
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
    implementation(libs.koin.core)
    testImplementation(libs.junit)
    testImplementation(kotlin("test-junit"))
    testImplementation(libs.kotlinx.coroutines.test)
}
