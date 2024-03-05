package com.example.simplenote

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.simplenote.databinding.ActivityMainBinding

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: NoteDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NoteDBHelper(this)

        binding.saveButton.setOnClick

        }
    }
