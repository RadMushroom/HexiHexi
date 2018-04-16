package com.hexihexi.hexihexi.notifications


import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = FirebaseDatabase.getInstance().reference
    private val TAG = "MyFirebaseIIDService"

    override fun onTokenRefresh() {
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Refreshed token: " + refreshedToken!!)
        auth.currentUser?.let { currUser ->
            FirebaseInstanceId.getInstance().token?.let{
                database.child("users").child(currUser.uid).child("token").setValue(it)
            }

        }
    }
}
