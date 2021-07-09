package com.example.queue

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.mukesh.OnOtpCompletionListener
import com.mukesh.OtpView
import java.util.concurrent.TimeUnit

class signIn : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var otpView: OtpView
    lateinit var button1SigninPage:ImageButton

    lateinit var signInProgress:ProgressBar
    val TAG = "SignInActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        var storedVerificationId = ""
        var resendToken: PhoneAuthProvider.ForceResendingToken
        otpView = findViewById(R.id.otp_view)
        button1SigninPage = findViewById(R.id.button1SigninPage)
        signInProgress = findViewById(R.id.signInProgress)
        otpView.setOtpCompletionListener(object : OnOtpCompletionListener {
            override fun onOtpCompleted(otp: String?) {
                val credential =
                    PhoneAuthProvider.getCredential(storedVerificationId!!, otp.toString())
                // do Stuff
                Log.d("onOtpCompleted=>", otp!!)
                signInWithPhoneAuthCredential(credential)
            }
        })

        // Initialize Firebase Aut0h
        auth = FirebaseAuth.getInstance()

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)
                button1SigninPage.visibility = View.VISIBLE

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(applicationContext, "Incorrect Phone Number", Toast.LENGTH_LONG).show()

                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(applicationContext, "Server Error", Toast.LENGTH_LONG).show()
                }

                // Show a message and update the UI
                button1SigninPage.visibility = View.VISIBLE


            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")

                otpView.visibility = View.VISIBLE
                signInProgress.visibility = View.GONE

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
            }
        }
        button1SigninPage.setOnClickListener {
            button1SigninPage.visibility = View.GONE
            signInProgress.visibility = View.VISIBLE
            val country_code = findViewById<EditText>(R.id.country_code)
            val phoneNumber = findViewById<EditText>(R.id.phone)
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(country_code.text.toString() + phoneNumber.text.toString())       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Toast.makeText(applicationContext, "user allready logged in", Toast.LENGTH_LONG).show()
            afterLogIn(currentUser)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user
                    afterLogIn(user!!)
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(applicationContext, "Invalid OTP", Toast.LENGTH_LONG).show()
                        otpView.visibility = View.GONE
                        signInProgress.visibility = View.GONE
                        button1SigninPage.visibility = View.VISIBLE
                    }
                    // Update UI
                }
            }
    }

    fun afterLogIn(currentUser: FirebaseUser) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}