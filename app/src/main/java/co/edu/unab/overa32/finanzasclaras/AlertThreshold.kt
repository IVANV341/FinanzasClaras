// Este archivo define la clase de datos `AlertThreshold`, que representa la estructura
// de un umbral de alerta en la aplicación. Incluye un ID único, el monto del umbral,
// si la alerta está activa o no, y el tipo de alerta (saldo alto o saldo bajo).


package co.edu.unab.overa32.finanzasclaras

// import kotlinx.serialization.Serializable // Mantener esta línea si la necesitas para kotlinx.serialization en otros contextos (ej. si usas JSON para guardar esto en DataStore en algún otro lado)

data class AlertThreshold(
    val id: Long, // Identificador único del umbral (0L para nuevos, Room lo generará)
    val amount: Double, // Monto del umbral
    val isEnabled: Boolean, // Si la alerta está activada o desactivada
    val type: AlertType // ¡MUY IMPORTANTE! El tipo de alerta (HIGH_BALANCE o LOW_BALANCE)
)

