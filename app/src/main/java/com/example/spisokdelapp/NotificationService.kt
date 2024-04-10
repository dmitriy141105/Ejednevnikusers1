package com.example.spisokdelapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class NotificationService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sendNotification()
        return START_NOT_STICKY
    }
    companion object {
        private const val CHANNEL_ID = "my_channel_id"
    }
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        // Check if the Android version is Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Channel Name", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification() {
        val PREF_LAST_NOTIFICATION_TIME = "last_notification_time" // Объявляем переменную здесь

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)

        val title = "Список дел"
        val message = "Не забудьте проверить свои дела!"
        val notificationId = Random.nextInt()

        // Проверяем время последнего уведомления
        val lastNotificationTime = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            .getLong(PREF_LAST_NOTIFICATION_TIME, 0)
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastNotificationTime >= TimeUnit.DAYS.toMillis(1)) {
            val contentIntent = Intent(applicationContext, MainActivity::class.java)
            val contentPendingIntent = PendingIntent.getActivity(
                applicationContext,
                notificationId,
                contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(contentPendingIntent)
                .setAutoCancel(true)

            notificationManager.notify(notificationId, builder.build())

            // Сохраняем время текущего уведомления
            getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                .edit()
                .putLong(PREF_LAST_NOTIFICATION_TIME, currentTime)
                .apply()
        }
    }



    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
