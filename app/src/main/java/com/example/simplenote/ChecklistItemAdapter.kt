package com.example.simplenote

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ChecklistItemAdapter(private var checklistItems: List<ChecklistItemDC>, private val context: Context) : RecyclerView.Adapter<ChecklistItemAdapter.ChecklistItemViewHolder>() {

    private val db: NoteDBHelper = NoteDBHelper(context)


    class ChecklistItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val checklistItemContentTextView: TextView = itemView.findViewById(R.id.contentChecklistItemTextView)
        val checklistItemDeleteButton: ImageView = itemView.findViewById(R.id.deleteChecklistItemButton)
        val checklistItemCheckbox: CheckBox = itemView.findViewById(R.id.checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_checklist_item, parent, false)
        return ChecklistItemViewHolder(view)
    }

    override fun getItemCount(): Int = checklistItems.size



    override fun onBindViewHolder(holder: ChecklistItemViewHolder, position: Int) {
        val checklistItem = checklistItems[position]
        holder.checklistItemContentTextView.text = checklistItem.itemContent
        holder.checklistItemCheckbox.isChecked = checklistItem.isChecked


        // Update Checklist Content with a dialog
        holder.checklistItemContentTextView.setOnClickListener {
            val oldContent = checklistItem.itemContent

            // Inflate the dialog layout
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_checklist_item, null)

            // Create the dialog
            val builder = AlertDialog.Builder(context)
                .setView(dialogView)

            val editTextChecklistItem = dialogView.findViewById<EditText>(R.id.dialogEditChecklistItemContent)
            editTextChecklistItem.setText(oldContent)

            val dialog = builder.create()

            // Set click listeners for confirm and cancel buttons
            dialogView.findViewById<Button>(R.id.dialogButtonConfirm).setOnClickListener {
                val newContent = editTextChecklistItem.text.toString()
                // Update the checklist item in the database
                db.updateChecklistItem(checklistItem.itemID, newContent)

                // Update the checklistItem in the list
                val updatedChecklistItem = checklistItem.copy(itemContent = newContent)
                val updatedList = checklistItems.toMutableList()
                updatedList[holder.adapterPosition] = updatedChecklistItem
                checklistItems = updatedList.toList()

                // Update the UI
                refreshData(db.getAllChecklistsItem(checklistItem.checklistIDfk))
                dialog.dismiss()
            }

            dialogView.findViewById<Button>(R.id.dialogButtonCancel).setOnClickListener {
                dialog.dismiss()
            }
            // Show the dialog
            dialog.show()
        }

        // Update appearance of content text based on checkbox state
        if (checklistItem.isChecked) {
            // Cross out text if checkbox is checked
            holder.checklistItemContentTextView.paintFlags = holder.checklistItemContentTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.checklistItemContentTextView.setTextColor(Color.GRAY)
        } else {
            // Remove strike-through if checkbox is unchecked
            holder.checklistItemContentTextView.paintFlags = holder.checklistItemContentTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.checklistItemContentTextView.setTextColor(ContextCompat.getColor(context, R.color.orange))
        }

        // Set listener for checkbox state changes
        holder.checklistItemCheckbox.setOnCheckedChangeListener { _, isChecked ->
            // Update isChecked value in checklist item object
            checklistItem.isChecked = isChecked

            // Update appearance of content text based on checkbox state
            if (isChecked) {
                // Cross out text if checkbox is checked
                holder.checklistItemContentTextView.paintFlags = holder.checklistItemContentTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                holder.checklistItemContentTextView.setTextColor(Color.GRAY)
            } else {
                // Remove strike-through if checkbox is unchecked
                holder.checklistItemContentTextView.paintFlags = holder.checklistItemContentTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                holder.checklistItemContentTextView.setTextColor(ContextCompat.getColor(context, R.color.orange))
            }

            // Update isChecked value in database
            val itemContent = checklistItem.itemContent
            val itemId = db.getItemIdByContent(itemContent)
            db.updateChecklistItemState(itemId, checklistItem.isChecked)

            // Notify adapter that data has changed
            refreshData(db.getAllChecklistsItem(checklistItem.checklistIDfk))
        }

        // The delete button
        holder.checklistItemDeleteButton.setOnClickListener{
            db.deleteChecklistItem(checklistItem.itemID)
            refreshData(db.getAllChecklistsItem(checklistItem.checklistIDfk))
            Toast.makeText(holder.itemView.context, "Item Deleted", Toast.LENGTH_SHORT).show()
        }

    }

    fun refreshData(newChecklistItems: List<ChecklistItemDC>){
        checklistItems = newChecklistItems
        notifyDataSetChanged()
    }
}