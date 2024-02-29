package com.example.simplenote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private lateinit var nextButton:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Reference the toolbar XML file
        val toolbar:Toolbar = findViewById(R.id.toolbar)

        // Set the toolbar as the app bar for the activity
        setSupportActionBar(toolbar)

        // Set the title and logo for this activity
        supportActionBar?.title = " Note";
        toolbar.setLogo(R.drawable.ic_add_new_note)

        //
        nextButton = findViewById(R.id.button)


    }

    fun goToNoteActivity(view: View) {
        val intent = Intent(this, Note_Activity::class.java)
        startActivity(intent)
    }

}