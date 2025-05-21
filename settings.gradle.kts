// settings.gradle.kts (en la raíz de tu proyecto)

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        // DECLARA AQUI LOS PLUGINS QUE TUS MÓDULOS USARÁN
        // NO USES apply(true) O apply = true AQUI
        // Las versiones aquí DEBEN coincidir con las que usas en el build.gradle.kts de la raíz.
        id("com.android.application") version "8.8.0" apply false
        id("org.jetbrains.kotlin.android") version "1.9.23" apply false

        // DECLARACIÓN DEL PLUGIN DE GOOGLE SERVICES (para Firebase)
        id("com.google.gms.google-services") version "4.4.1" apply false

        // DECLARACIÓN DEL PLUGIN DE KOTLIN SERIALIZATION
        id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23" apply false

        // --- ¡AÑADE ESTA LÍNEA PARA EL PLUGIN KSP! ---
        id("com.google.devrel.ksp") version "1.9.23-1.0.20" apply false // Asegúrate de que la versión coincida con tu Kotlin

        // DECLARACIÓN DEL PLUGIN DE SECRETOS DE GOOGLE AI (si lo vas a usar)
        // id("com.google.cloud.android.secrets-gradle-plugin") version "2.0.1" apply false
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Finanzas Claras"

include(":app")