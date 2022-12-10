package com.example.queue.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.queue.R
import org.json.JSONException
import org.json.JSONObject
import java.net.URL


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [feedback.newInstance] factory method to
 * create an instance of this fragment.
 */
class feedback : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        return inflater.inflate(R.layout.fragment_feedback, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val submitButton: Button = view.findViewById(R.id.feedback_submit)
        submitButton.setOnClickListener {
            val email = view.findViewById<EditText>(R.id.feedback_email).text.toString()
            if (isValidEmail(email)) {
                val name:String = view.findViewById<EditText>(R.id.feedback_name).text.toString()
                val phone:String = view.findViewById<EditText>(R.id.feedback_phone).text.toString()
                val message:String = view.findViewById<EditText>(R.id.feedback_message).text.toString()
                Log.d("TAG", name + email+phone+message);
                submitButton.visibility = View.GONE
                val contactFormtitle: TextView = view.findViewById(R.id.contact_form_title)
                contactFormtitle.visibility = View.GONE
                val contactFormSubmitted: TextView = view.findViewById(R.id.contact_form_Submitted)
                contactFormSubmitted.visibility = View.VISIBLE

                val queue = Volley.newRequestQueue(requireActivity())
                val url = URL("https://cute-erin-seal-gear.cyclic.app/email")
                val body:JSONObject = JSONObject()
                body.put("name",name)
                body.put("email",email)
                body.put("phone",phone)
                body.put("message",message)

                val req = JsonObjectRequest(url.toString(), body,
                    { response ->
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4))
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                ) { error -> VolleyLog.e("Error: ", error.message) }
                // Add the request to the RequestQueue.
                queue.add(req)

            } else {
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
                builder.setMessage("Please Enter a Valid Email Address")
                builder.setTitle("Invalid Email")
                // Set Cancelable false
                // for when the user clicks on the outside
                // the Dialog Box then it will remain show
                builder.setCancelable(true)
                //ok button to remove dialog
                builder
                    .setPositiveButton(
                        "ok"
                    ) { dialog, which ->
                        dialog.cancel()
                    }
                // Create the Alert dialog
                val alertDialog: AlertDialog = builder.create()
                // Show the Alert Dialog box
                alertDialog.show()
            }
        }
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target!!).matches()
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment feedback.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            feedback().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}