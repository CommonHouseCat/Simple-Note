package com.example.simplenote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar

class Note_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        val toolbar: Toolbar = findViewById(R.id.toolbar)

        // Set the toolbar as the app bar for the activity
        setSupportActionBar(toolbar)

        // Set the title and logo for this activity
        supportActionBar?.title = " This is a test";
    }
}