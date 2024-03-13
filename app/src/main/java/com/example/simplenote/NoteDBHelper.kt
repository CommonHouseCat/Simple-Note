package com.example.simplenote

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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
        private const val TABLE_NAME_CHECKLIST = "checklists"
        private const val COLUMN_ID_CHECKLIST = "checklistID"
        private const val COLUMN_TITLE_CHECKLIST = "checklistTitle"
        //Table for checklist item
        private const val TABLE_NAME_CHECKLIST_ITEM = "checklistsItem"
        private const val COLUMN_ID_CHECKLIST_ITEM = "itemID"
        private const val COLUMN_CONTENT_CHECKLIST_ITEM = "itemContent"
        private const val COLUMN_IS_CHECKED = "isChecked"
        private const val COLUMN_CHECKLIST_FOREIGN_KEY = "fkID"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TITLE TEXT, $COLUMN_CONTENT TEXT)"
        db?.execSQL(createTableQuery)

        val createChecklistTableQuery = "CREATE TABLE $TABLE_NAME_CHECKLIST ($COLUMN_ID_CHECKLIST INTEGER PRIMARY KEY, $COLUMN_TITLE_CHECKLIST TEXT)"
        db?.execSQL(createChecklistTableQuery)

        val createChecklistItemTableQuery = "CREATE TABLE $TABLE_NAME_CHECKLIST_ITEM ($COLUMN_ID_CHECKLIST_ITEM INTEGER PRIMARY KEY, $COLUMN_CONTENT_CHECKLIST_ITEM TEXT, $COLUMN_IS_CHECKED INTEGER DEFAULT 0, $COLUMN_CHECKLIST_FOREIGN_KEY INTEGER, FOREIGN KEY($COLUMN_CHECKLIST_FOREIGN_KEY) REFERENCES $TABLE_NAME_CHECKLIST ($COLUMN_ID_CHECKLIST) ON DELETE CASCADE ON UPDATE CASCADE, CONSTRAINT fk_checklist_id FOREIGN KEY($COLUMN_CHECKLIST_FOREIGN_KEY) REFERENCES $TABLE_NAME_CHECKLIST($COLUMN_ID_CHECKLIST))"
        db?.execSQL(createChecklistItemTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)

        val dropChecklistTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME_CHECKLIST"
        db?.execSQL(dropChecklistTableQuery)

        val dropChecklistItemTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME_CHECKLIST_ITEM"
        db?.execSQL(dropChecklistItemTableQuery)

        onCreate(db)
    }


    // This portion is for the Note
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

    // This portion is for checklist

}