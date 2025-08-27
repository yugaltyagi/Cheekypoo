package com.example.cheekypoo.auth

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.cheekypoo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class RegisterActivity : AppCompatActivity() {

    // Firebase instances
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // UI Elements
    private lateinit var loginTv: TextView
    private lateinit var nameEt: EditText
    private lateinit var dobEt: EditText
    private lateinit var emailEt: EditText
    private lateinit var passwordEt: EditText
    private lateinit var btnMale: Button
    private lateinit var btnFemale: Button
    private lateinit var btnRegister: Button

    private var selectedGender: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        loginTv = findViewById(R.id.tvLogin)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Bind Views
        nameEt = findViewById(R.id.etFullName)
        dobEt = findViewById(R.id.etDateOfBirth)
        emailEt = findViewById(R.id.etEmail)
        passwordEt = findViewById(R.id.etPassword)
        btnMale = findViewById(R.id.btnMale)
        btnFemale = findViewById(R.id.btnFemale)
        btnRegister = findViewById(R.id.btnRegister)


//Register se TV par jane k liye if you already have account
        loginTv.setOnClickListener{
           val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent);
        }

        // Gender selection
        btnMale.setOnClickListener {
            selectedGender = "Male"
            btnMale.isSelected = true
            btnFemale.isSelected = false
        }

        btnFemale.setOnClickListener {
            selectedGender = "Female"
            btnFemale.isSelected = true
            btnMale.isSelected = false
        }

        // Date picker for DOB
        dobEt.setOnClickListener {
            showDatePicker()
        }

        // Register button click
        btnRegister.setOnClickListener {
            registerUser()
        }
    }


    /**
     * Show Date Picker Dialog for selecting DOB
     */
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val dob = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dobEt.setText(dob)
            },
            year, month, day
        )
        datePicker.show()
    }

    /**
     * Register user with Firebase Auth and Firestore
     */
    private fun registerUser() {
        val name = nameEt.text.toString().trim()
        val dob = dobEt.text.toString().trim()
        val email = emailEt.text.toString().trim()
        val password = passwordEt.text.toString().trim()

        // Validation
        if (name.isEmpty() || dob.isEmpty() || email.isEmpty() || password.isEmpty() || selectedGender.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        // Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid

                    // Save additional user details in Firestore
                    val userMap = hashMapOf(
                        "name" to name,
                        "dob" to dob,
                        "gender" to selectedGender,
                        "email" to email
                    )

                    if (userId != null) {
                        db.collection("users").document(userId)
                            .set(userMap)
                            .addOnSuccessListener {
                                // Send email verification
                                user.sendEmailVerification()
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this,
                                            "Registered successfully! Please check your email for verification.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to save user data.", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}
