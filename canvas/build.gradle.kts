plugins {
    alias(libs.plugins.kotlin.serialization)
    id("kotlin")
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.json)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.12.0")
    implementation(libs.ktor.client.encoding)
    testImplementation(libs.junit)
}
