package com.example.countdown

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent

class WidgetRefreshReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val ids = appWidgetManager.getAppWidgetIds(
            ComponentName(context, CountdownWidgetProvider::class.java)
        )
        if (ids.isNotEmpty()) {
            CountdownWidgetProvider.updateAllWidgets(context, appWidgetManager, ids)
        }
        AlarmScheduler.scheduleMidnightWidgetRefresh(context)
    }
}
