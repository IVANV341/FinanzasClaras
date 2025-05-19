// build.gradle.kts en la RAIZ del proyecto (C:\Users\ivanv\Downloads\finanzas-claras\build.gradle.kts)

// Este bloque configura los repositorios y dependencias que necesita el SCRIPT DE CONSTRUCCION mismo para ejecutarse.
// Aquí se define dónde encontrar los archivos de los plugins de Gradle.
// build.gradle.kts en la RAIZ del proyecto

buildscript {
    repositories {
        // Repositorios donde Gradle buscará los archivos de los plugins (.jar).
        google() // Repositorio de Google
        mavenCentral() // Repositorio central de Maven
        gradlePluginPortal() // <-- ¡Añade esta linea aqui!
    }
    dependencies {
        // Dependencias que necesita el build script para funcionar.
        classpath("com.android.tools.build:gradle:8.2.0") // VERIFICA LA VERSIÓN
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0") // VERIFICA LA VERSIÓN
        classpath("com.google.android.libraries.mapssecrets:secrets-gradle-plugin:2.0.1")
}

// ... el resto del archivo ...

// Este bloque es donde declaras los PLUGINS que se aplicarán al PROYECTO DE LA RAIZ (si aplica)
// o declaras plugins que los subproyectos pueden aplicar sin especificar version.
// No necesitas aplicar los plugins de modulo aqui, solo declararlos si pluginManagement no lo hace.
plugins {
    // Plugins de alto nivel si aplican al proyecto raiz mismo.
    // A menudo, los plugins de modulo (android.application, kotlin.android) no se declaran con version aqui.
    // Si necesitas aplicar el secrets plugin al modulo raiz (raro), lo harias con ID y version.
    // Pero la forma mas común es declararlo en buildscript (arriba) y aplicarlo en los modulos (sin version).
}


// Este bloque configura los repositorios por defecto para las DEPENDENCIAS de TODOS los SUBPROYECTOS (módulos como :app).
// Gradle buscará las bibliotecas (como Compose, Navigation, la API de Gemini, etc.) en estos repositorios.
allprojects {
    repositories {
        google() // Repositorio de Google (para AndroidX, Material Design, etc.)
        mavenCentral() // Repositorio central de Maven (muchas bibliotecas)
        // Si tus módulos necesitan otras fuentes de dependencias (ej. JitPack), añádelas aquí.
    }
}}

// Opcional: Tarea para limpiar todos los directorios de construcción de todos los módulos.
// task clean(type: Delete) {
//     delete rootProject.buildDir
// }

// Nota: Este archivo NO debe contener secciones como 'android { ... }', 'defaultConfig { ... }',
// o bloques 'dependencies { ... }' con 'implementation(...)', que son exclusivas de los archivos
// build.gradle.kts de los módulos (como el de la carpeta 'app').