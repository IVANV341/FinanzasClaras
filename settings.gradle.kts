// settings.gradle.kts en la raíz de tu proyecto

pluginManagement {
    repositories {
        // Configuración estándar para encontrar plugins de Gradle y Android/Google
        google() // Repositorio de Google (necesario para muchos plugins y dependencias de Android/Google)
        mavenCentral() // Repositorio central de Maven (muchas bibliotecas open source)
        gradlePluginPortal() // Repositorio oficial de plugins de Gradle
    }
}

dependencyResolutionManagement {
    // Esta sección configura los repositorios para las dependencias de los módulos
    // Generalmente es buena práctica tener los mismos repositorios aquí que en pluginManagement
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) // Fallar si un módulo usa un repositorio no declarado aquí
    repositories {
        google() // Repositorio de Google
        mavenCentral() // Repositorio central de Maven
        // Si usas otras fuentes de dependencias (ej. JitPack), las añadirías aquí
    }
}

// Define el nombre de tu proyecto
rootProject.name = "Finanzas Claras" // Asegurate de que este sea el nombre correcto de tu proyecto

// Incluye los módulos de tu proyecto (normalmente solo :app al inicio)
include(":app")

// Si tienes otros módulos (ej. :feature:ia), los añadirías aquí:
// include(":feature:ia")