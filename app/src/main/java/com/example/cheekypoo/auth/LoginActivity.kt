package com.example.cheekypoo.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.cheekypoo.MainActivity
import com.example.cheekypoo.R
import com.example.cheekypoo.activity.userProfileDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEt: EditText
    private lateinit var passwordEt: EditText
    private lateinit var forgotPassword: TextView
    private lateinit var btnLogin: Button
    private lateinit var mRegister: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        emailEt = findViewById(R.id.etEmail)
        passwordEt = findViewById(R.id.etPassword)
        forgotPassword = findViewById(R.id.tvForgotPassword)
        btnLogin = findViewById(R.id.btnLogin)
        mRegister = findViewById(R.id.tvSignUp)

        // Go to RegisterActivity
        mRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Login button
        btnLogin.setOnClickListener {
            val email = emailEt.text.toString().trim()
            val password = passwordEt.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter Email and Password", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser!!.uid
                    db.collection("users").document(userId).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                // ✅ Old user → MainActivity
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            } else {
                                // ✅ New user → UserDetailActivity
                                startActivity(Intent(this, userProfileDetails::class.java))
                                finish()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error checking user profile", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(
                        this,
                        "Login failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}
