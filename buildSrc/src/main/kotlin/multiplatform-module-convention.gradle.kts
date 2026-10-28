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

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlin.multiplatform")
}

val projectName = name.filter { it.isLetter() }

kotlin {
    applyDefaultHierarchyTemplate()

    targets.all {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    progressiveMode.set(true)
                    // Disable warnings about expect/actual classes
                    freeCompilerArgs.addAll(
                        "-Xexpect-actual-classes",
                        "-opt-in=kotlin.RequiresOptIn"
                    )
                }
            }
        }
    }

    android {
        namespace = "zone.ien.hig${projectName}"
        compileSdk {
            version = release((findProperty("android.compileSdk") as String).toInt()) {
                minorApiLevel = (findProperty("android.compileSdkMinor") as String).toInt()
            }
        }
        minSdk = (findProperty("android.minSdk") as String).toInt()
        if (project.file("src/commonTest").isDirectory) {
            withHostTest {}
        }
        lint {
            targetSdk = (findProperty("android.targetSdk") as String).toInt()
        }

        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                    freeCompilerArgs.addAll(
                        "-opt-in=kotlin.RequiresOptIn"
                    )
                }
            }
        }
    }

    iosArm64()
    iosSimulatorArm64()
    macosArm64()

    jvm("desktop") {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                    freeCompilerArgs.addAll(
                        "-opt-in=kotlin.RequiresOptIn"
                    )
                }
            }
        }
    }

    js {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    useConfigDirectory(rootProject.file("karma.config.d"))
                }
            }
        }
        binaries.executable()
    }

    @Suppress("OPT_IN_USAGE")
    wasmJs {
        binaries.executable()
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    useConfigDirectory(rootProject.file("karma.config.d"))
                }
            }
        }
    }

    sourceSets {

        val desktopMain = getByName("desktopMain")
        val wasmJsMain = getByName("wasmJsMain")

        create("skikoMain") {
            dependsOn(commonMain.get())
            appleMain.get().dependsOn(this)
            desktopMain.dependsOn(this)
            jsMain.get().dependsOn(this)
            wasmJsMain.dependsOn(this)
        }
        create("nonIosMain") {
            dependsOn(commonMain.get())
            macosMain.get().dependsOn(this)
            androidMain.get().dependsOn(this)
            desktopMain.dependsOn(this)
            jsMain.get().dependsOn(this)
            wasmJsMain.dependsOn(this)
        }
        create("nonDarwinMain") {
            dependsOn(commonMain.get())
            androidMain.get().dependsOn(this)
            desktopMain.dependsOn(this)
            jsMain.get().dependsOn(this)
            wasmJsMain.dependsOn(this)
        }

        create("darwinMain") {
            dependsOn(commonMain.get())
            iosMain.get().dependsOn(this)
            macosMain.get().dependsOn(this)
        }

        create("jvmMain") {
            dependsOn(commonMain.get())
            desktopMain.dependsOn(this)
            androidMain.get().dependsOn(this)
        }
    }

    compilerOptions.freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
}
