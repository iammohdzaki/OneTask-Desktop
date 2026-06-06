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
            modules(
                "java.sql", 
                "jdk.unsupported", 
                "java.naming", 
                "java.management", 
                "jdk.crypto.ec"
            )
            packageName = "OneTask"
            packageVersion = project.findProperty("appVersion")?.toString() ?: "1.0.0"
            vendor = "OneKore"
            copyright = "Copyright (c) 2026 OneKore. All rights reserved."

            buildTypes {
                release {
                    proguard {
                        isEnabled.set(true)
                        optimize.set(true)
                        obfuscate.set(false) // Obfuscation can be tricky with Compose, starting with just shrinking
                        configurationFiles.from(project.file("proguard-rules.pro"))
                    }
                }
            }

            windows {
                iconFile.set(project.file("metadata/icon.ico"))
                menu = true // Add to Start Menu
                shortcut = true // Add to Desktop
                // Unique GUID for Windows updates/uninstalls
                upgradeUuid = "a3a63a91-3cfc-4fa1-8d60-0b657639ade5"
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

tasks.withType<JavaExec> {
    if (name == "run") {
        jvmArgs("-Donetask.debug=true")
    }
}