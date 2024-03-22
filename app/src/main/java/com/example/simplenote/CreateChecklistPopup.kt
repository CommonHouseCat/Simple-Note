package com.example.simplenote

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.simplenote.databinding.FragmentCreateChecklistPopupBinding

class CreateChecklistPopup : DialogFragment() {

    private var _binding: FragmentCreateChecklistPopupBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: NoteDBHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateChecklistPopupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,(resources.displayMetrics.heightPixels * 0.2).toInt())

        db = NoteDBHelper(requireContext())


        binding.buttonCreateMenu.setOnClickListener {
            val checklistTitleMenu = binding.enterChecklistTitle.text.toString()
            val checklist = ChecklistDC(0, checklistTitleMenu)
            db.insertChecklist(checklist)
            dismiss()
        }

        binding.buttonCancelMenu.setOnClickListener{
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}