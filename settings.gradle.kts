// settings.gradle.kts (en la raíz de tu proyecto)

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    // ¡ESTE BLOQUE 'plugins' DEBE ESTAR DIRECTAMENTE AQUÍ DENTRO DE 'pluginManagement'!
    plugins {
        // DECLARA AQUI LOS PLUGINS QUE TUS MÓDULOS USARÁN
        // NO USES apply(true) O apply = true AQUI
        // Las versiones aquí DEBEN coincidir con las que usas en el build.gradle.kts de la raíz.
        id("com.android.application") version "8.8.0" apply false // Usa la versión que tienes en el root build.gradle.kts
        id("org.jetbrains.kotlin.android") version "1.9.23" apply false // Usa la versión que tienes en el root build.gradle.kts (1.9.23 estable)

        // DECLARACIÓN DEL PLUGIN DE GOOGLE SERVICES (para Firebase)
        id("com.google.gms.google-services") version "4.4.1" apply false // Asegúrate de que coincida con el root build.gradle.kts

        // DECLARACIÓN DEL PLUGIN DE KOTLIN SERIALIZATION
        id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23" apply false // Asegúrate de que coincida con el root build.gradle.kts

        // DECLARACIÓN DEL PLUGIN DE SECRETOS DE GOOGLE AI
        id("com.google.cloud.android.secrets-gradle-plugin") version "2.0.1" apply false // *** AÑADE ESTA LÍNEA *** - Usa la versión más reciente y apply false
    }
}

// Bloque 'dependencyResolutionManagement': Define cómo Gradle resolverá las dependencias de tus módulos.
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) // O RepositoriesMode.PREFER_PROJECT
    repositories {
        // Estos son los repositorios donde tus dependencias (bibliotecas como Compose, Firebase, Gemini API, etc.) serán buscadas.
        google()
        mavenCentral()
    }
}

rootProject.name = "Finanzas Claras" // Asegúrate de que este sea el nombre correcto de tu proyecto.

include(":app") // Declara tus módulos aquí.

// ... (No debería haber más código relevante aquí para la mayoría de los proyectos)