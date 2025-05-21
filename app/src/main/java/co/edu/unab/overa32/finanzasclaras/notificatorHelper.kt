package co.edu.unab.overa32.finanzasclaras

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.text.NumberFormat
import java.util.Locale

object NotificationHelper { // <-- La definición del objeto

    // --- ¡ASEGÚRATE DE QUE ESTAS LÍNEAS ESTÁN AQUÍ Y SON CORRECTAS! ---
    private const val CHANNEL_ID = "finanzas_claras_alerts_channel"
    private const val CHANNEL_NAME = "Alertas de Saldo"
    private const val CHANNEL_DESCRIPTION = "Notificaciones para umbrales de saldo alcanzados"
    // --- FIN DEFINICIONES DE CANAL ---


    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, // Usando CHANNEL_ID
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showBalanceAlertNotification(context: Context, alertId: Long, currentBalance: Double, thresholdAmount: Double, alertType: AlertType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                println("Permiso de POST_NOTIFICATIONS no concedido. No se puede mostrar la alerta.")
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("alert_id", alertId)
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            alertId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val formattedBalance = NumberFormat.getCurrencyInstance(Locale("es", "CO")).format(currentBalance)
        val formattedThreshold = NumberFormat.getCurrencyInstance(Locale("es", "CO")).format(thresholdAmount)

        val message = when (alertType) {
            AlertType.HIGH_BALANCE -> "Tu saldo actual de $formattedBalance ha alcanzado o superado tu umbral de $formattedThreshold."
            AlertType.LOW_BALANCE -> "¡ATENCIÓN! Tu saldo actual de $formattedBalance ha caído por debajo de tu umbral de $formattedThreshold."
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID) // Usando CHANNEL_ID
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Alerta de Saldo en Finanzas Claras")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        with(NotificationManagerCompat.from(context)) {
            notify(alertId.toInt(), builder.build())
        }
    }
}