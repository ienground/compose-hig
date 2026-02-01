import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id(libs.plugins.android.application.get().pluginId)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.compose.cupertino.example"
    compileSdk = (findProperty("android.compileSdk") as String).toInt()

    defaultConfig {
        applicationId = "com.compose.cupertino.example"
        minSdk = (findProperty("android.minSdk") as String).toInt()
        versionCode = 1
        versionName = libs.versions.lib.version.name.get()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    target {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    dependencies {
        implementation(project(":example:composeApp"))
        implementation(libs.activity.compose)
        debugImplementation(libs.compose.ui.tooling)
    }
}