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

import org.jetbrains.compose.compose

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("multiplatform-module-convention")
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    id(libs.plugins.vanniktech.mavenPublish.get().pluginId)
}

kotlin {

    sourceSets {
        jsTest {
            resources.srcDir(rootProject.file("hig/src/jsTest/resources"))
        }
        commonMain.dependencies {
            api(projects.hig)
            api(projects.higNative)
            api(libs.compose.material3)
            api(libs.compose.adaptive)
            api(libs.compose.adaptive.navigation3)
            implementation(projects.higCore)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(compose.materialIconsExtended)
            implementation(libs.compose.ui.util)
            implementation(libs.backdrop)
            implementation(libs.capsule)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation("org.jetbrains.compose.ui:ui-test:${libs.plugins.compose.multiplatform.get().version}")
        }
        desktopTest.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}
