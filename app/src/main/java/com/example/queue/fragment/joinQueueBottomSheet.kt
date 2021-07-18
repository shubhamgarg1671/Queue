package com.example.queue.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.queue.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import org.json.JSONObject

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
//        course_button.setOnClickListener {
//            val pasteQueueID:EditText = requireView().findViewById(R.id.pasteQueueID)
//            val queueID:String = pasteQueueID.text.toString()
//            // Write a message to the database
//            val database = FirebaseDatabase.getInstance()
//            var myRef = database.getReference("queue/$queueID")
//
//            // Read from the database
//            myRef.addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    // This method is called once with the initial value and again
//                    // whenever data at this location is updated.
//                    val value = dataSnapshot.getValue()
//                    Log.d(TAG, "Value is: $value")
//                    if (value == null) {
//                        Toast.makeText(activity,"Incorrect Queue ID",Toast.LENGTH_LONG).show()
//                    } else {
////                        myRef = database.getReference("queue/$queueID/currentToken")
////                        myRef.setValue(0)
//                        val queueTitle = dataSnapshot.child("queueTitle").value.toString()
//                        val averageTime:Int = dataSnapshot.child("averageTime").value.toString().toInt()
//                        var totalToken:Int = dataSnapshot.child("totalToken").value.toString().toInt()
//                        val yourToken:Int = totalToken
//                        totalToken++
//                        myRef = database.getReference("queue/$queueID/totalToken")
//                        myRef.setValue(totalToken)
//
//                        Log.d(TAG, "average time $averageTime totalToken$totalToken")
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    // Failed to read value
//                    Log.e(TAG, "Failed to read value.", error.toException())
//                }
//            })
//            dismiss()
//        }
        return v
    }
}
