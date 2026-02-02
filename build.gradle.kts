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

plugins {
    // Necessary trick: for the same plugin versions in all sub-modules
    id(libs.plugins.android.library.get().pluginId) apply false
    id(libs.plugins.android.kotlin.multiplatform.library.get().pluginId) apply false
    id(libs.plugins.kotlin.multiplatform.get().pluginId) apply false
    id(libs.plugins.android.application.get().pluginId) apply false

    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.compose.multiplatform) apply false
}

subprojects {
    plugins.withId("com.vanniktech.maven.publish") {
        configure<com.vanniktech.maven.publish.MavenPublishBaseExtension> {
            publishToMavenCentral()

            // Artifact ID만 각 프로젝트의 이름으로 자동 설정
            group = "zone.ien.hig"
            version = libs.versions.lib.version.name.get()
            println("${group} ${project.name} ${version}")

            coordinates(group.toString(), project.name, version.toString())

            pom {
                name = project.name
                description = "IENGROUND Kotlin & Compose Multiplatform Helper."
                inceptionYear = "2026"
                url = "https://github.com/ienground/ienlab-cmp-library"
                licenses {
                    license {
                        name = "Apache-2.0"
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                    }
                }
                developers {
                    developer {
                        id = "ienground"
                        name = "Ericano Rhee"
                        url = "my@ien.zone"
                    }
                }
                scm {
                    url = "https://github.com/ienground/ienlab-cmp-library.git"
                    connection = "scm:git:https://github.com/ienground/ienlab-cmp-library.git"
                    developerConnection = "scm:git:https://github.com/ienground/ienlab-cmp-library.git"
                }
            }
        }
    }
}