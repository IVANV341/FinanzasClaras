package co.edu.unab.overa32.finanzasclaras

// Clase de datos para representar un registro de movimiento individual
data class MovimientoRecord(
    val tipo: String,       // Ej: "gasto", "saldo"
    val descripcion: String,// Descripción del movimiento (ej. "comida", "sueldo")
    val monto: Double,      // Cantidad de dinero
    val fecha: String       // Fecha del movimiento (ej. "dd/MM/yyyy")
)