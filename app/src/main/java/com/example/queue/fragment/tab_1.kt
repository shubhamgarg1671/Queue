package com.example.queue.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.queue.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.properties.Delegates

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

val TAG = "tab1_fragment"
/**
 * A simple [Fragment] subclass.
 * Use the [tab_1.newInstance] factory method to
 * create an instance of this fragment.
 */
class tab_1 : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        var queueID:String? = null
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

        if (queueID != null) {
            inQueueUpdateUI()
            updateData(queueID, false)
        } else {
            noQueueUpdateUI()
        }
        joinQueueButton.setOnClickListener {
            Log.d(TAG, "joinQueueButton clicked")
            val dialog = BottomSheetDialog(requireActivity())

            // on below line we are inflating a layout file for bottom sheet.
            val bottomSheetView = layoutInflater.inflate(R.layout.join_queue_bottom_sheet_layout, null)

            // on below line we are creating a variable for our button
            // which is in the bottom sheet.
            val joinQueue:Button = bottomSheetView.findViewById<Button>(R.id.joinQueue)
            // on below line we are adding on click listener
            // for join Queue button in bottom sheet.
            joinQueue.setOnClickListener {
                val pasteQueueID:EditText = bottomSheetView.findViewById(R.id.pasteQueueID)
                queueID = pasteQueueID.text.toString()
                updateData(queueID.toString(),true)
                // on below line we are calling a dismiss
                // method to close our dialog.
                dialog.dismiss()
            }

            // on below line we are setting
            // content view to our bottomSheetView.
            dialog.setContentView(bottomSheetView)

            // on below line we are calling
            // a show method to display a dialog.
            dialog.show()
        }
            leaveQueueButton.setOnClickListener {
            Log.d(TAG, "leaveQueueButton clicked")
        }
    }

    private fun updateData(queueID: String, newlyJoined:Boolean) {

        var newlyJoined = newlyJoined   // Don't know why but without this newlyJoined is val so I can not change it's value
        //database reference
        var myRef = database.getReference("queue/$queueID")

        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue()
                Log.d(TAG, "Value is: $value")
                if (value == null) {
                    Toast.makeText(activity,"Incorrect Queue ID", Toast.LENGTH_LONG).show()
                } else {
                    val queueTitle = dataSnapshot.child("queueTitle").value.toString()
                    val averageTime:Int = dataSnapshot.child("averageTime").value.toString().toInt()
                    if (newlyJoined) {
                        var totalToken: Int =
                            dataSnapshot.child("totalToken").value.toString().toInt()
                        totalToken++    // total tokens are incremented by one
                        yourToken.text =
                            totalToken.toString()  // last token is given to user who joined
                        myRef = database.getReference("queue/$queueID/totalToken")
                        myRef.setValue(totalToken)
                            token = totalToken
                        inQueueUpdateUI()
                        newlyJoined = false   // without this onDataChanged is called infinitely
                    }
                    val currToken:Int = dataSnapshot.child("currentToken").value.toString().toInt()
                    if (currToken == token) {
                        noQueueUpdateUI()
                    }
                    expectedWaitingTime.text = ((token - currToken) * averageTime).toString()
                    currentToken.text = currToken.toString()
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
}