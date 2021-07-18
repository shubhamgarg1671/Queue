package com.example.queue

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.queue.fragment.TAG
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class yourQueueActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_queue)
        val queueID = intent.getStringExtra(EXTRA_MESSAGE)
        val currentToken:TextView = findViewById(R.id.cuurentTokenYourQueue)
        val totalTokan:TextView = findViewById(R.id.totalToken)
        val clipboard: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label.toString(), queueID)
        clipboard.setPrimaryClip(clip)

        val myRef = database.getReference("queue/$queueID")

        val callNext:Button = findViewById(R.id.callNext)
        callNext.setOnClickListener {
            val oldToken:Int = currentToken.text.toString().toInt()
            currentToken.text = (oldToken + 1).toString()
            val ref = database.getReference("queue/$queueID/currentToken")
            ref.setValue(oldToken + 1)
        }
        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue()
                Log.d(TAG, "Value is: $value")
                val queueTitle = dataSnapshot.child("queueTitle").value.toString()
                val totalTokenInt: Int =
                    dataSnapshot.child("totalToken").value.toString().toInt()
                totalTokan.text = totalTokenInt.toString()
                val currToken:Int =
                    dataSnapshot.child("currentToken").value.toString().toInt()
                if (currToken == totalTokenInt) {
                    callNext.visibility = View.GONE
                } else {
                    callNext.visibility = View.VISIBLE
                }
                currentToken.text = dataSnapshot.child("currentToken").value.toString()
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.e(TAG, "Failed to read value.", error.toException())
            }
        })

    }
    private fun updateData(queueID: String, newlyJoined:Boolean) {

        var newlyJoined = newlyJoined   // Don't know why but without this newlyJoined is val so I can not change it's value
        //database reference

    }

}