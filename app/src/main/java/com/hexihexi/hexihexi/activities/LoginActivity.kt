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
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private var database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        loginButton.setOnClickListener {
            userLogin()
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun userLogin() {
        val email: String = emailTxt.text.toString()
        val password: String = passwordTxt.text.toString()

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Enter email address!", Toast.LENGTH_SHORT).show();
            return
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter password!", Toast.LENGTH_SHORT).show();
            return
        }

        progressBar.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    progressBar.visibility = View.GONE

                    if (!it.isSuccessful) {
                        if (password.length < 6) {
                            Toast.makeText(this, "Password is too short!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Auth failed!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        FirebaseInstanceId.getInstance().token?.let { token ->
                            database.child("users").child(it.result.user.uid).child("token").setValue(token)
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    }
                }
    }
}
