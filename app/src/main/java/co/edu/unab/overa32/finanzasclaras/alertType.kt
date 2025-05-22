// Este archivo define la enumeración `AlertType`, la cual especifica los diferentes
// tipos de umbrales de alerta que pueden configurarse en la aplicación:
// `HIGH_BALANCE` para alertas de saldo alto y `LOW_BALANCE` para alertas de saldo bajo.



package co.edu.unab.overa32.finanzasclaras

// Definimos los tipos de alerta que puede haber
enum class AlertType {
    HIGH_BALANCE, // Alerta cuando el saldo es MAYOR O IGUAL al umbral
    LOW_BALANCE   // Alerta cuando el saldo es MENOR O IGUAL al umbral
}