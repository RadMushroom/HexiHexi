package com.hexihexi.hexihexi.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.hexihexi.hexihexi.R
import kotlinx.android.synthetic.main.activity_restore_pass.*

class RestorePassActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restore_pass)

        restoreButton.setOnClickListener {
            restorePassword()

        }
    }

    private fun restorePassword() {
        val email = emailTxt.text.toString()

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Enter your registered email id", Toast.LENGTH_SHORT).show();
            return
        }

        progressBar.visibility = View.VISIBLE
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to send reset email!", Toast.LENGTH_SHORT).show()
                    }

                    progressBar.visibility = View.GONE
                }
    }
}
