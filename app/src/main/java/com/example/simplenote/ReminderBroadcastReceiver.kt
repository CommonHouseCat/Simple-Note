package com.example.simplenote

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class ReminderBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "reminder"
    }
    override fun onReceive(context: Context, intent: Intent) {

        val intents = Intent(context,  ProgressTrackerActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intents, PendingIntent.FLAG_IMMUTABLE)



        if (intent.action == "SHOW_NOTIFICATION") {
            val reminderName = intent.getStringExtra("reminder_name")

            // Generate a unique notification ID based on the reminder name
            val notificationId = reminderName.hashCode()

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Reminder")
                .setContentText(reminderName)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .build()
            notificationManager.notify(notificationId, builder)
        }
    }
}