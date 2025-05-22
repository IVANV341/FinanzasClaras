// Este archivo define el Composable `LoginBackgroundWaves`, que crea un efecto visual de ondas
// dinámicas para usar como fondo en pantallas de inicio de sesión o similares.
// Utiliza `Canvas` para dibujar dos conjuntos de ondas (superiores e inferiores) con colores fijos
// y opacidades para un efecto sutil y animado, sin depender directamente del tema de Material Design.



package co.edu.unab.overa32.finanzasclaras

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // Asegúrate de que este import esté
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
// ELIMINAR: import androidx.compose.material3.MaterialTheme // Ya no se necesita si no usamos colorScheme directamente
import androidx.compose.ui.geometry.Offset
import kotlin.math.sin

@Composable
fun LoginBackgroundWaves() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // --- ¡SOLUCIÓN FINAL! COLORES FIJOS PARA BYPASSEAR EL ERROR ---
        // Estos colores ya no dependen de MaterialTheme.colorScheme, sino que son valores directos.
        // Puedes ajustarlos a tu gusto (ej. un púrpura suave, un gris, un azul claro).
        val waveColor = Color(0xFFADD8E6).copy(alpha = 0.3f) // Un azul claro con opacidad
        val lineColor = Color.Gray.copy(alpha = 0.1f)     // Un gris con opacidad

        // Si prefieres usar colores que estén en tu Theme.kt como constantes globales (ej. MyColors.Purple500)
        // podrías hacerlo si los defines como 'val' fuera de MaterialTheme.colorScheme.
        // Por ahora, estos valores fijos garantizan la compilación.

        // Ondas superiores
        val wavePathTop = Path().apply {
            moveTo(0f, 0f)
            for (i in 0..width.toInt() step 80) {
                val x = i.toFloat()
                val y = (sin(x * 0.02) * 15).toFloat() + height * 0.1f
                lineTo(x, y)
            }
            lineTo(width, 0f)
            close()
        }
        drawPath(path = wavePathTop, color = waveColor)
        drawPath(path = wavePathTop, color = lineColor, style = Stroke(width = 1f))


        // Ondas inferiores
        val wavePathBottom = Path().apply {
            moveTo(0f, height)
            for (i in 0..width.toInt() step 80) {
                val x = i.toFloat()
                val y = height - ((sin(x * 0.02 + 0.5) * 20).toFloat() + height * 0.15f)
                lineTo(x, y)
            }
            lineTo(width, height)
            close()
        }
        drawPath(path = wavePathBottom, color = waveColor)
        drawPath(path = wavePathBottom, color = lineColor, style = Stroke(width = 1f))
    }
}