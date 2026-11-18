plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
}
kotlin {
    iosArm64(); iosSimulatorArm64(); iosX64()
    sourceSets {
        val skikoMain by creating { dependsOn(commonMain.get()) }
        iosArm64Main.get().dependsOn(skikoMain)
        iosSimulatorArm64Main.get().dependsOn(skikoMain)
        iosX64Main.get().dependsOn(skikoMain)
        commonMain.dependencies {
            api(compose.runtime); api(compose.foundation); api(compose.ui)
            api(project(":kyant-shapes"))
            implementation("org.jetbrains:annotations:26.0.2")
        }
    }
}
