package com.example.countdown

import android.content.Context

object Prefs {
    private const val PREFS_NAME = "countdown_prefs"
    private const val KEY_TITLE = "event_title"
    private const val KEY_TARGET_MILLIS = "target_millis"
    private const val KEY_HOUR = "notif_hour"
    private const val KEY_MINUTE = "notif_minute"

    fun save(context: Context, title: String, targetMillis: Long, hour: Int, minute: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_TITLE, title)
            .putLong(KEY_TARGET_MILLIS, targetMillis)
            .putInt(KEY_HOUR, hour)
            .putInt(KEY_MINUTE, minute)
            .apply()
    }

    fun getTitle(context: Context): String =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_TITLE, "Etkinlik") ?: "Etkinlik"

    fun getTargetMillis(context: Context): Long =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getLong(KEY_TARGET_MILLIS, 0L)

    fun getHour(context: Context): Int =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getInt(KEY_HOUR, 9)

    fun getMinute(context: Context): Int =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getInt(KEY_MINUTE, 0)

    fun isConfigured(context: Context): Boolean = getTargetMillis(context) > 0L
}
