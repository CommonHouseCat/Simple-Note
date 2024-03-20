package com.example.simplenote

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class NoteDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    companion object{
        private const val DATABASE_NAME = "simplenote.db"
        private const val DATABASE_VERSION = 1
        //Table for notes
        private const val TABLE_NAME = "notes"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"
        //Table for checklists
        private const val TABLE_CHECKLIST_NAME = "checklists"
        private const val COLUMN_CHECKLIST_ID = "checklistID"
        private const val COLUMN_CHECKLIST_TITLE = "checklistTitle"
        //Table for checklist items
        private const val TABLE_CHECKLIST_ITEM_NAME = "checklistsItem"
        private const val COLUMN_ITEM_ID = "itemID"
        private const val COLUMN_CHECKLIST_ID_FK = "itemFK"
        private const val COLUMN_ITEM_CONTENT = "itemContent"
        private const val COLUMN_IS_CHECKED = "itemIsChecked"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TITLE TEXT, $COLUMN_CONTENT TEXT)"
        val createChecklistTableQuery = "CREATE TABLE $TABLE_CHECKLIST_NAME ($COLUMN_CHECKLIST_ID INTEGER PRIMARY KEY, $COLUMN_CHECKLIST_TITLE TEXT)"
        val createChecklistItemTableQuery = "CREATE TABLE $TABLE_CHECKLIST_ITEM_NAME ($COLUMN_ITEM_ID INTEGER PRIMARY KEY, $COLUMN_CHECKLIST_ID_FK INTEGER, $COLUMN_ITEM_CONTENT TEXT, $COLUMN_IS_CHECKED INTEGER DEFAULT 0, FOREIGN KEY ($COLUMN_CHECKLIST_ID_FK) REFERENCES $TABLE_CHECKLIST_NAME($COLUMN_CHECKLIST_ID))"

        db?.execSQL(createTableQuery)
        db?.execSQL(createChecklistTableQuery)
        db?.execSQL(createChecklistItemTableQuery)
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)

        val dropChecklistTableQuery = "DROP TABLE IF EXISTS $TABLE_CHECKLIST_NAME"
        db?.execSQL(dropChecklistTableQuery)

        val dropChecklistItemTableQuery = "DROP TABLE IF EXISTS $TABLE_CHECKLIST_ITEM_NAME"
        db?.execSQL(dropChecklistItemTableQuery)

        onCreate(db)
    }


    // This portion is for the Note ----------------------------------------------------------------
    fun insertNote(noteDT: NoteDT){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, noteDT.title)
            put(COLUMN_CONTENT, noteDT.content)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllNotes(): List<NoteDT>{
        val noteList = mutableListOf<NoteDT>()
        val db = writableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        while(cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow((COLUMN_ID)))
            val title = cursor.getString(cursor.getColumnIndexOrThrow((COLUMN_TITLE)))
            val content = cursor.getString(cursor.getColumnIndexOrThrow((COLUMN_CONTENT)))

            val notedt = NoteDT(id, title, content)
            noteList.add(notedt)
        }
        cursor.close()
        db.close()
        return noteList
    }

    fun updateNote(noteDT: NoteDT){
        val db = writableDatabase
        val values = ContentValues().apply{
            put(COLUMN_TITLE, noteDT.title)
            put(COLUMN_CONTENT, noteDT.content)
        }
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(noteDT.id.toString())
        db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    fun getNoteByID(noteID: Int): NoteDT{
        val db = writableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $noteID"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow((COLUMN_ID)))
        val title = cursor.getString(cursor.getColumnIndexOrThrow((COLUMN_TITLE)))
        val content = cursor.getString(cursor.getColumnIndexOrThrow((COLUMN_CONTENT)))

        cursor.close()
        db.close()
        return NoteDT(id, title, content)
    }

    fun deleteNote(nodeID: Int){
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(nodeID.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }

    // This portion is for checklist ---------------------------------------------------------------
    fun insertChecklist(checklistDC: ChecklistDC){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CHECKLIST_TITLE, checklistDC.checklistTitle)
        }
        db.insert(TABLE_CHECKLIST_NAME, null, values)
        db.close()
    }

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
            // Delete from Item table, could be a future problem here!!!
            val whereClauseItems = "$COLUMN_CHECKLIST_ID_FK = ?"
            val whereArgsItems = arrayOf(checklistID.toString())
            db.delete(TABLE_CHECKLIST_ITEM_NAME, whereClauseItems, whereArgsItems)

            //Delete from Checklist table
            val whereClause = "$COLUMN_CHECKLIST_ID = ?"
            val whereArgs = arrayOf(checklistID.toString())
            db.delete(TABLE_CHECKLIST_NAME, whereClause, whereArgs)

            db.close()
    }



    // This portion is for checklist Items --------------------------------------------------------
    fun insertChecklistItem(checklistItemDC: ChecklistItemDC){
        val db = writableDatabase
        val values = ContentValues().apply{
            put(COLUMN_CHECKLIST_ID_FK, checklistItemDC.checklistIDfk)
            put(COLUMN_CONTENT, checklistItemDC.itemContent)
        }
        db.insert(TABLE_CHECKLIST_ITEM_NAME, null, values)
        db.close()
    }

    // Get the currently editing checklist ID for the checklist Item (Use in AddChecklistActivity)
    fun getCurrentChecklistID(): Int{
        val db = writableDatabase
        var checklistID = -1

        val query = "SELECT MAX($COLUMN_CHECKLIST_ID) FROM $TABLE_CHECKLIST_NAME"
        val cursor = db.rawQuery(query, null)

        cursor.use { c ->
            while (c.moveToNext()) {
                // Retrieve the checklist ID from the cursor
                checklistID = cursor.getInt(0)
            }
        }
        cursor.close()
        db.close()

        return checklistID
    }

    fun getAllChecklistsItem(currentChecklistID: Int): List<ChecklistItemDC>{
        val checklistItemList = mutableListOf<ChecklistItemDC>()
        val db = writableDatabase
        val query = "SELECT * FROM $TABLE_CHECKLIST_ITEM_NAME WHERE $COLUMN_CHECKLIST_ID_FK = ?"
        val selectionArgs = arrayOf(currentChecklistID.toString())
        val cursor = db.rawQuery(query, selectionArgs)

        while(cursor.moveToNext()){
            val itemID = cursor.getInt(cursor.getColumnIndexOrThrow((COLUMN_ITEM_ID)))
            val itemContent = cursor.getString(cursor.getColumnIndexOrThrow((COLUMN_ITEM_CONTENT)))
            val isChecked = cursor.getInt(cursor.getColumnIndexOrThrow((COLUMN_IS_CHECKED))) == 0

            val checklistItemDC = ChecklistItemDC(itemID, currentChecklistID, itemContent, isChecked)
            checklistItemList.add(checklistItemDC)
        }
        cursor.close()
        db.close()

        return checklistItemList
    }



}