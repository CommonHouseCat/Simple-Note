package com.example.simplenote

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class RemindersAdapter(private var reminders: List<ReminderDC>, context: Context) : RecyclerView.Adapter<RemindersAdapter.ReminderViewHolder>() {

    private val db: DatabaseHelper = DatabaseHelper(context)

    class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val nameTextView: TextView = itemView.findViewById(R.id.reminderTitleTextView)
//        val reminderUpdateButton: ImageView = itemView.findViewById(R.id.updateReminderButton)
        val reminderDeleteButton: ImageView = itemView.findViewById(R.id.deleteReminderButton)
//        val reminderSwitch: SwitchCompat = itemView.findViewById(R.id.reminderSwitch)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.reminder_item, parent, false)
        return ReminderViewHolder(view)
    }

    override fun getItemCount(): Int = reminders.size

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        holder.timeTextView.text = timeFormat.format(reminder.time.time)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        holder.dateTextView.text = dateFormat.format(reminder.date.time)
        holder.nameTextView.text = reminder.reminderName

        holder.reminderDeleteButton.setOnClickListener{
            db.deleteReminder(reminder.reminderID)
            refreshData(db.getAllReminder())
            Toast.makeText(holder.itemView.context, "Reminder Deleted", Toast.LENGTH_SHORT).show()
        }
    }

    fun refreshData(newReminders: List<ReminderDC>){
        reminders = newReminders
        notifyDataSetChanged()
    }



}