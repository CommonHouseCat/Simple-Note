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

        // Update Checklist Button
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

        // Checklist Items related
        // Add Item button
        val title = checklist.checklistTitle
        binding.addChecklistItemButton.setOnClickListener{
            showChecklistItemPopup(title)
        }

        // Checklist Item RecycleView
        val currentChecklistID = db.getCurrentChecklistID(title)
        checklistItemAdapter = ChecklistItemAdapter(db.getAllChecklistsItem(currentChecklistID), this)
        binding.addChecklistRecycleView.layoutManager = LinearLayoutManager(this)
        binding.addChecklistRecycleView.adapter = checklistItemAdapter

    }

    override fun onResume() {
        super.onResume()
        val checklist = db.getChecklistByID(checklistId)
        val title = checklist.checklistTitle
        val currentChecklistID = db.getCurrentChecklistID(title)
        checklistItemAdapter.refreshData(db.getAllChecklistsItem(currentChecklistID))
    }

    private fun showChecklistItemPopup(title: String){
        val fragmentManager = supportFragmentManager
        val currentChecklistID = db.getCurrentChecklistID(title)
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = ChecklistItemPopup(currentChecklistID)
        fragmentTransaction.add(fragment, "ChecklistItemPopup")
        fragmentTransaction.commit()
    }
}