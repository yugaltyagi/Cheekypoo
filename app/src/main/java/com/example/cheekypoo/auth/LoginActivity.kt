//package com.example.cheekypoo.auth
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.*
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import com.example.cheekypoo.MainActivity
//import com.example.cheekypoo.R
//import com.example.cheekypoo.activity.UserProfileDetails
//import com.google.firebase.auth.FirebaseAuth
//
//class LoginActivity : AppCompatActivity() {
//
//    private lateinit var emailEt: EditText
//    private lateinit var passwordEt: EditText
//    private lateinit var forgotPassword: TextView
//    private lateinit var btnLogin: Button
//    private lateinit var mRegister: TextView
//    private lateinit var auth: FirebaseAuth
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_login)
//
//        auth = FirebaseAuth.getInstance()
//
//        emailEt = findViewById(R.id.etEmail)
//        passwordEt = findViewById(R.id.etPassword)
//        forgotPassword = findViewById(R.id.tvForgotPassword)
//        btnLogin = findViewById(R.id.btnLogin)
//        mRegister = findViewById(R.id.tvSignUp)
//
//        // Go to RegisterActivity
//        mRegister.setOnClickListener {
//            startActivity(Intent(this, RegisterActivity::class.java))
//        }
//
//        // Forgot password flow
//        forgotPassword.setOnClickListener {
//            val email = emailEt.text.toString().trim()
//            if (email.isEmpty()) {
//                Toast.makeText(this, "Enter your email to reset password", Toast.LENGTH_SHORT).show()
//            } else {
//                auth.sendPasswordResetEmail(email)
//                    .addOnSuccessListener {
//                        Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_LONG).show()
//                    }
//                    .addOnFailureListener {
//                        Toast.makeText(this, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
//                    }
//            }
//        }
//
//        // Login button
//        btnLogin.setOnClickListener {
//            val email = emailEt.text.toString().trim()
//            val password = passwordEt.text.toString().trim()
//
//            if (email.isEmpty() || password.isEmpty()) {
//                Toast.makeText(this, "Please enter Email and Password", Toast.LENGTH_SHORT).show()
//            } else {
//                loginUser(email, password)
//            }
//        }
//    }
//
//    private fun loginUser(email: String, password: String) {
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    val user = auth.currentUser
//                    if (user != null) {
//                        if (user.isEmailVerified) {
//                            val prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
//                            val isNewUser = prefs.getBoolean("isNewUser", false)
//
//                            if (isNewUser) {
//                                // ✅ First-time login → UserProfileDetails
//                                prefs.edit().putBoolean("isNewUser", false).apply()
//                                startActivity(Intent(this, UserProfileDetails::class.java))
//                            } else {
//                                // ✅ Old user → MainActivity
//                                startActivity(Intent(this, MainActivity::class.java))
//                            }
//                            finish()
//                        } else {
//                            Toast.makeText(
//                                this,
//                                "Please verify your email before logging in.",
//                                Toast.LENGTH_LONG
//                            ).show()
//                            auth.signOut()
//                        }
//                    }
//                } else {
//                    Toast.makeText(
//                        this,
//                        "Login failed: ${task.exception?.message}",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//            }
//    }
//}



// LoginActivity.kt
package com.example.cheekypoo.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cheekypoo.MainActivity
import com.example.cheekypoo.activity.UserProfileDetails
import com.example.cheekypoo.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Auto-login if already signed in
        if (auth.currentUser != null) {
            checkProfileAndRedirect(auth.currentUser!!.uid)
            finish()
            return
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvForgotPassword.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(this, "Reset link sent!", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser!!
                    if (!user.isEmailVerified) {
                        Toast.makeText(this, "Please verify your email first!", Toast.LENGTH_LONG).show()
                        auth.signOut()
                        return@addOnCompleteListener
                    }

                    checkProfileAndRedirect(user.uid)
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun checkProfileAndRedirect(uid: String) {
        val userRef = database.getReference("Users").child(uid)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Old user → Go to Main
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                } else {
                    // New user → Complete profile
                    startActivity(Intent(this@LoginActivity, UserProfileDetails::class.java))
                }
                finish()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LoginActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}