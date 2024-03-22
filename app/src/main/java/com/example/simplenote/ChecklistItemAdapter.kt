package com.example.simplenote

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ChecklistItemAdapter(private var checklistItems: List<ChecklistItemDC>, context: Context) : RecyclerView.Adapter<ChecklistItemAdapter.ChecklistItemViewHolder>() {

    private val db: NoteDBHelper = NoteDBHelper(context)

    class ChecklistItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val checklistItemContentTextView: TextView = itemView.findViewById(R.id.contentChecklistItemTextView)
        //val checklistItemChecklistBox: CheckBox = itemView.findViewById(R.id.checkbox)
        val checklistItemDeleteButton: ImageView = itemView.findViewById(R.id.deleteChecklistItemButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_checklist_item, parent, false)
        return ChecklistItemViewHolder(view)
    }

    override fun getItemCount(): Int = checklistItems.size

    override fun onBindViewHolder(holder: ChecklistItemViewHolder, position: Int) {
        val checklistItem = checklistItems[position]
        holder.checklistItemContentTextView.text = checklistItem.itemContent


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