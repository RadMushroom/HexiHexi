package com.hexihexi.hexihexi.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.hexihexi.hexihexi.R
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        registerButton.setOnClickListener {
            userRegister()
        }

        restorePassButton.setOnClickListener {
            startActivity(Intent(this, RestorePassActivity::class.java))
        }
    }

    private fun userRegister() {
        val email = emailTxt.text.toString().trim()
        val password = passwordTxt.text.toString().trim()

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(applicationContext, "Enter email address!", Toast.LENGTH_SHORT).show();
            return
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, "Enter password!", Toast.LENGTH_SHORT).show();
            return
        }

        if (password.length < 6) {
            Toast.makeText(applicationContext, "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return
        }

        progressBar.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    Toast.makeText(this, "createUserWithEmail:onComplete: ${it.isSuccessful}", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    if (!it.isSuccessful) {
                        Toast.makeText(this, "Authentication failed!", Toast.LENGTH_SHORT).show()
                    } else {
                        auth.currentUser?.let { currUser ->
                            database.child("users").child(currUser.uid).child("email").setValue(currUser.email)
                           FirebaseInstanceId.getInstance().token?.let{
                               database.child("users").child(currUser.uid).child("token").setValue(it)
                           }

                        } ?: run {
                            Toast.makeText(this, "Current user error!!!", Toast.LENGTH_SHORT).show()
                        }
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
    }

    override fun onResume() {
        super.onResume()
        progressBar.visibility = View.GONE
    }
}
