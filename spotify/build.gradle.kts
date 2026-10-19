plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.serialization)
}

val nativeJniOut = layout.buildDirectory.dir("generated/jniLibs")

android {
    namespace = "com.convx.spotify"
    sourceSets { getByName("main") { jniLibs.srcDir(file("build/generated/jniLibs")) } }
    compileSdk = 37
    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin { jvmToolchain(21) }

// Native Spotify engine (Rust, librespot). Built with the project-local toolchain in .tools when it
// exists; otherwise the task is skipped and the app runs without Spotify playback.
val cargoHome = rootProject.file(".tools/cargo")
val rustupHome = rootProject.file(".tools/rustup")
val cargoExe = File(cargoHome, "bin/cargo.exe").takeIf { it.exists() } ?: File(cargoHome, "bin/cargo")
val androidSdk = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
    ?: File(System.getProperty("user.home"), "AppData/Local/Android/Sdk").path
val ndkDir = File(androidSdk, "ndk").listFiles()?.filter { it.isDirectory }?.maxByOrNull { it.name }
// The old C:\MinGW assembler cannot target x86-64; the MSYS2 one can.
val nativePath = (listOf("C:\\msys64\\mingw64\\bin", File(cargoHome, "bin").path) +
    System.getenv("PATH").orEmpty().split(File.pathSeparator).filterNot { it.equals("C:\\MinGW\\bin", true) })
    .joinToString(File.pathSeparator)
val buildSpotifyNative = tasks.register<Exec>("buildSpotifyNative") {
    // A plain property, not an onlyIf lambda: the lambda would capture the script object, which the
    // configuration cache cannot store.
    enabled = cargoExe.exists() && File(cargoHome, "bin/cargo-ndk.exe").exists() && ndkDir != null
    workingDir = file("native")
    inputs.dir("native/src")
    inputs.files("native/Cargo.toml", "native/Cargo.lock")
    outputs.dir(nativeJniOut)
    environment("PATH", nativePath)
    environment("CARGO_HOME", cargoHome.path)
    environment("RUSTUP_HOME", rustupHome.path)
    environment("ANDROID_NDK_HOME", ndkDir?.path ?: "")
    commandLine(cargoExe.path, "ndk", "-t", "arm64-v8a", "-o", nativeJniOut.get().asFile.path, "build", "--release")
}
tasks.matching { it.name.startsWith("merge") && it.name.endsWith("JniLibFolders") }.configureEach { dependsOn(buildSpotifyNative) }

dependencies {
    implementation(libs.webkit)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.client.encoding)
    implementation(libs.json)
    testImplementation(libs.junit)
    coreLibraryDesugaring(libs.desugaring)
}
