package com.example.countdown

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class CountdownWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateAllWidgets(context, appWidgetManager, appWidgetIds)
    }

    companion object {
        fun updateAllWidgets(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
            val title = Prefs.getTitle(context)
            val target = Prefs.getTargetMillis(context)
            val daysLeft = CountdownUtils.daysRemaining(target)
            val configured = Prefs.isConfigured(context)

            val dayText = when {
                !configured -> "–"
                daysLeft > 0 -> "$daysLeft"
                daysLeft == 0 -> "Bugün!"
                else -> "Geçti"
            }
            val labelText = when {
                !configured -> "Uygulamayı açıp tarih seçin"
                daysLeft > 0 -> "gün kaldı"
                daysLeft == 0 -> ""
                else -> "${-daysLeft} gün önce"
            }

            for (appWidgetId in appWidgetIds) {
                val views = RemoteViews(context.packageName, R.layout.widget_countdown)
                views.setTextViewText(R.id.widget_title, title)
                views.setTextViewText(R.id.widget_days, dayText)
                views.setTextViewText(R.id.widget_label, labelText)

                val pendingIntent = android.app.PendingIntent.getActivity(
                    context, 0,
                    Intent(context, MainActivity::class.java),
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}
