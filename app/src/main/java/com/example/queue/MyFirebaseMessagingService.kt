package com.example.queue

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseMessagingService :FirebaseMessagingService() {
    val TAG = "MyFirebaseMessagingServ"
    override fun onNewToken(p0: String) {
        Log.d(TAG, "onNewToken() called with: p0 = $p0")
    }
}