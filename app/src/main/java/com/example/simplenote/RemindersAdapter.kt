package com.example.simplenote

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class RemindersAdapter(private var reminders: List<ReminderDC>, private val context: Context) : RecyclerView.Adapter<RemindersAdapter.ReminderViewHolder>() {

    private val db: DatabaseHelper = DatabaseHelper(context)

    class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val nameTextView: TextView = itemView.findViewById(R.id.reminderTitleTextView)
        val reminderUpdateButton: ImageView = itemView.findViewById(R.id.updateReminderButton)
        val reminderDeleteButton: ImageView = itemView.findViewById(R.id.deleteReminderButton)
        val reminderSwitch: SwitchCompat = itemView.findViewById(R.id.reminderSwitch)
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
        holder.reminderSwitch.isChecked = reminder.isActivated

        holder.reminderUpdateButton.setOnClickListener{
            // Get old data
            val oldTime = reminder.time
            val oldDate = reminder.date
            val oldName = reminder.reminderName

            // Inflate the reminder dialog
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_reminder, null)

            // Create the dialog
            val builder = AlertDialog.Builder(context)
                .setView(dialogView)

            // Get the text location
            val editTextTime = dialogView.findViewById<EditText>(R.id.editReminderTimeEditText)
            val editTextDate = dialogView.findViewById<EditText>(R.id.editReminderDateEditText)
            val editTextName = dialogView.findViewById<EditText>(R.id.dialogEditReminderNameEditText)

            // Parse Calender objects (time and date) to string
            val timeF = SimpleDateFormat("HH:mm", Locale.getDefault())
            val dateF = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val oldTimeString = timeF.format(oldTime.time.time)
            val oldDateString = dateF.format(oldDate.time.time)

            // Set all edit text with their old data
            editTextTime.setText(oldTimeString)
            editTextDate.setText(oldDateString)
            editTextName.setText(oldName)

            val dialog = builder.create()

            // Set click listeners for confirm buttons
            dialogView.findViewById<Button>(R.id.dialogEditConfirmReminder).setOnClickListener {
                val newTime = editTextTime.text.toString()
                val newDate = editTextDate.text.toString()
                val newName = editTextName.text.toString()

                db.updateReminder(reminder.reminderID, newTime, newDate, newName)
                refreshData(db.getAllReminder())
                dialog.dismiss()
            }

            // Set click listeners for cancel buttons
            dialogView.findViewById<Button>(R.id.dialogEditCancelReminder).setOnClickListener {
                dialog.dismiss()
            }

            dialogView.findViewById<ImageView>(R.id.editSelectTimeButton).setOnClickListener{
                showTimePickerDialog(editTextTime)
            }

            dialogView.findViewById<ImageView>(R.id.editSelectDateButton).setOnClickListener{
                showDatePickerDialog(editTextDate)
            }
            dialog.show()
        }

        holder.reminderDeleteButton.setOnClickListener{
            db.deleteReminder(reminder.reminderID)
            refreshData(db.getAllReminder())
            Toast.makeText(holder.itemView.context, "Reminder Deleted", Toast.LENGTH_SHORT).show()
        }


        if(reminder.isActivated){
            holder.nameTextView.setTextColor(ContextCompat.getColor(context, R.color.orange))
            holder.timeTextView.setTextColor(ContextCompat.getColor(context, R.color.bone))
            holder.dateTextView.setTextColor(ContextCompat.getColor(context, R.color.bone))
        }else{
            holder.nameTextView.setTextColor(Color.GRAY)
            holder.timeTextView.setTextColor(Color.GRAY)
            holder.dateTextView.setTextColor(Color.GRAY)
        }

//        holder.reminderSwitch.isChecked = reminder.isActivated

        holder.reminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            reminder.isActivated = isChecked

            if (reminder.isActivated) {
                holder.nameTextView.setTextColor(ContextCompat.getColor(context, R.color.orange))
                holder.timeTextView.setTextColor(ContextCompat.getColor(context, R.color.bone))
                holder.dateTextView.setTextColor(ContextCompat.getColor(context, R.color.bone))
            } else {
                holder.nameTextView.setTextColor(Color.GRAY)
                holder.timeTextView.setTextColor(Color.GRAY)
                holder.dateTextView.setTextColor(Color.GRAY)
            }

            db.updateReminderState(reminder.reminderID, isChecked)
            refreshData(db.getAllReminder())
        }

    }


    fun refreshData(newReminders: List<ReminderDC>){
        reminders = newReminders
        notifyDataSetChanged()
    }

    private fun showTimePickerDialog(timeEditText: EditText) {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            context,
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
            context,
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
}