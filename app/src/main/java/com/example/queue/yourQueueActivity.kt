package com.example.queue

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

class yourQueueActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()

    var queueID: String = ""
    var currToken:Int = 0
    var totalTokenInt:Int = 0
    val TAG = "yourQueueActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_queue)
        queueID = intent.getStringExtra(EXTRA_MESSAGE)!!
        val currentTokenView:TextView = findViewById(R.id.cuurentTokenYourQueue)
        val totalTokanView:TextView = findViewById(R.id.totalToken)
        val myRef = database.getReference("queue/$queueID")

        val callNext:Button = findViewById(R.id.callNext)
        callNext.setOnClickListener {
            val oldToken:Int = currentTokenView.text.toString().toInt()
            currentTokenView.text = (oldToken + 1).toString()
            val ref = database.getReference("queue/$queueID/data/currentToken")
            ref.setValue(oldToken + 1)
        }
        val queueFull:Button = findViewById(R.id.queueFull)
        queueFull.setOnClickListener{
            Log.d(TAG, "queueFull clicked")
            if (queueFull.text == "Mark as Full") {
                queueFull.text = getString(R.string.allow_more)    // Button status will be apposite to that in database
                val ref = database.getReference("queue/$queueID/data/queueFull")
                ref.setValue(true)
            } else {
                queueFull.text = getString(R.string.mark_as_full)   // Botton status will be apposite to that in database
                val ref = database.getReference("queue/$queueID/data/queueFull")
                ref.setValue(false)
            }
        }
        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue()
                Log.d(TAG, "Value is: $value")
                val queueTitle = dataSnapshot.child("data/queueTitle").value.toString()
                val textViewQueueTitle:TextView = findViewById(R.id.textQueueTitle)
                textViewQueueTitle.setText(queueTitle)
                totalTokenInt =
                    dataSnapshot.child("data/totalToken").value.toString().toInt()
                totalTokanView.text = totalTokenInt.toString()
                if (currToken != dataSnapshot.child("data/currentToken").value.toString().toInt()) {

                    currToken = dataSnapshot.child("data/currentToken").value.toString().toInt()
                    val clientUID = dataSnapshot.child("client/$currToken/uid").value.toString()
                    sendNotificationToClient(clientUID)

                    currentTokenView.text = dataSnapshot.child("data/currentToken").value.toString()
                }
                if (currToken == totalTokenInt) {
                    callNext.visibility = View.GONE
                } else {
                    callNext.visibility = View.VISIBLE
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.e(TAG, "Failed to read value.", error.toException())
            }
        })

//        val image = findViewById<ImageView>(R.id.image)
        val writer = QRCodeWriter()
        try {
            val bitMatrix = writer.encode(queueID, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            (findViewById<View>(R.id.qrCodeImage) as ImageView).setImageBitmap(bmp)
        } catch (e: WriterException) {
            e.printStackTrace()
        }

        val copyQueueIDButton:Button = findViewById(R.id.copyQueueIDButton)
        copyQueueIDButton.setOnClickListener {
            copyToClipBoard(queueID)
            Toast.makeText(this,"Queue ID copied",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        if (currToken == totalTokenInt) {
            super.onBackPressed()
        } else {
            Toast.makeText(this,"Please, Complete the Queue",Toast.LENGTH_LONG).show()
        }
    }

    private fun copyToClipBoard(queueID: String) {
        val clipboard: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label.toString(), queueID)
        clipboard.setPrimaryClip(clip)
    }
    private fun sendNotificationToClient(clientUID:String) {
        val queue = Volley.newRequestQueue(this)
        val myRef = database.getReference("user/$clientUID")
        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) { // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue()
                Log.d(TAG, "Value is: $value")
                val clientContact = dataSnapshot.child("contact").value.toString()
                val clientDeviceToken = dataSnapshot.child("deviceToken").value.toString()
                val url = "https://queue-server.herokuapp.com/$clientDeviceToken"

                // Request a string response from the provided URL.
                val stringRequest = StringRequest(
                    Request.Method.GET, url,
                    { response ->
                        Log.d(TAG, "volley response = $response")
                        Toast.makeText(this@yourQueueActivity,response,Toast.LENGTH_LONG).show()
                    },
                    { error ->
                        Log.e(TAG, "Volley error = $error")
                        Toast.makeText(this@yourQueueActivity,error.message,Toast.LENGTH_LONG).show()
                    })

                // Add the request to the RequestQueue.
                queue.add(stringRequest)
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.e(TAG, "Failed to read value.", error.toException())
                Toast.makeText(this@yourQueueActivity,"failed to read client info",Toast.LENGTH_LONG).show()
            }
        })

    }
}