package com.example.countdown

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            if (Prefs.isConfigured(context)) {
                AlarmScheduler.scheduleDailyNotification(context, Prefs.getHour(context), Prefs.getMinute(context))
                AlarmScheduler.scheduleMidnightWidgetRefresh(context)
            }
        }
    }
}
