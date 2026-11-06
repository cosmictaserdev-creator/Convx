import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

// Derive versionCode from version.properties so it stays in sync with versionName.
val versionProps = Properties().apply {
    load(rootProject.file("version.properties").inputStream())
}
val appVersionMajor = versionProps.getProperty("VERSION_MAJOR").toInt()
val appVersionMinor = versionProps.getProperty("VERSION_MINOR").toInt()
val appVersionPatch = versionProps.getProperty("VERSION_PATCH").toInt()
// The source version uses two decimal digits each for MINOR and PATCH; updater builds reserve four low digits for the channel run.
val appVersionCode = appVersionMajor * 10_000 + appVersionMinor * 100 + appVersionPatch
val appVersionName = "$appVersionMajor.$appVersionMinor.$appVersionPatch"

plugins {
    id("com.android.application")
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.protobufPlugin)
}

android {
    namespace = "com.convx.music"
    compileSdk = 37
    ndkVersion = "27.0.12077973"

    defaultConfig {
        applicationId = "com.convx.music"
        minSdk = 26
        targetSdk = 36
        val nightlyRunForVersion = (project.findProperty("nightlyRun") as String?)?.toIntOrNull() ?: 0
        require(nightlyRunForVersion in 0..9_998) { "Nightly run number exceeds the updater version-code range" }
        versionCode = appVersionCode * 10_000 + if (nightlyRunForVersion == 0) 9_999 else nightlyRunForVersion
        versionName = appVersionName
        buildConfigField("Boolean", "GITHUB_UPDATER_ENABLED", (project.findProperty("githubUpdater") == "true").toString())

        testInstrumentationRunner = providers.gradleProperty("convxTestRunner")
            .orElse("androidx.test.runner.AndroidJUnitRunner").get()
        vectorDrawables.useSupportLibrary = true

        // LastFM API keys from local.properties or environment variables
        val lastFmKey = localProperties.getProperty("LASTFM_API_KEY") ?: System.getenv("LASTFM_API_KEY") ?: ""
        val lastFmSecret = localProperties.getProperty("LASTFM_SECRET") ?: System.getenv("LASTFM_SECRET") ?: ""

        buildConfigField("String", "LASTFM_API_KEY", "\"$lastFmKey\"")
        buildConfigField("String", "LASTFM_SECRET", "\"$lastFmSecret\"")

        val isNightly = project.hasProperty("nightly") && project.property("nightly") == "true"
        buildConfigField("Boolean", "IS_NIGHTLY", isNightly.toString())
        // CI stamps the workflow run number so a nightly build knows which one it is.
        // 0 for local/stable builds, which makes any published nightly look newer.
        val nightlyRun = (project.findProperty("nightlyRun") as String?)?.toIntOrNull() ?: 0
        buildConfigField("int", "NIGHTLY_RUN", nightlyRun.toString())
    }
    

    flavorDimensions += listOf("abi", "variant")
    productFlavors {
        // FOSS variant (default) - F-Droid compatible, no Google Play Services
        create("foss") {
            dimension = "variant"
            isDefault = true
            buildConfigField("Boolean", "CAST_AVAILABLE", "false")
        }

        // GMS variant - with Google Cast support (requires Google Play Services)
        create("gms") {
            dimension = "variant"
            buildConfigField("Boolean", "CAST_AVAILABLE", "true")
        }
        
        create("universal") {
            dimension = "abi"
            buildConfigField("String", "ARCHITECTURE", "\"universal\"")
            // 64-bit only: MMKV (native KV store) ships no 32-bit binaries, so a
            // 32-bit slice would crash at load time. armeabi/x86 flavors removed.
            ndk {
                abiFilters.addAll(listOf("arm64-v8a", "x86_64"))
            }
        }
        create("arm64") {
            dimension = "abi"
            buildConfigField("String", "ARCHITECTURE", "\"arm64\"")
            ndk { abiFilters.add("arm64-v8a") }
        }
        // 32-bit-only ARM userland (e.g. Galaxy A13). MMKV 2.x has no 32-bit library,
        // so this flavor swaps in the 1.3.x line below.
        create("arm32") {
            dimension = "abi"
            buildConfigField("String", "ARCHITECTURE", "\"arm32\"")
            ndk { abiFilters.add("armeabi-v7a") }
        }
        create("x86_64") {
            dimension = "abi"
            buildConfigField("String", "ARCHITECTURE", "\"x86_64\"")
            ndk { abiFilters.add("x86_64") }
        }
    }

    signingConfigs {
        create("persistentDebug") {
            storeFile = file("persistent-debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        create("release") {
            // Signing material comes from the environment only. Never hardcode a
            // fallback password here: this file is published, the keystore is not,
            // and a leaked release password plus a leaked keystore is an
            // unrecoverable compromise of the app's signing identity.
            // Set STORE_PASSWORD / KEY_ALIAS / KEY_PASSWORD before a release build.
            // local.properties is gitignored, so it is a safe place to keep them.
            storeFile = file("keystore/release.keystore")
            storePassword = localProperties.getProperty("STORE_PASSWORD") ?: System.getenv("STORE_PASSWORD")
            keyAlias = localProperties.getProperty("KEY_ALIAS") ?: System.getenv("KEY_ALIAS")
            keyPassword = localProperties.getProperty("KEY_PASSWORD") ?: System.getenv("KEY_PASSWORD")
        }
        getByName("debug") {
            keyAlias = "androiddebugkey"
            keyPassword = "android"
            storePassword = "android"
            storeFile = file("${System.getProperty("user.home")}/.android/debug.keystore")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isCrunchPngs = false
            isDebuggable = false
            if (file("keystore/release.keystore").exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    // JVM unit tests touch framework stubs (ColorUtils in tint math). Return
    // defaults instead of throwing — Robolectric would be heavier than these
    // token tests warrant.
    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    kotlin {
        jvmToolchain(21)
        compilerOptions {
            freeCompilerArgs.add("-Xannotation-default-target=param-property")
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    lint {
        lintConfig = file("lint.xml")
        warningsAsErrors = false
        abortOnError = false
        checkDependencies = false
    }

    androidResources {
        generateLocaleConfig = true
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/analyzer/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    packaging {
        jniLibs {
            useLegacyPackaging = false
            keepDebugSymbols += listOf(
                "**/libandroidx.graphics.path.so",
                "**/libdatastore_shared_counter.so"
            )
        }
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/NOTICE.md"
            excludes += "META-INF/CONTRIBUTORS.md"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/io.netty.versions.properties"
        }
    }
}

composeCompiler {
    // :innertube ships no Compose metadata, so its models default to unstable
    // and every YouTube row/tile was unskippable. See compose_stability.conf.
    stabilityConfigurationFiles.add(
        layout.projectDirectory.file("compose_stability.conf")
    )
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.protobuf.get()}"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
                create("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=kotlin.RequiresOptIn"
        )
        suppressWarnings.set(false)
    }
}

dependencies {
    implementation(libs.androidx.material3)
    implementation(libs.guava)
    implementation(libs.coroutines.guava)
    implementation(libs.concurrent.futures)

    implementation(libs.activity)
    implementation(libs.hilt.navigation)
    implementation(libs.datastore)
    "universalImplementation"(libs.mmkv)
    "arm64Implementation"(libs.mmkv)
    "x86_64Implementation"(libs.mmkv)
    "arm32Implementation"(libs.mmkv.legacy)
    implementation(libs.webkit)

    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.util)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.animation)
    implementation(libs.compose.reorderable)

    implementation(libs.viewmodel)
    implementation(libs.viewmodel.compose)

    implementation(libs.material3)
    implementation(libs.androidx.adaptive)
    implementation(libs.androidx.adaptive.layout)
    implementation(libs.androidx.adaptive.navigation)
    implementation(libs.palette)
    implementation(libs.materialKolor)

    implementation(libs.appcompat)
    implementation(libs.exifinterface)

    implementation(libs.coil)
    implementation(libs.coil.network.okhttp)
    implementation(libs.coil.gif)
    implementation(libs.coil.svg)

    implementation(libs.ucrop)

    implementation(libs.shimmer)

    implementation(libs.haze)
    implementation(libs.haze.materials)

    // Apple-Music-style karaoke lyrics: per-syllable timing, background/accompaniment
    // lines, and left/right alignment for duets. Same renderer the reference app uses --
    // writing this by hand is ~1400 lines of text layout and interruptible scroll.
    implementation(libs.accompanist.lyrics.ui)
    implementation(libs.accompanist.lyrics.core)

    implementation(libs.media3)
    implementation(libs.media3.session)
    implementation(libs.media3.hls)
    implementation(libs.media3.ui)
    implementation(libs.media3.okhttp)
    // FFmpeg audio decoder fallback for local files the platform MediaCodec can't
    // decode (e.g. ALAC — no AOSP device ships a decoder for it). Only engages when
    // the platform has no decoder for a format; every existing decode path is untouched.
    implementation(libs.nextlib.media3ext)

    // Google Cast - only included in GMS flavor (not available in F-Droid/FOSS builds)
    "gmsImplementation"(libs.media3.cast)
    "gmsImplementation"(libs.mediarouter)
    "gmsImplementation"(libs.cast.framework)

    implementation(libs.room.runtime)
    implementation(libs.kuromoji.ipadic)
    // Vendored: JitPack serves an empty 295-byte jar for com.github.promeG:tinypinyin:2.0.3
    // (upstream build broke), so the real jar (compiled from v2.0.3 sources) + its
    // aho-corasick dep live in app/libs. Do not switch back to libs.tinypinyin.
    implementation(files("libs/tinypinyin-2.0.3.jar", "libs/ahocorasick-0.3.0.jar"))
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)

    implementation(libs.apache.lang3)

    implementation(libs.hilt)
    implementation(libs.jsoup)
    ksp(libs.hilt.compiler)

    implementation(project(":innertube"))
    implementation(project(":lyrics"))
    implementation(project(":integrations"))
    implementation(project(":spotify"))
    implementation(project(":artwork"))
    implementation(project(":network-storage"))
    implementation(project(":canvas"))
    implementation(project(":spine"))
    implementation(project(":hig"))


    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.client.encoding)

    // Protobuf for message serialization (lite version for Android)
    implementation(libs.protobuf.javalite)
    implementation(libs.protobuf.kotlin.lite)

    coreLibraryDesugaring(libs.desugaring)
    implementation(libs.timber)
    implementation(libs.smoothCorner)
    implementation(libs.lottie.compose)
    implementation(libs.androidx.material.icons.extended.v178)
    implementation(libs.work.runtime.ktx)
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.jetpackSfSymbols)

    // Smart analysis ONNX models (Beat This + Open-Unmix vocal separator)
    implementation(libs.onnxruntime)

    testImplementation(libs.junit)
}



/**
 * `./gradlew bumpVersion -Ppart=patch|minor|major` — the whole release-versioning
 * mechanism. Increments the requested part of version.properties (patch/minor reset
 * the parts below them to 0, same as semver), leaves everything else untouched, and
 * prints the tag to push. Nothing auto-commits or auto-tags: git stays a manual,
 * reviewable step.
 *
 * `part` defaults to "patch" since that's what almost every release here has been —
 * bugfix passes, not new features.
 */
tasks.register("bumpVersion") {
    group = "versioning"
    description = "Bumps version.properties (patch/minor/major) — the single source for versionCode/versionName."
    doLast {
        val part = (project.findProperty("part") as String?)?.lowercase() ?: "patch"
        var major = appVersionMajor
        var minor = appVersionMinor
        var patch = appVersionPatch
        when (part) {
            "major" -> { major += 1; minor = 0; patch = 0 }
            "minor" -> { minor += 1; patch = 0 }
            "patch" -> { patch += 1 }
            else -> throw GradleException("Unknown part '$part' — use major, minor, or patch.")
        }

        val versionFile = rootProject.file("version.properties")
        versionFile.writeText(
            "VERSION_MAJOR=$major\nVERSION_MINOR=$minor\nVERSION_PATCH=$patch\n"
        )

        val newVersionName = "$major.$minor.$patch"
        val newVersionCode = major * 10_000 + minor * 100 + patch
        println("Bumped $part: $appVersionName ($appVersionCode) -> $newVersionName ($newVersionCode)")
        println()
        println("Next steps:")
        println("  git add version.properties")
        println("  git commit -m \"chore: bump version to $newVersionName\"")
        println("  git tag v$newVersionName")
        println("  git push && git push --tags   # tag push triggers release.yml")
    }
}
