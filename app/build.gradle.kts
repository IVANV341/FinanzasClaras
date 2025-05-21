// app/build.gradle.kts (Module: app)

import java.util.Properties // Importación necesaria para Properties
import java.io.FileInputStream // Importación necesaria para FileInputStream

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.serialization")

    id("kotlin-kapt")
}

android {
    namespace = "co.edu.unab.overa32.finanzasclaras"
    compileSdk = 35

    defaultConfig {
        applicationId = "co.edu.unab.overa32.finanzasclaras"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // --- ¡IMPORTANTE! Acceso más robusto a la API Key de Gemini desde local.properties ---
        // Se carga el archivo local.properties y se lee la clave de forma explícita
        val properties = Properties()
        if (rootProject.file("local.properties").exists()) {
            properties.load(FileInputStream(rootProject.file("local.properties")))
        }
        buildConfigField("String", "GOOGLE_GEMINI_API_KEY", "\"${properties.getProperty("GOOGLE_GEMINI_API_KEY")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true // ¡AÑADE ESTA LÍNEA!

    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.12"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // ViewModel support for Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    // Cliente de Generative AI de Google
    implementation("com.google.ai.client.generativeai:generativeai:0.5.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Core KTX, Lifecycle Runtime KTX, Activity Compose
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.9.0")

    // Compose BOM (Platform)
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.05.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Dependencias de Navigation
    val nav_version = "2.7.0"
    implementation("androidx.navigation:navigation-compose:$nav_version")

    // Librería de serialización JSON (si usas Kotlinx Serialization)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // Firebase (si usas Firebase)
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")

    // --- Dependencias de Room ---
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
}