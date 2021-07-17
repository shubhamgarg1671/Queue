package com.example.queue.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import com.example.queue.R
import com.google.android.gms.dynamic.SupportFragmentWrapper
import com.google.android.material.bottomsheet.BottomSheetDialog


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

    lateinit var textWaitTime:TextView
    lateinit var expectedTime:TextView
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
        val view:View =  inflater.inflate(R.layout.fragment_tab_1, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textWaitTime = view.findViewById(R.id.textWaitTime)
        expectedTime = view.findViewById(R.id.expectedTime)
        minText = view.findViewById(R.id.minText)
        textCurrentToken = view.findViewById(R.id.textCurrentToken)
        currentToken = view.findViewById(R.id.currentToken)
        textYourToken = view.findViewById(R.id.textYourToken)
        yourToken = view.findViewById(R.id.yourToken)
        leaveQueueButton = view.findViewById(R.id.leaveQueueButton)

        noWaitingImage = view.findViewById(R.id.noWaitingImage)
        noWaiting = view.findViewById(R.id.noWaiting)
        joinQueueButton = view.findViewById(R.id.joinQueueButton)

//        inQueueUpdateUI()
        noQueueUpdateUI()
        joinQueueButton.setOnClickListener {
            Log.d(TAG, "joinQueueButton clicked")
            val bottomSheet = joinQueueBottomSheet()
            bottomSheet.show(requireActivity().supportFragmentManager,"shubham")
        }
        leaveQueueButton.setOnClickListener {
            Log.d(TAG, "leaveQueueButton clicked")
        }
    }

    private fun noQueueUpdateUI() {
        Log.d(TAG, "noQueueUpdateUI() called")
        textWaitTime.visibility = View.GONE
        expectedTime.visibility = View.GONE
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
        expectedTime.visibility = View.VISIBLE
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