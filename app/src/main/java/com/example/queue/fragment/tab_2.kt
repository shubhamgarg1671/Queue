package com.example.queue.fragment

import android.content.Intent
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
import com.example.queue.R
import com.example.queue.yourQueueActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

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
            val queueTitile = view.findViewById<EditText>(R.id.queueTitile).text.toString()
            var averageTime:Int? = view.findViewById<EditText>(R.id.averageTime).text.toString().toIntOrNull()
            Log.d(TAG, "createQueueButton clicked with averageTime $averageTime and queueTitile $queueTitile")

            if (averageTime == null) {
                averageTime = 2
                Toast.makeText(requireActivity(),"averageTime sets to default 2 min",Toast.LENGTH_LONG).show()
            }
            val uid:String = auth.uid!!
            val currentTimeStamp:String = System.currentTimeMillis().toString()
            val queueID:String = uid + currentTimeStamp
            // Database reference
            var myRef = database.getReference("queue/$queueID/queueTitle")
            myRef.setValue(queueTitile)
            myRef = database.getReference("queue/$queueID/averageTime")
            myRef.setValue(averageTime)
            myRef = database.getReference("queue/$queueID/totalToken")
            myRef.setValue(0)
            myRef = database.getReference("queue/$queueID/currentToken")
            myRef.setValue(0)

            val intent:Intent = Intent(activity,yourQueueActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, queueID)
            }
            startActivity(intent)
        }
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