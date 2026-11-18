import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

val versionProperties = Properties().apply { rootProject.file("version.properties").inputStream().use(::load) }
val major = versionProperties.getProperty("VERSION_MAJOR").toInt()
val minor = versionProperties.getProperty("VERSION_MINOR").toInt()
val patch = versionProperties.getProperty("VERSION_PATCH").toInt()
val signingProperties = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use(::load)
}

android {
    namespace = "com.convx.music.wear"
    compileSdk = 37
    defaultConfig {
        applicationId = "com.convx.music"
        minSdk = 30
        targetSdk = 36
        val nightlyRun = providers.gradleProperty("nightlyRun").orNull?.toIntOrNull() ?: 0
        require(nightlyRun in 0..9_998)
        versionCode = (major * 10_000 + minor * 100 + patch) * 10_000 + if (nightlyRun == 0) 9_999 else nightlyRun
        versionName = "$major.$minor.$patch"
    }
    signingConfigs {
        create("phoneRelease") {
            storeFile = rootProject.file("app/keystore/release.keystore")
            storePassword = signingProperties.getProperty("STORE_PASSWORD") ?: System.getenv("STORE_PASSWORD")
            keyAlias = signingProperties.getProperty("KEY_ALIAS") ?: System.getenv("KEY_ALIAS")
            keyPassword = signingProperties.getProperty("KEY_PASSWORD") ?: System.getenv("KEY_PASSWORD")
        }
    }
    buildTypes {
        debug { applicationIdSuffix = ".debug" }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            if (rootProject.file("app/keystore/release.keystore").exists()) signingConfig = signingConfigs.getByName("phoneRelease")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }
    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin { jvmToolchain(21) }
base.archivesName.set("ConvxWear")

dependencies {
    implementation(project(":playback-core"))
    implementation(libs.activity)
    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui)
    implementation(libs.play.services.wearable)
    implementation(libs.coroutines.play.services)
    implementation(libs.koin.core)
}
