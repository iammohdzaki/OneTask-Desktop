import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(projects.shared)
    implementation(libs.compose.components.resources)
    implementation(compose.desktop.currentOs)
    implementation(libs.compose.material3)
    implementation(libs.compose.icons.extended)
    implementation(libs.kotlinx.coroutinesSwing)
    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "com.one.task.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
            packageName = "com.one.task"
            packageVersion = project.findProperty("appVersion")?.toString() ?: "1.0.0"
            windows {
                iconFile.set(project.file("metadata/icon.ico"))
            }
            macOS {
                iconFile.set(project.file("metadata/icon.icns"))
            }
            linux {
                iconFile.set(project.file("metadata/icon.png"))
            }
        }
    }
}