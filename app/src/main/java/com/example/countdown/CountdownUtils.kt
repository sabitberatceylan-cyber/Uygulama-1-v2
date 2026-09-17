package com.example.countdown

import java.util.Calendar
import java.util.concurrent.TimeUnit

object CountdownUtils {
    fun daysRemaining(targetMillis: Long): Int {
        if (targetMillis == 0L) return 0
        val now = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val target = Calendar.getInstance().apply {
            timeInMillis = targetMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val diff = target.timeInMillis - now.timeInMillis
        return TimeUnit.MILLISECONDS.toDays(diff).toInt()
    }
}
