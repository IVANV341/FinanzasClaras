// Este archivo define la clase de datos `MovimientoRecord`, que representa un registro
// individual de una transacción financiera. Contiene propiedades para el tipo de movimiento
// (ej. "gasto", "saldo"), una descripción, el monto involucrado y la fecha en que ocurrió.
// Es utilizada para estructurar los datos leídos o escritos en el archivo de movimientos.




package co.edu.unab.overa32.finanzasclaras

// Clase de datos para representar un registro de movimiento individual
data class MovimientoRecord(
    val tipo: String,       // Ej: "gasto", "saldo"
    val descripcion: String,// Descripción del movimiento (ej. "comida", "sueldo")
    val monto: Double,      // Cantidad de dinero
    val fecha: String       // Fecha del movimiento (ej. "dd/MM/yyyy")
)