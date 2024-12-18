package com.example.simplenote


import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplenote.databinding.ActivityReminderBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale



class ReminderActivity : AppCompatActivity() {
    companion object {
        const val CHANNEL_ID = "reminder"
    }

    private lateinit var binding: ActivityReminderBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var db : DatabaseHelper
    private lateinit var reminderAdapter: RemindersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Create the notification channel
        createNotificationChannel()

        binding.toolbar.setOnMenuItemClickListener {menuItem ->
            when(menuItem.itemId){
                R.id.add_note -> {
                    showDialogCreateReminderPopup()
                    true
                }
                else -> false
            }
        }

        db = DatabaseHelper(this)

        reminderAdapter = RemindersAdapter(db.getAllReminder(), this)
        binding.reminderRecycleView.layoutManager = LinearLayoutManager(this)
        binding.reminderRecycleView.adapter = reminderAdapter


        drawerLayout = binding.drawerLayout
        val toggle = ActionBarDrawerToggle(this, drawerLayout
            , binding.toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.toolbar.setNavigationOnClickListener{
            if(drawerLayout.isDrawerOpen(binding.navigationView)){
                drawerLayout.closeDrawer(binding.navigationView)
            }else{
                drawerLayout.openDrawer(binding.navigationView)
            }
        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId){
                R.id.nav_note -> {
                    if (!isCurrentActivity(MainActivity::class.java)) {
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_checklist -> {
                    if (!isCurrentActivity(ChecklistActivity::class.java)) {
                        startActivity(Intent(this, ChecklistActivity::class.java))
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_reminder -> {
                    if (!isCurrentActivity(ReminderActivity::class.java)) {
                        startActivity(Intent(this, ReminderActivity::class.java))
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_progress_tracker -> {
                    if (!isCurrentActivity(ProgressTrackerActivity::class.java)) {
                        startActivity(Intent(this, ProgressTrackerActivity::class.java))
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed(){
                if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START)
                }else{
                    finish()
                }
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        reminderAdapter.refreshData(db.getAllReminder())
    }

    private fun showDialogCreateReminderPopup(){
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_reminder, null)
        val timeEditText = dialogView.findViewById<EditText>(R.id.addReminderTimeEditText)
        val dateEditText = dialogView.findViewById<EditText>(R.id.addReminderDateEditText)
        val reminderNameEditText = dialogView.findViewById<EditText>(R.id.dialogReminderNameEditText)

        val dialogBuilder = AlertDialog.Builder(this).setView(dialogView)
        val dialog = dialogBuilder.create()

        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

        timeEditText.setText(currentTime)
        dateEditText.setText(currentDate)

        dialogView.findViewById<Button>(R.id.dialogConfirmReminder).setOnClickListener {
            // Parse time string to Calender
            val time = timeEditText.text.toString()
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val timeCalendar = Calendar.getInstance()
            timeCalendar.time = timeFormat.parse(time) ?: Date()

            // Parse date string to Calendar
            val date = dateEditText.text.toString()
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val dateCalendar = Calendar.getInstance()
            dateCalendar.time = dateFormat.parse(date) ?: Date()

            val name = reminderNameEditText.text.toString()


            val reminder = ReminderDC(0, timeCalendar, dateCalendar, name, true)
            db.insertReminder(reminder)
            Toast.makeText(this, "Reminder Saved", Toast.LENGTH_SHORT).show()
            onResume()
            if(reminder.isActivated){
                scheduleNotification(reminder)
            }
            dialog.dismiss()

        }

        dialogView.findViewById<Button>(R.id.dialogCancelReminder).setOnClickListener{
            dialog.dismiss()
        }

        dialogView.findViewById<ImageView>(R.id.selectTimeButton).setOnClickListener{
            showTimePickerDialog(timeEditText)
        }

        dialogView.findViewById<ImageView>(R.id.selectDateButton    ).setOnClickListener{
            showDatePickerDialog(dateEditText)
        }

        dialog.show()
    }

    private fun showTimePickerDialog(timeEditText: EditText) {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                val time = String.format("%02d:%02d", selectedHour, selectedMinute)
                timeEditText.setText(time)
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }

    private fun showDatePickerDialog(dateEditText: EditText) {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val date = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                dateEditText.setText(date)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }


    // Create Notification Channel in onCreate()
    @SuppressLint("ObsoleteSdkInt")
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Reminder Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for displaying reminders"
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    // Schedule Notifications in add and update
    @SuppressLint("ObsoleteSdkInt")
    private fun scheduleNotification(reminder: ReminderDC) {
        if(!reminder.isActivated) return

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val notificationIntent = Intent(this, ReminderBroadcastReceiver::class.java).apply {
            action = "SHOW_NOTIFICATION"
            putExtra("reminder_id", reminder.reminderID)
            putExtra("reminder_name", reminder.reminderName)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this, reminder.reminderID, notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        // Schedule the alarm at the exact time specified for the reminder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminder.time.timeInMillis, pendingIntent)
            } else alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminder.time.timeInMillis, pendingIntent)
        } else alarmManager.set(AlarmManager.RTC_WAKEUP, reminder.time.timeInMillis, pendingIntent)

        db.updateReminderState(reminder.reminderID, false)
        onResume()
    }

    private fun isCurrentActivity(activityClass: Class<*>): Boolean {
        return activityClass == this::class.java
    }
}