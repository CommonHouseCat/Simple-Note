package com.example.simplenote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.simplenote.databinding.FragmentChecklistItemPopupBinding


class ChecklistItemPopup(private val currentChecklistID: Int) : DialogFragment() {

    private var _binding: FragmentChecklistItemPopupBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: NoteDBHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChecklistItemPopupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,(resources.displayMetrics.heightPixels * 0.2).toInt())

        db = NoteDBHelper(requireContext())

        binding.buttonConfirm.setOnClickListener{
            val itemContent = binding.editTextChecklistItem.text.toString()
            val checklistItemA = ChecklistItemDC(0, currentChecklistID, itemContent, false)
            db.insertChecklistItem(checklistItemA, currentChecklistID)
            dismiss()
        }

        binding.buttonCancel.setOnClickListener{
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}