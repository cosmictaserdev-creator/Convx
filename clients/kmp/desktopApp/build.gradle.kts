import org.gradle.api.tasks.Copy
import org.gradle.jvm.tasks.Jar

plugins {
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

val javaFxPlatform = when {
    System.getProperty("os.name").startsWith("Windows", ignoreCase = true) -> "win"
    System.getProperty("os.name").startsWith("Mac", ignoreCase = true) ->
        if (System.getProperty("os.arch").lowercase() in setOf("aarch64", "arm64")) "mac-aarch64" else "mac"
    else -> "linux"
}
val ffmpegPlatform = when {
    System.getProperty("os.name").startsWith("Windows", ignoreCase = true) -> "windows-x86_64"
    System.getProperty("os.name").startsWith("Mac", ignoreCase = true) ->
        if (System.getProperty("os.arch").lowercase() in setOf("aarch64", "arm64")) "macosx-arm64" else "macosx-x86_64"
    else ->
        if (System.getProperty("os.arch").lowercase() in setOf("aarch64", "arm64")) "linux-arm64" else "linux-x86_64"
}
val javaFxVersion = libs.versions.javafx.get()

kotlin {
    jvmToolchain(21)
}

tasks.named<Copy>("processResources") {
    from(rootProject.file("shared/src/commonMain/composeResources/files/po_token.html"))
}

dependencies {
    implementation(libs.nucleus.macos.ui)
    implementation("dev.nucleusframework:nucleus.decorated-window-tao:2.5.2")
    implementation(project(":shared"))
    implementation(project(":network-storage-transports"))
    implementation(project(":network-storage-presentation"))
    implementation(project(":modules-runtime"))
    implementation(project(":modules-presentation"))
    implementation(project(":account-core"))
    implementation(project(":account-presentation"))
    implementation(project(":library-data"))
    implementation(project(":catalog-presentation"))
    implementation(project(":innertube"))
    implementation(project(":playback-core"))
    implementation(project(":playback-data"))
    implementation(project(":playback-presentation"))
    implementation(project(":lyrics-data"))
    implementation(project(":canvas"))
    implementation(project(":design-system"))
    implementation(project(":hig"))
    implementation(libs.haze)
    implementation(libs.haze.blur)
    implementation(libs.haze.blur.materials)
    implementation(libs.room.runtime)
    implementation(libs.koin.compose)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.swing)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.jna)
    implementation(libs.jetpackSfSymbols)
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
    implementation("sh.calvin.reorderable:reorderable:3.0.0")
    implementation("org.bytedeco:javacv:1.5.11")
    runtimeOnly("org.bytedeco:ffmpeg:7.1-1.5.11:$ffmpegPlatform")
    implementation("org.openjfx:javafx-base:$javaFxVersion:$javaFxPlatform")
    implementation("org.openjfx:javafx-graphics:$javaFxVersion:$javaFxPlatform")
    implementation("org.openjfx:javafx-controls:$javaFxVersion:$javaFxPlatform")
    implementation("org.openjfx:javafx-media:$javaFxVersion:$javaFxPlatform")
    implementation("org.openjfx:javafx-swing:$javaFxVersion:$javaFxPlatform")
    implementation("org.openjfx:javafx-web:$javaFxVersion:$javaFxPlatform")
    implementation(compose.desktop.currentOs)
    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation("org.jetbrains.compose.ui:ui-test-junit4:${libs.versions.composeMultiplatform.get()}")
}

compose.desktop {
    application {
        mainClass = "com.convx.music.client.desktop.MainKt"
    }
}

tasks.withType<Jar>().matching { it.name.endsWith("UberJarForCurrentOS") }.configureEach {
    // Dependency signatures describe their original manifests, not the merged application JAR.
    exclude("META-INF/*.SF", "META-INF/*.RSA", "META-INF/*.DSA", "META-INF/*.EC")
}
