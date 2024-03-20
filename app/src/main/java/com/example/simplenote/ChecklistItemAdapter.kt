package com.example.simplenote

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChecklistItemAdapter(private var checklistItems: List<ChecklistItemDC>, context: Context) : RecyclerView.Adapter<ChecklistItemAdapter.ChecklistItemViewHolder>() {

    class ChecklistItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val checklistItemContentTextView: TextView = itemView.findViewById(R.id.contentChecklistItemTextView)
        //val checklistItemChecklistBox: CheckBox = itemView.findViewById(R.id.checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_checklist_item, parent, false)
        return ChecklistItemViewHolder(view)
    }

    override fun getItemCount(): Int = checklistItems.size

    override fun onBindViewHolder(holder: ChecklistItemViewHolder, position: Int) {
        val checklistItem = checklistItems[position]
        holder.checklistItemContentTextView.text = checklistItem.itemContent
    }

    fun refreshData(newChecklistItems: List<ChecklistItemDC>){
        checklistItems = newChecklistItems
        notifyDataSetChanged()
    }

}