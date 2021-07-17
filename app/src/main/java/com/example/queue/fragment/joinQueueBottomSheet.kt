package com.example.queue.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.queue.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class joinQueueBottomSheet : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(
            R.layout.join_queue_bottom_sheet_layout,
            container, false
        )
        val course_button = v.findViewById<Button>(R.id.course_button)
        course_button.setOnClickListener {
            Toast.makeText(
                activity,
                "Course Shared", Toast.LENGTH_SHORT
            )
                .show()
            dismiss()
        }
        return v
    }
}
