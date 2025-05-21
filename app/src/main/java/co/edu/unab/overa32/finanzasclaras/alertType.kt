package co.edu.unab.overa32.finanzasclaras

// Definimos los tipos de alerta que puede haber
enum class AlertType {
    HIGH_BALANCE, // Alerta cuando el saldo es MAYOR O IGUAL al umbral
    LOW_BALANCE   // Alerta cuando el saldo es MENOR O IGUAL al umbral
}