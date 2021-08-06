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

    var currToken:Int = 0
    var totalTokenInt:Int = 0
    val TAG = "yourQueueActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_queue)
        val queueID = intent.getStringExtra(EXTRA_MESSAGE)!!
        val currentTokenView:TextView = findViewById(R.id.cuurentTokenYourQueue)
        val totalTokanView:TextView = findViewById(R.id.totalToken)
        copyToClipBoard(queueID)
        val myRef = database.getReference("queue/$queueID")

        val callNext:Button = findViewById(R.id.callNext)
        callNext.setOnClickListener {
            val oldToken:Int = currentTokenView.text.toString().toInt()
            currentTokenView.text = (oldToken + 1).toString()
            val ref = database.getReference("queue/$queueID/currentToken")
            ref.setValue(oldToken + 1)
            val queue = Volley.newRequestQueue(this)
            val deviceToken = "fPQ6R2E5QiONjugWqujuZ5:APA91bFBNzyRks6HvZkaMY4hAtCtlKKPJyzNgJy6RR573mC0ETmciDQ0h2pLWwkLXVyiR_UesY-R1HT2AhPRFTTAYUi_ISuBqQntcqbOMBccq_dLE8UGGYSNPL7AcrE5a0j_uWHi8Q51"
            val url = "https://queue-server.herokuapp.com/$deviceToken"

            // Request a string response from the provided URL.
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    // Display the first 500 characters of the response string.
                    //textView.text = "Response is: ${response.substring(0, 500)}"
                    Toast.makeText(this,response,Toast.LENGTH_LONG).show()
                    Log.d(TAG, "volley response = $response")
                },
                { error ->
                    Toast.makeText(this,error.message,Toast.LENGTH_LONG).show()
                    Log.e(TAG, "Volley error = $error")
                //    textView.text = "That didn't work!"
                })

            // Add the request to the RequestQueue.
            queue.add(stringRequest)

        }
        val queueFull:Button = findViewById(R.id.queueFull)
        queueFull.setOnClickListener{
            Log.d(TAG, "queueFull clicked")
            if (queueFull.text == "Queue Full") {
                queueFull.text = "Allow more"    // Botton status will be apposite to that in database
                val ref = database.getReference("queue/$queueID/queueFull")
                ref.setValue(true)
            } else {
                queueFull.text = "Queue Full"   // Botton status will be apposite to that in database
                val ref = database.getReference("queue/$queueID/queueFull")
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
                val queueTitle = dataSnapshot.child("queueTitle").value.toString()
                val textViewQueueTitle:TextView = findViewById(R.id.textQueueTitle)
                textViewQueueTitle.setText(queueTitle)
                totalTokenInt =
                    dataSnapshot.child("totalToken").value.toString().toInt()
                totalTokanView.text = totalTokenInt.toString()
                currToken =
                    dataSnapshot.child("currentToken").value.toString().toInt()
                if (currToken == totalTokenInt) {
                    callNext.visibility = View.GONE
                } else {
                    callNext.visibility = View.VISIBLE
                }
                currentTokenView.text = dataSnapshot.child("currentToken").value.toString()
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
}