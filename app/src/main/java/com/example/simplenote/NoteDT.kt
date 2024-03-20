package com.example.simplenote

data class NoteDT(val id: Int, val title: String, val content: String)

data class ChecklistDC(val checklistID: Int, val checklistTitle: String)

data class ChecklistItemDC(val itemID: Int, val checklistIDfk: Int, val itemContent: String, val isChecked: Boolean)
