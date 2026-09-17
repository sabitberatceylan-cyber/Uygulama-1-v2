package com.example.countdown

import android.Manifest
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var statusText: TextView

    private var selectedCalendar: Calendar = Calendar.getInstance()
    private var selectedHour = 9
    private var selectedMinute = 0

    private val notifPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        titleInput = findViewById(R.id.input_title)
        dateButton = findViewById(R.id.button_date)
        timeButton = findViewById(R.id.button_time)
        statusText = findViewById(R.id.text_status)
        val saveButton: Button = findViewById(R.id.button_save)

        if (Prefs.isConfigured(this)) {
            titleInput.setText(Prefs.getTitle(this))
            selectedCalendar.timeInMillis = Prefs.getTargetMillis(this)
            selectedHour = Prefs.getHour(this)
            selectedMinute = Prefs.getMinute(this)
        } else {
            selectedCalendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        updateDateButtonText()
        updateTimeButtonText()

        dateButton.setOnClickListener { showDatePicker() }
        timeButton.setOnClickListener { showTimePicker() }
        saveButton.setOnClickListener { saveAndSchedule() }

        requestNotificationPermissionIfNeeded()
        checkExactAlarmPermission()
    }

    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, year, month, day ->
                selectedCalendar.set(year, month, day)
                updateDateButtonText()
            },
            selectedCalendar.get(Calendar.YEAR),
            selectedCalendar.get(Calendar.MONTH),
            selectedCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        TimePickerDialog(
            this,
            { _, hour, minute ->
                selectedHour = hour
                selectedMinute = minute
                updateTimeButtonText()
            },
            selectedHour, selectedMinute, true
        ).show()
    }

    private fun updateDateButtonText() {
        val fmt = SimpleDateFormat("dd.MM.yyyy", Locale("tr"))
        dateButton.text = "Tarih: ${fmt.format(selectedCalendar.time)}"
    }

    private fun updateTimeButtonText() {
        timeButton.text = "Bildirim saati: %02d:%02d".format(selectedHour, selectedMinute)
    }

    private fun saveAndSchedule() {
        val title = titleInput.text.toString().ifBlank { "Etkinlik" }
        selectedCalendar.set(Calendar.HOUR_OF_DAY, 0)
        selectedCalendar.set(Calendar.MINUTE, 0)
        selectedCalendar.set(Calendar.SECOND, 0)
        selectedCalendar.set(Calendar.MILLISECOND, 0)

        Prefs.save(this, title, selectedCalendar.timeInMillis, selectedHour, selectedMinute)

        AlarmScheduler.scheduleDailyNotification(this, selectedHour, selectedMinute)
        AlarmScheduler.scheduleMidnightWidgetRefresh(this)

        val appWidgetManager = AppWidgetManager.getInstance(this)
        val ids = appWidgetManager.getAppWidgetIds(ComponentName(this, CountdownWidgetProvider::class.java))
        CountdownWidgetProvider.updateAllWidgets(this, appWidgetManager, ids)

        statusText.text = "Kaydedildi. Widget'ı ana ekrana eklemeyi unutmayın."
        Toast.makeText(this, "Kaydedildi", Toast.LENGTH_SHORT).show()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(
                    this,
                    "Bildirimlerin tam zamanında gelmesi için 'Alarmlar ve hatırlatıcılar' iznini açın",
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            }
        }
    }
}
