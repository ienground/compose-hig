/*
 * Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
 * Copyright (c) 2025. Scott Lanoue.
 * Copyright (c) 2026. IENGROUND of IENLAB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
    id(libs.plugins.android.kotlin.multiplatform.library.get().pluginId)
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
}

kotlin {
    androidLibrary {
        namespace = "zone.ien.hig.example.lib"
        compileSdk = (findProperty("android.compileSdk") as String).toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }

        androidResources {
            enable = true
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true

            export(libs.decompose.core)
            export(libs.essenty)
            export("com.arkivanov.essenty:lifecycle:${libs.versions.essenty}")
        }
    }

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "composeApp"
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer =
                    (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                        static =
                            (static ?: mutableListOf()).apply {
                                // Serve sources to debug inside browser
                                add(project.rootDir.path)
                                add(project.projectDir.path)
                            }
                    }
                sourceMaps = true
            }
        }
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting
        val desktopMain by getting

        commonMain.dependencies {
            implementation(projects.hig)
            implementation(projects.higAdaptive)
            implementation(projects.higNative)
            implementation(projects.higDecompose)
            implementation(projects.higIconsExtended)
            implementation(libs.material.kolor)

            api(libs.decompose.core)
            api(libs.essenty)
            implementation(libs.decompose.compose)
            implementation(libs.compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(libs.datetime)
            implementation(libs.serialization)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }

    compilerOptions.freeCompilerArgs.add("-Xopt-in=kotlin.time.ExperimentalTime")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "zone.ien.hig.example"
            packageVersion = "1.0.0"
        }
    }
}
