plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvm("desktop")
    sourceSets {
        commonMain.dependencies {
            api(project(":lyrics-core"))
            api(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies { implementation(kotlin("test")) }
        val desktopMain by getting {
            kotlin.srcDir("../../../lyrics/src/main/kotlin")
            kotlin.include("com/convx/music/lyrics/bitchord/**", "com/convx/music/lyrics/data/**")
            dependencies {
                implementation(project(":innertube"))
                implementation(libs.ktor.client.okhttp)
                implementation(libs.kotlinx.serialization.json)
                implementation("org.jsoup:jsoup:1.22.1")
            }
        }
    }
}
