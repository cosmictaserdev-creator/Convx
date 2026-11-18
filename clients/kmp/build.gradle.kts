import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.kmp.library) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

subprojects {
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.configure<KotlinMultiplatformExtension> {
            jvmToolchain(21)
        }
    }
}

val kyantShimPaths = setOf(":kyant-shapes", ":kyant-capsule", ":kyant-backdrop", ":sfsymbols-shim")
allprojects {
    if (path !in kyantShimPaths) {
        configurations.configureEach {
            resolutionStrategy.dependencySubstitution {
                substitute(module("io.github.kyant0:backdrop")).using(project(":kyant-backdrop"))
                substitute(module("io.github.kyant0:shapes")).using(project(":kyant-shapes"))
                substitute(module("zone.ien.capsule:capsule")).using(project(":kyant-capsule"))
                substitute(module("com.github.cosmictaserdev-creator.Jetpack_SF_Symbols:sfsymbols")).using(project(":sfsymbols-shim"))
            }
        }
    }
}
