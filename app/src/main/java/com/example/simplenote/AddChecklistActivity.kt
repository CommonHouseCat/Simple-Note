package com.example.simplenote

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplenote.databinding.ActivityAddChecklistBinding

class AddChecklistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddChecklistBinding
    private lateinit var db: NoteDBHelper
    private lateinit var checklistItemAdapter: ChecklistItemAdapter
    private var checklistId: Int  = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddChecklistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NoteDBHelper(this)
        val currentChecklistID = db.getCurrentChecklistID()
        checklistItemAdapter = ChecklistItemAdapter(db.getAllChecklistsItem(currentChecklistID), this)

        // Edit button function here
        checklistId = intent.getIntExtra("checklistID", -1)
        if(checklistId == -1){
            finish()
            return
        }

        val checklist = db.getChecklistByID(checklistId)
        binding.titleChecklistEditText.setText(checklist.checklistTitle)

        binding.saveChecklistButton.setOnClickListener {
            val newTitle = binding.titleChecklistEditText.text.toString()
            val updatedChecklist = ChecklistDC(checklistId, newTitle)
            db.updateChecklist(updatedChecklist)
            finish()
            Toast.makeText(this, "Checklist Updated", Toast.LENGTH_SHORT).show()
        }
        //-----------------------------------------------------------------------
        binding.addChecklistRecycleView.layoutManager = LinearLayoutManager(this)
        binding.addChecklistRecycleView.adapter = checklistItemAdapter


        binding.addChecklistItemButton.setOnClickListener{
            showChecklistItemPopup()
        }


    }

    override fun onResume() {
        super.onResume()
        val currentChecklistID = db.getCurrentChecklistID()
        checklistItemAdapter.refreshData(db.getAllChecklistsItem(currentChecklistID))
    }

    private fun showChecklistItemPopup(){
        val fragmentManager = supportFragmentManager
        val currentChecklistID = db.getCurrentChecklistID()
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = ChecklistItemPopup(currentChecklistID)
        fragmentTransaction.add(fragment, "ChecklistItemPopup")
        fragmentTransaction.commit()
    }
}