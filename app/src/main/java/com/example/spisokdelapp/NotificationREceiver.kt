package com.example.spisokdelapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Получаем системный сервис для управления уведомлениями
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений (требуется для Android 8 и выше)
        val channelId = "default_channel_id"
        val channelName = "Default Channel"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance)
        notificationManager.createNotificationChannel(channel)

        // Создаем намерение для запуска приложения при нажатии на уведомление
        val notificationIntent = Intent(context, SobytieActivity::class.java) // Замените YourActivity на свой класс Activity
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Создаем уведомление
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Напоминание")
            .setContentText("Текст напоминания")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Показываем уведомление
        notificationManager.notify(1, notification)
    }
}
