plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.convx.music.modules.runtime"
    compileSdk = 37
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin { jvmToolchain(21) }

dependencies {
    api(project(":modules-core"))
    implementation("io.github.dokar3:quickjs-kt:1.0.15")
    implementation(libs.kotlinx.atomicfu)
    implementation(libs.ktor.client.okhttp)
    testImplementation(libs.junit)
    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.test)
}
