package com.example.queue.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.queue.R
import com.example.queue.yourQueueActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [tab_2.newInstance] factory method to
 * create an instance of this fragment.
 */
class tab_2 : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var auth: FirebaseAuth
    val TAG = "tab_2 fragment"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view:View = inflater.inflate(R.layout.fragment_tab_2, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Database instance
        val database = FirebaseDatabase.getInstance()

        val createQueueButton:Button = view.findViewById(R.id.createQueueButton)
        createQueueButton.setOnClickListener {
            var message:String = "Something went wrong";
            val sharedPref: SharedPreferences = activity?.getSharedPreferences("tab_1", Context.MODE_PRIVATE)!!
            val queueTitile = view.findViewById<EditText>(R.id.queueTitile).text.toString()

            if (sharedPref.getString("queueID", null) != null) {
                message = "First leave the queue you have joined"
                showAlertBox(message);
            } else if (queueTitile.length < 5) {
                message = "Too short QueueTitle"
                showAlertBox(message);
            } else {
                var averageTime: Int? =
                    view.findViewById<EditText>(R.id.averageTime).text.toString().toIntOrNull()
                if (averageTime == null) {
                    averageTime = 2
                }

                var myRef = database.getReference("queue")
                myRef = myRef.push()
                if (myRef.key == null) {
                    Toast.makeText(
                        requireActivity(),
                        "Unable to generate Queue ID",
                        Toast.LENGTH_LONG
                    ).show()
                }
                val queueID: String = myRef.key!!
                myRef = database.getReference("queue/$queueID/data/queueTitle")
                myRef.setValue(queueTitile)
                myRef = database.getReference("queue/$queueID/data/averageTime")
                myRef.setValue(averageTime)
                myRef = database.getReference("queue/$queueID/data/totalToken")
                myRef.setValue(0)
                myRef = database.getReference("queue/$queueID/data/currentToken")
                myRef.setValue(0)
                myRef = database.getReference("queue/$queueID/data/queueFull")
                myRef.setValue(false)

                val intent: Intent = Intent(activity, yourQueueActivity::class.java).apply {
                    putExtra(EXTRA_MESSAGE, queueID)
                }

                val queue = Volley.newRequestQueue(requireActivity())
                // this was an extra api call just to awake heroku server
                val url = "https://queue-server.herokuapp.com/"
                val stringRequest = StringRequest(
                    Request.Method.GET, url,
                    { response ->

                    },
                    { error ->
                        Log.d(TAG, "API call failed with: error = $error")
                        Toast.makeText(
                            requireActivity(),
                            "Notification not working",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                queue.add(stringRequest)
                sharedPref.edit().putString("myQueueID",queueID).apply()

                startActivity(intent)
            }
        }
    }

    private fun showAlertBox(message: String) {
        // Create the object of
        // AlertDialog Builder class
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder.setMessage(message)
        builder.setTitle("Alert")
        // Set Cancelable false
        // for when the user clicks on the outside
        // the Dialog Box then it will remain show
        builder.setCancelable(false)
        //ok button to remove dialog
        builder
            .setPositiveButton(
                "ok"
            ) { dialog, which ->
                Log.d(TAG, "onViewCreated() called with: dialog = $dialog, _ = $which")
                dialog.cancel()
            }
        // Create the Alert dialog
        val alertDialog: AlertDialog = builder.create()
        // Show the Alert Dialog box
        alertDialog.show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment tab_2.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            tab_2().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}