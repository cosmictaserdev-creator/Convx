import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

// Single source of truth for the app version — see version.properties and the
// bumpVersion task at the bottom of this file. versionCode is DERIVED, not hand-set,
// so it can never drift from versionName the way it did before (versionCode 5 sat
// next to versionName "1.5.1" with no relationship between them at all).
val versionProps = Properties().apply {
    load(rootProject.file("version.properties").inputStream())
}
val appVersionMajor = versionProps.getProperty("VERSION_MAJOR").toInt()
val appVersionMinor = versionProps.getProperty("VERSION_MINOR").toInt()
val appVersionPatch = versionProps.getProperty("VERSION_PATCH").toInt()
// 1.5.1 -> 10501. Room for MINOR/PATCH up to 99 each before a scheme change is needed.
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
    namespace = "com.convxy.music"
    compileSdk = 37
    ndkVersion = "27.0.12077973"

    defaultConfig {
        applicationId = "com.convxy.music"
        minSdk = 26
        targetSdk = 36
        versionCode = appVersionCode
        versionName = appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true

        // LastFM API keys from local.properties or environment variables
        val lastFmKey = localProperties.getProperty("LASTFM_API_KEY") ?: System.getenv("LASTFM_API_KEY") ?: ""
        val lastFmSecret = localProperties.getProperty("LASTFM_SECRET") ?: System.getenv("LASTFM_SECRET") ?: ""

        buildConfigField("String", "LASTFM_API_KEY", "\"$lastFmKey\"")
        buildConfigField("String", "LASTFM_SECRET", "\"$lastFmSecret\"")

        // SoundCloud application credentials, for the timestamped-comments source. Same
        // local.properties-or-environment pattern as Last.fm above, and optional in exactly the same
        // way: an empty value means the source reports itself unconfigured and the comments sheet
        // says so, rather than the build failing or the feature inventing data. A user can also set
        // these at runtime in Settings → Integrations, which takes precedence.
        val soundCloudClientId = localProperties.getProperty("SOUNDCLOUD_CLIENT_ID")
            ?: System.getenv("SOUNDCLOUD_CLIENT_ID") ?: ""
        val soundCloudClientSecret = localProperties.getProperty("SOUNDCLOUD_CLIENT_SECRET")
            ?: System.getenv("SOUNDCLOUD_CLIENT_SECRET") ?: ""
        buildConfigField("String", "SOUNDCLOUD_CLIENT_ID", "\"$soundCloudClientId\"")
        buildConfigField("String", "SOUNDCLOUD_CLIENT_SECRET", "\"$soundCloudClientSecret\"")

//add nightly build label support
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
        }
        create("arm64") {
            dimension = "abi"
            buildConfigField("String", "ARCHITECTURE", "\"arm64\"")
            ndk { abiFilters.add("arm64-v8a") }
        }
        create("armeabi") {
            dimension = "abi"
            buildConfigField("String", "ARCHITECTURE", "\"armeabi\"")
            ndk { abiFilters.add("armeabi-v7a") }
        }
        create("x86") {
            dimension = "abi"
            buildConfigField("String", "ARCHITECTURE", "\"x86\"")
            ndk { abiFilters.add("x86") }
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
            buildConfigField("String", "ARCHITECTURE", "\"release\"")
        }
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
            buildConfigField("String", "ARCHITECTURE", "\"debug\"")
        }
        create("optimized") {
            // Installable performance build for CI distribution: fully minified
            // and resource-shrunk like release, but signed with the debug key so
            // workflow artifacts install without a release keystore. Not
            // debuggable — avoids the debug-build interpreter/JIT overhead that
            // made the distributed test builds lag.
            isMinifyEnabled = true
            isShrinkResources = true
            isCrunchPngs = false
            isDebuggable = false
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "ARCHITECTURE", "\"optimized\"")
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
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

    implementation(libs.media3)
    implementation(libs.media3.session)
    implementation(libs.media3.hls)
    implementation(libs.media3.ui)
    implementation(libs.media3.okhttp)

    // Google Cast - only included in GMS flavor (not available in F-Droid/FOSS builds)
    "gmsImplementation"(libs.media3.cast)
    "gmsImplementation"(libs.mediarouter)
    "gmsImplementation"(libs.cast.framework)

    implementation(libs.room.runtime)
    implementation(libs.kuromoji.ipadic)
    implementation(libs.tinypinyin)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)

    implementation(libs.apache.lang3)

    implementation(libs.hilt)
    implementation(libs.jsoup)
    ksp(libs.hilt.compiler)

    implementation(project(":innertube"))
    implementation(project(":kugou"))
    implementation(project(":lrclib"))
    implementation(project(":kizzy"))
    implementation(project(":lastfm"))
    implementation(project(":betterlyrics"))
    implementation(project(":simpmusic"))
    implementation(project(":youlyplus"))
    implementation(project(":canvas"))
    implementation(project(":shazamkit"))
    implementation(project(":artistvideo"))
    implementation(project(":applecanvas"))
    implementation(project(":vivimusiccanvas"))
    implementation(project(":paxsenixlyrics"))
    implementation(project(":musixmatchlyrics"))
    implementation(project(":jiosaavn"))
    implementation(project(":spotify"))
    implementation(project(":spine"))


    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.json)

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
