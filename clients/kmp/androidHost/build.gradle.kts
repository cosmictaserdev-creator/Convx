plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
}

android {
    namespace = "com.convx.music.client.androidhost"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.convx.music.client.androidhost"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    sourceSets["main"].assets.directories.add(
        rootProject.file("shared/src/commonMain/composeResources/files").absolutePath,
    )
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":modules-runtime"))
    implementation(project(":modules-presentation"))
    implementation(project(":catalog-presentation"))
    implementation(project(":innertube"))
    implementation(project(":playback-core"))
    implementation(project(":playback-data"))
    implementation(project(":playback-presentation"))
    implementation(project(":account-core"))
    implementation(project(":account-presentation"))
    implementation(project(":library-data"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.webkit)
    implementation(libs.media3.exoplayer)
    implementation(libs.koin.compose)
    implementation(libs.room.runtime)
    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
}
