package com.example.simplenote

import java.util.Calendar

data class NoteDC(val id: Int, val title: String, val content: String)

data class ChecklistDC(val checklistID: Int, val checklistTitle: String)

data class ChecklistItemDC(val itemID: Int, val checklistIDfk: Int, val itemContent: String, var isChecked: Boolean)

data class ReminderDC(val reminderID: Int, val time: Calendar, val date: Calendar, val reminderName: String, var isActivated: Boolean )

data class ProgressTrackerDC(val red: Int, val yellow: Int, val green: Int)
