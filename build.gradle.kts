// build.gradle.kts (ARCHIVO EN LA RAÍZ DEL PROYECTO)

// Bloque 'plugins': Aquí se DECLARAN las versiones de los plugins de todo el proyecto.
// Es CRÍTICO que este bloque vaya al PRINCIPIO del archivo.
plugins {
    // Android Gradle Plugin (AGP) - Versión del plugin de Android
    id("com.android.application") version "8.8.0" apply false // Usas la versión 8.8.0

    // Kotlin Android Plugin - Versión del compilador de Kotlin para Android
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false // Versión de Kotlin estable

    // Google Services Plugin - Para Firebase (procesa google-services.json)
    id("com.google.gms.google-services") version "4.4.1" apply false // Última versión estable de gms-google-services

    // Kotlin Serialization Plugin - Para usar kotlinx.serialization
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23" apply false // Misma versión que Kotlin
}

// Nota: Con las versiones modernas de Gradle y Kotlin DSL, los bloques 'buildscript'
// y 'allprojects' en este archivo (root build.gradle.kts) son generalmente
// reemplazados por 'pluginManagement' y 'dependencyResolutionManagement'
// en el archivo settings.gradle.kts. Si los tienes en settings.gradle.kts,
// no deben ir aquí para evitar conflictos y redundancia.


// Tarea opcional para limpiar el directorio de build de todo el proyecto.
// Esta tarea está bien aquí.
tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}