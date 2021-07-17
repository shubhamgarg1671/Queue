package com.example.queue

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
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
    lateinit var googleSignInButton:SignInButton
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
                Log.d(TAG, "Entered otp = $otp sstoredVerificationId = $storedVerificationId")
                val credential =
                    PhoneAuthProvider.getCredential(storedVerificationId!!, otp.toString())
                // do Stuff
                Log.d("onOtpCompleted=>", otp!!)
                signInWithPhoneAuthCredential(credential)
            }
        })

        // Initialize Firebase Aut0h
        auth = FirebaseAuth.getInstance()

        // callbacks for Phone uth vie Otp
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
                otpView.requestFocus();

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
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        //https://stackoverflow.com/a/63654043/12575211
        var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }
        }

        googleSignInButton = findViewById(R.id.googleSignInButton)
        googleSignInButton.setOnClickListener {
            Log.d(TAG, "googleSighIn button clicked")
            val signInIntent = googleSignInClient.getSignInIntent()
                //    startActivityForResult(signInIntent, RC_SIGN_IN);    //this has been depricated
                resultLauncher.launch(signInIntent)   //https://stackoverflow.com/a/63654043/12575211
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

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)

            Log.d(TAG, "signIn with account = $account")
            firebaseAuthWithGoogle(account?.idToken!!)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            googleFailedUpdateUI()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    afterLogIn(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    googleFailedUpdateUI()
                }
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

    fun afterLogIn(currentUser: FirebaseUser?) {
        Log.d(TAG, "afterLogIn() called with: currentUser = $currentUser")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun googleFailedUpdateUI () {
        Toast.makeText(applicationContext, "google SignIn not working", Toast.LENGTH_LONG).show()
        googleSignInButton.visibility = View.GONE
    }
}