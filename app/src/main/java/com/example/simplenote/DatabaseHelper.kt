@file:Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER")

package com.example.simplenote

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    companion object{
        private const val DATABASE_NAME = "simplenote.db"
        private const val DATABASE_VERSION = 1
        // Table for notes
        private const val TABLE_NAME = "notes"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"
        // Table for checklists
        private const val TABLE_CHECKLIST_NAME = "checklists"
        private const val COLUMN_CHECKLIST_ID = "checklistID"
        private const val COLUMN_CHECKLIST_TITLE = "checklistTitle"
        // Table for checklist items
        private const val TABLE_CHECKLIST_ITEM_NAME = "checklistsItem"
        private const val COLUMN_ITEM_ID = "itemID"
        private const val COLUMN_CHECKLIST_ID_FK = "itemFK"
        private const val COLUMN_ITEM_CONTENT = "itemContent"
        private const val COLUMN_IS_CHECKED = "itemIsChecked"
        // Table for reminders
        private const val TABLE_REMINDER_NAME = "reminder"
        private const val COLUMN_REMINDER_ID = "reminderID"
        private const val COLUMN_TIME = "reminderTime"
        private const val COLUMN_DATE = "reminderDate"
        private const val COLUMN_REMINDER_NAME = "reminderName"
        private const val COLUMN_IS_ACTIVATED = "reminderIsActivated"
        // Table for progress tracker
        private const val TABLE_TRACKER_NAME = "progressTracker"
        private const val COLUMN_LATE_TASK = "lateTasks"
        private const val COLUMN_UNFINISHED_TASK = "unfinishedTasks"
        private const val COLUMN_ON_TIME_TASK = "onTimeTasks"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TITLE TEXT, $COLUMN_CONTENT TEXT)"
        val createChecklistTableQuery = "CREATE TABLE $TABLE_CHECKLIST_NAME ($COLUMN_CHECKLIST_ID INTEGER PRIMARY KEY, $COLUMN_CHECKLIST_TITLE TEXT UNIQUE)"
        val createChecklistItemTableQuery = "CREATE TABLE $TABLE_CHECKLIST_ITEM_NAME ($COLUMN_ITEM_ID INTEGER PRIMARY KEY, $COLUMN_CHECKLIST_ID_FK INTEGER, $COLUMN_ITEM_CONTENT TEXT UNIQUE, $COLUMN_IS_CHECKED INTEGER DEFAULT 0, FOREIGN KEY ($COLUMN_CHECKLIST_ID_FK) REFERENCES $TABLE_CHECKLIST_NAME($COLUMN_CHECKLIST_ID))"
        val createReminderTableQuery = "CREATE TABLE $TABLE_REMINDER_NAME ($COLUMN_REMINDER_ID INTEGER PRIMARY KEY, $COLUMN_TIME TEXT, $COLUMN_DATE TEXT, $COLUMN_REMINDER_NAME TEXT, $COLUMN_IS_ACTIVATED INTEGER DEFAULT 1)"
        val createProgressTrackerQuery = "CREATE TABLE $TABLE_TRACKER_NAME ($COLUMN_LATE_TASK INTEGER DEFAULT 0, $COLUMN_UNFINISHED_TASK INTEGER DEFAULT 0, $COLUMN_ON_TIME_TASK INTEGER DEFAULT 0)"


        db?.execSQL(createTableQuery)
        db?.execSQL(createChecklistTableQuery)
        db?.execSQL(createChecklistItemTableQuery)
        db?.execSQL(createReminderTableQuery)
        db?.execSQL(createProgressTrackerQuery)

        // Insert initial values for the tracker table
        val initialValues = ContentValues().apply {
            put(COLUMN_LATE_TASK, 0)
            put(COLUMN_UNFINISHED_TASK, 0)
            put(COLUMN_ON_TIME_TASK, 0)
        }
        db?.insert(TABLE_TRACKER_NAME, null, initialValues)
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)

        val dropChecklistTableQuery = "DROP TABLE IF EXISTS $TABLE_CHECKLIST_NAME"
        db?.execSQL(dropChecklistTableQuery)

        val dropChecklistItemTableQuery = "DROP TABLE IF EXISTS $TABLE_CHECKLIST_ITEM_NAME"
        db?.execSQL(dropChecklistItemTableQuery)

        val dropReminderTableQuery = "DROP TABLE IF EXISTS $TABLE_REMINDER_NAME"
        db?.execSQL(dropReminderTableQuery)

        val dropTrackerTableQuery = "DROP TABLE IF EXISTS $TABLE_TRACKER_NAME"
        db?.execSQL(dropTrackerTableQuery)
        onCreate(db)
    }


    // This portion is for the Note ----------------------------------------------------------------------------------------------------------------------------
    fun insertNote(noteDC: NoteDC){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, noteDC.title)
            put(COLUMN_CONTENT, noteDC.content)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    // Get all notes for the recycle view
    fun getAllNotes(): List<NoteDC>{
        val noteList = mutableListOf<NoteDC>()
        val db = writableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        while(cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow((COLUMN_ID)))
            val title = cursor.getString(cursor.getColumnIndexOrThrow((COLUMN_TITLE)))
            val content = cursor.getString(cursor.getColumnIndexOrThrow((COLUMN_CONTENT)))

            val notedt = NoteDC(id, title, content)
            noteList.add(notedt)
        }
        cursor.close()
        db.close()
        return noteList
    }

    fun updateNote(noteDC: NoteDC){
        val db = writableDatabase
        val values = ContentValues().apply{
            put(COLUMN_TITLE, noteDC.title)
            put(COLUMN_CONTENT, noteDC.content)
        }
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(noteDC.id.toString())
        db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    fun getNoteByID(noteID: Int): NoteDC{
        val db = writableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $noteID"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow((COLUMN_ID)))
        val title = cursor.getString(cursor.getColumnIndexOrThrow((COLUMN_TITLE)))
        val content = cursor.getString(cursor.getColumnIndexOrThrow((COLUMN_CONTENT)))

        cursor.close()
        db.close()
        return NoteDC(id, title, content)
    }

    fun deleteNote(nodeID: Int){
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(nodeID.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }

    // This portion is for checklist ---------------------------------------------------------------------------------------------------------------------------
    // insertChecklist has been modified, if cause error, remove the try-catch block
    fun insertChecklist(checklistDC: ChecklistDC){
        val db = writableDatabase
        try{
            val values = ContentValues().apply {
                put(COLUMN_CHECKLIST_TITLE, checklistDC.checklistTitle)
            }
            db.insert(TABLE_CHECKLIST_NAME, null, values)
            db.close()
        } catch (e: SQLiteConstraintException){
            Log.d("TAG", "Duplicate Checklist Title")
        }
    }

    // Get all checklists for the recycle view
    fun getAllChecklists(): List<ChecklistDC>{
        val checklistList = mutableListOf<ChecklistDC>()
        val db = writableDatabase
        val query = "SELECT * FROM $TABLE_CHECKLIST_NAME"
        val cursor = db.rawQuery(query, null)

        while(cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow((COLUMN_CHECKLIST_ID)))
            val title = cursor.getString(cursor.getColumnIndexOrThrow((COLUMN_CHECKLIST_TITLE)))

            val checklistDC = ChecklistDC(id, title)
            checklistList.add(checklistDC)
        }
        cursor.close()
        db.close()
        return checklistList
    }

    fun updateChecklist(checklistDC: ChecklistDC){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CHECKLIST_TITLE, checklistDC.checklistTitle)
        }
        val whereClause = "$COLUMN_CHECKLIST_ID = ?"
        val whereArgs = arrayOf(checklistDC.checklistID.toString())
        db.update(TABLE_CHECKLIST_NAME, values, whereClause, whereArgs)
        db.close()
    }

    fun getChecklistByID(checklistID: Int): ChecklistDC{
        val db = writableDatabase
        val query = "SELECT * FROM $TABLE_CHECKLIST_NAME WHERE $COLUMN_CHECKLIST_ID = $checklistID"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow((COLUMN_CHECKLIST_ID)))
        val title = cursor.getString(cursor.getColumnIndexOrThrow((COLUMN_CHECKLIST_TITLE)))

        cursor.close()
        db.close()
        return ChecklistDC(id, title)
    }

    fun deleteChecklist(checklistID: Int){
        val db = writableDatabase
            // Delete from Item table
            val whereClauseItems = "$COLUMN_CHECKLIST_ID_FK = ?"
            val whereArgsItems = arrayOf(checklistID.toString())
            db.delete(TABLE_CHECKLIST_ITEM_NAME, whereClauseItems, whereArgsItems)

            //Delete from Checklist table
            val whereClause = "$COLUMN_CHECKLIST_ID = ?"
            val whereArgs = arrayOf(checklistID.toString())
            db.delete(TABLE_CHECKLIST_NAME, whereClause, whereArgs)

            db.close()
    }


    // This portion is for checklist Items --------------------------------------------------------------------------------------------------------------------
    fun insertChecklistItem(checklistItemDC: ChecklistItemDC, currentChecklistID: Int){
        val db = writableDatabase
        val values = ContentValues().apply{
            put(COLUMN_CHECKLIST_ID_FK, currentChecklistID)
            put(COLUMN_ITEM_CONTENT, checklistItemDC.itemContent)
            put(COLUMN_IS_CHECKED, false)
        }
        db.insert(TABLE_CHECKLIST_ITEM_NAME, null, values)
        db.close()
    }


    // Get the currently editing checklist ID through title
    fun getCurrentChecklistID(checklistTitle: String): Int{
        val db = writableDatabase
        var currentChecklistID = -1
        // single quote for strings, without means column name
        val query = "SELECT * FROM $TABLE_CHECKLIST_NAME WHERE $COLUMN_CHECKLIST_TITLE = '$checklistTitle'"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        currentChecklistID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CHECKLIST_ID))
        cursor.close()
        db.close()
        return currentChecklistID
    }


    // Get all checklist items for the recycle view
    fun getAllChecklistsItem(currentChecklistID: Int): List<ChecklistItemDC>{
        val checklistItemList = mutableListOf<ChecklistItemDC>()
        val db = writableDatabase
        val query = "SELECT * FROM $TABLE_CHECKLIST_ITEM_NAME WHERE $COLUMN_CHECKLIST_ID_FK = ?"
        val selectionArgs = arrayOf(currentChecklistID.toString())
        val cursor = db.rawQuery(query, selectionArgs)

        while(cursor.moveToNext()){
            val itemID = cursor.getInt(cursor.getColumnIndexOrThrow((COLUMN_ITEM_ID)))
            val itemContent = cursor.getString(cursor.getColumnIndexOrThrow((COLUMN_ITEM_CONTENT)))
            val isChecked = cursor.getInt(cursor.getColumnIndexOrThrow((COLUMN_IS_CHECKED))) == 1

            val checklistItemDC = ChecklistItemDC(itemID, currentChecklistID, itemContent, isChecked)
            checklistItemList.add(checklistItemDC)
        }
        cursor.close()
        db.close()

        return checklistItemList
    }

    fun updateChecklistItem(itemId: Int, newContent: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ITEM_CONTENT, newContent)
        }
        val whereClause = "$COLUMN_ITEM_ID = ?"
        val whereArgs = arrayOf(itemId.toString())
        db.update(TABLE_CHECKLIST_ITEM_NAME, values, whereClause, whereArgs)
        db.close()
    }

    fun deleteChecklistItem(checklistItemID: Int){
        val db = writableDatabase
        val whereClauseItems = "$COLUMN_ITEM_ID = ?"
        val whereArgsItems = arrayOf(checklistItemID.toString())
        db.delete(TABLE_CHECKLIST_ITEM_NAME, whereClauseItems, whereArgsItems)
        db.close()
    }

    // Update the checkbox state (checked and unchecked)
    fun updateChecklistItemState(itemId: Int, isChecked: Boolean) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IS_CHECKED, isChecked)
        }
        val whereClause = "$COLUMN_ITEM_ID = ?"
        val whereArgs = arrayOf(itemId.toString())
        db.update(TABLE_CHECKLIST_ITEM_NAME, values, whereClause, whereArgs)
        db.close()
    }

    // Get Checklist Item ID through the content string
    fun getItemIdByContent(itemContent: String): Int {
        val db = readableDatabase
        var itemId = -1
        val query = "SELECT $COLUMN_ITEM_ID FROM $TABLE_CHECKLIST_ITEM_NAME WHERE $COLUMN_ITEM_CONTENT = ?"
        val selectionArgs = arrayOf(itemContent)
        val cursor = db.rawQuery(query, selectionArgs)
        if (cursor.moveToFirst()) {
            itemId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_ID))
        }
        cursor.close()
        db.close()
        return itemId
    }


    // This portion is for Reminder  --------------------------------------------------------------------------------------------------------------------
    fun insertReminder(reminderDC: ReminderDC){
        val db = writableDatabase
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val values = ContentValues().apply {
            put(COLUMN_TIME, timeFormat.format(reminderDC.time.time))
            put(COLUMN_DATE, dateFormat.format(reminderDC.date.time))
            put(COLUMN_REMINDER_NAME, reminderDC.reminderName)
            put(COLUMN_IS_ACTIVATED, true)
        }
        db.insert(TABLE_REMINDER_NAME, null, values)
        db.close()
    }

    // Get all reminder for the recycle view
    fun getAllReminder(): List<ReminderDC>{
        val reminderList = mutableListOf<ReminderDC>()
        val db = writableDatabase
        val query = "SELECT * FROM $TABLE_REMINDER_NAME"
        val cursor = db.rawQuery(query, null)

        while(cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REMINDER_ID))

            // Parse time string to Calendar
            val time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME))
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val timeCalendar = Calendar.getInstance()
            timeCalendar.time = timeFormat.parse(time) ?: Date()

            // Parse date string to Calendar
            val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val dateCalendar = Calendar.getInstance()
            dateCalendar.time = dateFormat.parse(date) ?: Date()

            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REMINDER_NAME))
            val isActivated = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ACTIVATED)) == 1

            val reminder = ReminderDC(id, timeCalendar, dateCalendar, name, isActivated)
            reminderList.add(reminder)
        }
        cursor.close()
        db.close()
        return reminderList
    }

    fun updateReminder(reminderID: Int, newTime: String, newDate: String, newName: String){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TIME, newTime)
            put(COLUMN_DATE, newDate)
            put(COLUMN_REMINDER_NAME, newName)
        }
        val whereClause = "$COLUMN_REMINDER_ID = ?"
        val whereArgs = arrayOf(reminderID.toString())
        db.update(TABLE_REMINDER_NAME,values, whereClause, whereArgs)
        db.close()
    }

    fun deleteReminder(reminderID: Int){
        val db = writableDatabase
        val whereClause = "$COLUMN_REMINDER_ID = ?"
        val whereArgs = arrayOf(reminderID.toString())
        db.delete(TABLE_REMINDER_NAME, whereClause, whereArgs)
        db.close()
    }

    // Update the state of the switch (ON or OFF)
    fun updateReminderState(reminderID: Int, isActivated: Boolean){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IS_ACTIVATED, isActivated)
        }
        val whereClause = "$COLUMN_REMINDER_ID = ?"
        val whereArgs = arrayOf(reminderID.toString())
        db.update(TABLE_REMINDER_NAME, values, whereClause, whereArgs)
        db.close()
    }

    // This portion is for the Progress Tracker ----------------------------------------------------------------------------------------------------------------------------

    // Increment the selected task by 1
    fun rateTask(taskType: String) {
        val db = writableDatabase
        val selection = "1" // Dummy selection since I want to update all rows
        db.execSQL("UPDATE $TABLE_TRACKER_NAME SET $taskType = $taskType + 1 WHERE $selection")
        db.close()
    }

    // Reset all columns to 0
    fun deleteProgress() {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LATE_TASK, 0)
            put(COLUMN_UNFINISHED_TASK, 0)
            put(COLUMN_ON_TIME_TASK, 0)
        }

        db.update(TABLE_TRACKER_NAME, values, null, null)
        db.close()
    }


    // Get value from the late task column
    fun getLateTasks(): Int {
        val db = readableDatabase
        val query = "SELECT $COLUMN_LATE_TASK FROM $TABLE_TRACKER_NAME"
        val cursor = db.rawQuery(query, null)
        var lateTasksCount = 0

        if (cursor.moveToFirst()) {
            lateTasksCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LATE_TASK))
        }

        cursor.close()
        db.close()
        return lateTasksCount
    }

    // Get value from the unfinished task column
    fun getUnfinishedTasks(): Int {
        val db = readableDatabase
        val query = "SELECT $COLUMN_UNFINISHED_TASK FROM $TABLE_TRACKER_NAME"
        val cursor = db.rawQuery(query, null)
        var unfinishedTasksCount = 0

        if (cursor.moveToFirst()) {
            unfinishedTasksCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_UNFINISHED_TASK ))
        }

        cursor.close()
        db.close()
        return unfinishedTasksCount
    }


    // Get value from the on time task column
    fun getOnTimeTasks(): Int {
        val db = readableDatabase
        val query = "SELECT $COLUMN_ON_TIME_TASK FROM $TABLE_TRACKER_NAME"
        val cursor = db.rawQuery(query, null)
        var onTimeTasksCount = 0

        if (cursor.moveToFirst()) {
            onTimeTasksCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ON_TIME_TASK))
        }

        cursor.close()
        db.close()
        return onTimeTasksCount
    }
}