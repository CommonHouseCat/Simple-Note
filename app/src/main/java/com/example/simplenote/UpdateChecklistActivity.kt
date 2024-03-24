package com.example.simplenote

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplenote.databinding.ActivityAddChecklistBinding

class UpdateChecklistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddChecklistBinding
    private lateinit var db: DatabaseHelper
    private lateinit var checklistItemAdapter: ChecklistItemAdapter
    private var checklistId: Int  = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddChecklistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

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
            showCreateChecklistItemDialog(title)
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

    // Working with the Dialog view for adding new Checklist Item
    private fun showCreateChecklistItemDialog(title: String){
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_checklist_item, null)
        val editTextChecklistItem = dialogView.findViewById<EditText>(R.id.dialogAddChecklistItemContent)

        val dialogBuilder = AlertDialog.Builder(this).setView(dialogView)
        val dialog = dialogBuilder.create()

        dialogView.findViewById<Button>(R.id.dialogAddButtonConfirm).setOnClickListener {
            val content = editTextChecklistItem.text.toString()
            val currentChecklistID = db.getCurrentChecklistID(title)
            val checklistItem = ChecklistItemDC(0, currentChecklistID, content, false)
            db.insertChecklistItem(checklistItem, currentChecklistID)
            onResume()
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.dialogAddButtonCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}

