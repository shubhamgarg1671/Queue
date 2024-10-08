package com.example.queue.fragment

import android.Manifest
import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.AlarmClock
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.example.queue.R
import com.example.queue.yourQueueActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.logging.LogManager


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [tab_1.newInstance] factory method to
 * create an instance of this fragment.
 */
class tab_1 : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var codeScanner: CodeScanner

    lateinit var sharedPref: SharedPreferences
    val TAG = "tab1_fragment"
    var queueID:String? = null
    var token:Int = 0
    val database:FirebaseDatabase = FirebaseDatabase.getInstance()
    lateinit var textWaitTime:TextView
    lateinit var expectedWaitingTime:TextView
    lateinit var minText:TextView
    lateinit var textCurrentToken:TextView
    lateinit var currentToken:TextView
    lateinit var textYourToken:TextView
    lateinit var yourToken:TextView
    lateinit var leaveQueueButton:Button

    lateinit var noWaitingImage:ImageView
    lateinit var noWaiting:TextView
    lateinit var joinQueueButton:Button

    lateinit var tab_1_progressBar:ProgressBar

    val auth = FirebaseAuth.getInstance()
    val uid:String = auth.uid!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textWaitTime = view.findViewById(R.id.textWaitTime)
        expectedWaitingTime = view.findViewById(R.id.expectedWaitingTime)
        minText = view.findViewById(R.id.minText)
        textCurrentToken = view.findViewById(R.id.textCurrentToken)
        currentToken = view.findViewById(R.id.currentToken)
        textYourToken = view.findViewById(R.id.textYourToken)
        yourToken = view.findViewById(R.id.yourToken)
        leaveQueueButton = view.findViewById(R.id.leaveQueueButton)

        noWaitingImage = view.findViewById(R.id.noWaitingImage)
        noWaiting = view.findViewById(R.id.noWaiting)
        joinQueueButton = view.findViewById(R.id.joinQueueButton)

        tab_1_progressBar = view.findViewById(R.id.tab_1_progressBar)
        requestCamera()
        sharedPref = activity?.getSharedPreferences("tab_1", Context.MODE_PRIVATE)!!

        val myQueueID = sharedPref.getString("myQueueID", null)
        if (myQueueID != null) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
            builder.setMessage("Do you want to continue your queue you left")
            builder.setTitle("Reminder")
            // Set Cancelable false
            // for when the user clicks on the outside
            // the Dialog Box then it will remain show
            builder.setCancelable(false)
            // Set the positive button with yes name
            // OnClickListener method is use of
            // DialogInterface interface.
            builder
                .setPositiveButton(
                    "Yes",
                    DialogInterface.OnClickListener { dialog, which -> // When the user click yes button
                        // then scanBarcode activity
                        val intent: Intent = Intent(activity, yourQueueActivity::class.java).apply {
                            putExtra(EXTRA_MESSAGE, myQueueID)
                        }
                        startActivity(intent)
                    })
            // Set the Negative button with No name
            // OnClickListener method is use
            // of DialogInterface interface.
            builder
                .setNegativeButton(
                    "No",
                    DialogInterface.OnClickListener { dialog, which -> // If user click no
                        sharedPref.edit().putString("myQueueID",null).apply()

                        dialog.cancel()
                    })
            // Create the Alert dialog
            val alertDialog: AlertDialog = builder.create()
            // Show the Alert Dialog box
            alertDialog.show()
        }
        queueID = sharedPref.getString("queueID", null)
        token = sharedPref.getInt("token", 0)

        if (queueID != null) {
            inQueueUpdateUI()
            updateData(false)
        } else {
            noQueueUpdateUI()
        }
        joinQueueButton.setOnClickListener {
            Log.d(TAG, "joinQueueButton clicked")
            requestCamera()
            val dialog = BottomSheetDialog(requireActivity())

            // on below line we are inflating a layout file for bottom sheet.
            val bottomSheetView = layoutInflater.inflate(R.layout.join_queue_bottom_sheet_layout, null)
            val pasteQueueID:EditText = bottomSheetView.findViewById(R.id.pasteQueueID)
            val joinQueue:Button = bottomSheetView.findViewById<Button>(R.id.joinQueue)

            val scannerView = bottomSheetView.findViewById<CodeScannerView>(R.id.scanner_view)
            val activity = requireActivity()
            codeScanner = CodeScanner(activity, scannerView)
            codeScanner.decodeCallback = DecodeCallback {
                activity.runOnUiThread {
                    pasteQueueID.setText(it.text)
                    joinQueue.performClick()
                }
            }
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                codeScanner.startPreview()
            }

            // on below line we are creating a variable for our button
            // which is in the bottom sheet.
            // on below line we are adding on click listener
            // for join Queue button in bottom sheet.
            joinQueue.setOnClickListener {
                if (pasteQueueID.text.isEmpty()) {
                    Toast.makeText(requireActivity(),"Queue ID can not be empty",Toast.LENGTH_LONG).show()
                } else {
                    tab_1_progressBar.visibility = View.VISIBLE
                    queueID = pasteQueueID.text.toString()
                    updateData(true)
                    // on below line we are calling a dismiss
                    // method to close our dialog.
                    dialog.dismiss()
                }
            }

            // on below line we are setting
            // content view to our bottomSheetView.
            dialog.setContentView(bottomSheetView)

            // auto expand bottom sheet to full screen
            //https://www.codegrepper.com/code-examples/whatever/how+to+make+bottom+sheet+full+screen
            dialog.setOnShowListener {
                val bottomSheetDialog = it as BottomSheetDialog
                val parentLayout =
                    bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                parentLayout?.let { it ->
                    val behaviour = BottomSheetBehavior.from(it)
                    val layoutParams = it.layoutParams
                    layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
                    it.layoutParams = layoutParams
                    behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }

            // on below line we are calling
            // a show method to display a dialog.
            dialog.show()
        }
            leaveQueueButton.setOnClickListener {
            Log.d(TAG, "leaveQueueButton clicked")
                queueID = null
                noQueueUpdateUI()
                // remove from shared pref
                sharedPref.edit().putString("queueID",null).apply()
        }
    }


    private fun updateData(newlyJoined:Boolean) {

        Log.d(TAG, "updateData() called with: newlyJoined = $newlyJoined and queueID $queueID")
        var newlyJoined = newlyJoined   // Don't know why but without this newlyJoined is val so I can not change it's value

        // so that we can have a valid
        // firebase path removing special characters
        // you can also try check if these characters are
        // present in queue ID and say that queue is incorrect
        queueID = queueID?.replace(".", "")
        queueID = queueID?.replace("#", "")
        queueID = queueID?.replace("$", "")
        queueID = queueID?.replace("[", "")
        queueID = queueID?.replace("]", "")

        //database reference
        var myRef = database.getReference("queue/$queueID/data")

        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue()
                Log.d(TAG, "Value is: $value")
                if (value == null) {
                    Toast.makeText(activity,"Incorrect Queue ID", Toast.LENGTH_LONG).show()
                    tab_1_progressBar.visibility = View.GONE
                } else if (newlyJoined && dataSnapshot.child("queueFull").value.toString().toBoolean()) {
                    tab_1_progressBar.visibility = View.GONE
                    Toast.makeText(activity,"Sorry, Queue is Full", Toast.LENGTH_LONG).show()
                    // need to create an alert dialog box
                    // Toast message will not work
                    queueID = null
                    myRef.removeEventListener(this)
                } else {
                    val queueTitle = dataSnapshot.child("queueTitle").value.toString()
                    val averageTime:Int = dataSnapshot.child("averageTime").value.toString().toInt()
                    if (newlyJoined) {
                        var totalToken: Int =
                            dataSnapshot.child("totalToken").value.toString().toInt()
                        totalToken++    // total tokens are incremented by one
                        yourToken.text =
                            totalToken.toString()  // last token is given to user who joined
                        myRef = database.getReference("queue/$queueID/data/totalToken")
                        myRef.setValue(totalToken)
                            token = totalToken
                        myRef = database.getReference("queue/$queueID/client/${token}/uid")
                        myRef.setValue(uid)
                        sharedPref.edit().putString("queueID",queueID).apply()
                        sharedPref.edit().putInt("token",token).apply()

                        inQueueUpdateUI()
                        tab_1_progressBar.visibility = View.GONE
                        newlyJoined = false   // without this onDataChanged is called infinitely
                    }
                    val currToken:Int = dataSnapshot.child("currentToken").value.toString().toInt()
                    if (currToken >= token) {
                        noQueueUpdateUI()
                        sharedPref.edit().putString("queueID",null).apply()
                    }
                    expectedWaitingTime.text = ((token - currToken) * averageTime).toString()
                    currentToken.text = currToken.toString()
                    yourToken.text = token.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.e(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    private fun noQueueUpdateUI() {
        Log.d(TAG, "noQueueUpdateUI() called")
        textWaitTime.visibility = View.GONE
        expectedWaitingTime.visibility = View.GONE
        minText.visibility = View.GONE
        textCurrentToken.visibility = View.GONE
        currentToken.visibility = View.GONE
        textYourToken.visibility = View.GONE
        yourToken.visibility = View.GONE
        leaveQueueButton.visibility = View.GONE

        noWaitingImage.visibility = View.VISIBLE
        noWaiting.visibility = View.VISIBLE
        joinQueueButton.visibility = View.VISIBLE
    }

    private fun inQueueUpdateUI() {
        Log.d(TAG, "inQueueUpdateUI() called")
        textWaitTime.visibility = View.VISIBLE
        expectedWaitingTime.visibility = View.VISIBLE
        minText.visibility = View.VISIBLE
        textCurrentToken.visibility = View.VISIBLE
        currentToken.visibility = View.VISIBLE
        textYourToken.visibility = View.VISIBLE
        yourToken.visibility = View.VISIBLE
        leaveQueueButton.visibility = View.VISIBLE

        noWaitingImage.visibility = View.GONE
        noWaiting.visibility = View.GONE
        joinQueueButton.visibility = View.GONE
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment tab_1.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            tab_1().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun requestCamera() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
//            startCamera()
            Log.d(TAG, "already granted Camera permission")
        } else {
            Log.d(TAG, "requestCamera permission")
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.CAMERA
                )
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    50
                )
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    50
                )
            }
        }
    }
}