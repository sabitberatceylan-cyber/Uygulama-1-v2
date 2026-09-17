package com.example.countdown

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = Prefs.getTitle(context)
        val target = Prefs.getTargetMillis(context)
        val daysLeft = CountdownUtils.daysRemaining(target)

        val channelId = "countdown_channel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Geri Sayım Bildirimleri", NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val message = when {
            daysLeft > 0 -> "$title için $daysLeft gün kaldı"
            daysLeft == 0 -> "$title bugün!"
            else -> "$title geçti (${-daysLeft} gün önce)"
        }

        val openIntent = Intent(context, MainActivity::class.java)
        val contentPendingIntent = android.app.PendingIntent.getActivity(
            context, 0, openIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_my_calendar)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(2001, builder.build())

        // Reschedule for the same time tomorrow
        AlarmScheduler.scheduleDailyNotification(context, Prefs.getHour(context), Prefs.getMinute(context))
    }
}
