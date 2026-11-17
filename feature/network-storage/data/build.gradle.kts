plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.convx.storage.transports"
    compileSdk = 37
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin { jvmToolchain(21) }

dependencies {
    api(project(":network-storage-core"))
    implementation(libs.kotlinx.serialization.json)
    implementation("io.coil-kt.coil3:coil-core:${libs.versions.coil.get()}")
    api("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.hierynomus:smbj:0.15.0")
    testImplementation(libs.junit)
    testImplementation(kotlin("test-junit"))
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}
