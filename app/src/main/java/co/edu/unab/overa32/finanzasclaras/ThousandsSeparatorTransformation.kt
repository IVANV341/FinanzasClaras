package co.edu.unab.overa32.finanzasclaras

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class ThousandsSeparatorTransformation(private val locale: Locale) : VisualTransformation {

    // Obtenemos los símbolos de formato para la localización específica
    private val symbols = DecimalFormatSymbols(locale)

    // Creamos un DecimalFormat para el formato de números
    private val decimalFormat = DecimalFormat("#,##0.00", symbols).apply {
        isGroupingUsed = true // Activar el uso de separadores de grupo (miles)
        groupingSize = 3 // Grupo de 3 dígitos para miles
        maximumFractionDigits = 2 // Limitar a dos decimales
        minimumFractionDigits = 0 // No mostrar decimales si no los hay
    }

    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text

        // 1. Limpiar el texto original: quitar separadores de grupo y reemplazar separador decimal por punto
        val cleanString = originalText
            .replace(symbols.groupingSeparator.toString(), "")
            .replace(symbols.decimalSeparator.toString(), ".")
            .replace(" ", "") // Eliminar espacios en blanco

        // 2. Intentar parsear el número
        val number = cleanString.toDoubleOrNull()

        // 3. Formatear el texto para la visualización
        val formattedText = if (originalText.isEmpty()) {
            ""
        } else if (originalText.endsWith(symbols.decimalSeparator)) {
            // Si el usuario acaba de escribir el separador decimal, lo mantenemos y formateamos la parte entera
            decimalFormat.format(number ?: 0.0).split(symbols.decimalSeparator)[0] + symbols.decimalSeparator
        } else if (originalText.contains(symbols.decimalSeparator)) {
            // Si el usuario está escribiendo decimales, formateamos la parte entera y mantenemos los decimales
            val parts = originalText.split(symbols.decimalSeparator)
            val integerPart = parts[0].replace(symbols.groupingSeparator.toString(), "")
            val decimalPart = if (parts.size > 1) parts[1] else ""

            val formattedInteger = if (integerPart.isEmpty()) "" else decimalFormat.format(integerPart.toLongOrNull() ?: 0L)
            formattedInteger + symbols.decimalSeparator + decimalPart
        } else {
            // Para números enteros o sin decimales aún
            decimalFormat.format(number?.toLong() ?: 0L)
        }

        // 4. Mapeo del offset del cursor (la parte más delicada)
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var transformedOffset = offset
                var originalIndex = 0
                var transformedIndex = 0

                // Contar cuántos separadores se insertaron antes del offset original
                while (originalIndex < offset && transformedIndex < formattedText.length) {
                    val char = formattedText[transformedIndex]
                    if (char == symbols.groupingSeparator || char == symbols.decimalSeparator) {
                        // Si encontramos un separador, el offset transformado avanza
                        transformedOffset++
                    } else {
                        // Si es un dígito, el offset original avanza
                        originalIndex++
                    }
                    transformedIndex++
                }
                return transformedOffset
            }

            override fun transformedToOriginal(offset: Int): Int {
                var originalOffset = offset
                var transformedIndex = 0
                var originalIndex = 0

                // Contar cuántos separadores se eliminaron antes del offset transformado
                while (transformedIndex < offset && originalIndex < originalText.length) {
                    val char = formattedText[transformedIndex]
                    if (char == symbols.groupingSeparator || char == symbols.decimalSeparator) {
                        // Si encontramos un separador, el offset original retrocede
                        originalOffset--
                    } else {
                        // Si es un dígito, el offset transformado avanza
                        originalIndex++
                    }
                    transformedIndex++
                }
                return originalOffset
            }
        }

        return TransformedText(AnnotatedString(formattedText), offsetMapping)
    }
}