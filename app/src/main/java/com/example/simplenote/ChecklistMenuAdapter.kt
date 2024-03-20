package com.example.simplenote

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ChecklistMenuAdapter(private var checklists: List<ChecklistDC>, context: Context) : RecyclerView.Adapter<ChecklistMenuAdapter.ChecklistViewHolder>() {

    private var db : NoteDBHelper = NoteDBHelper(context)

    class ChecklistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val checklistTitleTextView: TextView = itemView.findViewById(R.id.titleChecklistMenuItemTextView)
        val checklistDeleteButton: ImageView = itemView.findViewById(R.id.deleteChecklistMenuItemButton)
        val checklistUpdateButton: ImageView = itemView.findViewById(R.id.updateChecklistMenuItemButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_checklist_menu_item, parent, false)
        return ChecklistViewHolder(view)
    }

    override fun getItemCount(): Int = checklists.size

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        val checklist = checklists[position]
        holder.checklistTitleTextView.text = checklist.checklistTitle

        holder.checklistUpdateButton.setOnClickListener{
            val intent= Intent(holder.itemView.context, AddChecklistActivity::class.java).apply {
                putExtra("checklistID", checklist.checklistID)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.checklistDeleteButton.setOnClickListener{
            db.deleteChecklist(checklist.checklistID)
            refreshData(db.getAllChecklists())
            Toast.makeText(holder.itemView.context, "Checklist Deleted", Toast.LENGTH_SHORT).show()
        }
    }

    fun refreshData(newChecklists: List<ChecklistDC>){
        checklists = newChecklists
        notifyDataSetChanged()
    }

}