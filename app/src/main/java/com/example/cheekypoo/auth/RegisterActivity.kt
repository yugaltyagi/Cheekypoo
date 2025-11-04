//package com.example.cheekypoo.auth
//
//import android.app.DatePickerDialog
//import android.content.Intent
//import android.os.Bundle
//import android.widget.*
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import com.example.cheekypoo.R
//import com.google.firebase.auth.FirebaseAuth
//import java.util.*
//
//class RegisterActivity : AppCompatActivity() {
//
//    private lateinit var auth: FirebaseAuth
//
//    private lateinit var nameEt: EditText
//    private lateinit var dobEt: EditText
//    private lateinit var emailEt: EditText
//    private lateinit var passwordEt: EditText
//    private lateinit var loginTv: TextView
//    private lateinit var btnRegister: Button
//
//    private var userAge: Int = 0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_register)
//
//        auth = FirebaseAuth.getInstance()
//
//        nameEt = findViewById(R.id.etFullName)
//        dobEt = findViewById(R.id.etDateOfBirth)
//        emailEt = findViewById(R.id.etEmail)
//        passwordEt = findViewById(R.id.etPassword)
//        loginTv = findViewById(R.id.tvLogin)
//        btnRegister = findViewById(R.id.btnRegister)
//
//        // Navigate to Login
//        loginTv.setOnClickListener {
//            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
//        }
//
//        // Date picker for DOB
//        dobEt.setOnClickListener { showDatePicker() }
//
//        // Register button click
//        btnRegister.setOnClickListener { registerUser() }
//    }
//
//    private fun showDatePicker() {
//        val calendar = Calendar.getInstance()
//        val year = calendar.get(Calendar.YEAR)
//        val month = calendar.get(Calendar.MONTH)
//        val day = calendar.get(Calendar.DAY_OF_MONTH)
//
//        val datePicker = DatePickerDialog(
//            this,
//            { _, selectedYear, selectedMonth, selectedDay ->
//                val dob = "$selectedDay/${selectedMonth + 1}/$selectedYear"
//                dobEt.setText(dob)
//
//                // ✅ Calculate age
//                userAge = calculateAge(selectedYear, selectedMonth, selectedDay)
//            },
//            year, month, day
//        )
//        datePicker.show()
//    }
//
//    private fun calculateAge(year: Int, month: Int, day: Int): Int {
//        val today = Calendar.getInstance()
//        val birthDate = Calendar.getInstance()
//        birthDate.set(year, month, day)
//
//        var age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)
//
//        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
//            age--
//        }
//        return age
//    }
//
//    private fun registerUser() {
//        val name = nameEt.text.toString().trim()
//        val dob = dobEt.text.toString().trim()
//        val email = emailEt.text.toString().trim()
//        val password = passwordEt.text.toString().trim()
//
//        // Validation
//        if (name.isEmpty() || dob.isEmpty() || email.isEmpty() || password.isEmpty()) {
//            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if (userAge < 18) {
//            Toast.makeText(this, "You must be at least 18 years old to register", Toast.LENGTH_LONG).show()
//            return
//        }
//
//        if (password.length < 6) {
//            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        // ✅ Firebase Authentication only
//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val user = auth.currentUser
//                    if (user != null) {
//                        user.sendEmailVerification()
//                            .addOnSuccessListener {
//                                Toast.makeText(
//                                    this,
//                                    "Registered successfully! Please check your email for verification.",
//                                    Toast.LENGTH_LONG
//                                ).show()
//                                auth.signOut()
//                                startActivity(Intent(this, LoginActivity::class.java))
//                                finish()
//                            }
//                            .addOnFailureListener {
//                                Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
//                            }
//                    }
//                } else {
//                    Toast.makeText(
//                        this,
//                        "Registration failed: ${task.exception?.message}",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//            }
//    }
//}



// File: app/src/main/java/com/example/cheekypoo/auth/RegisterActivity.kt
package com.example.cheekypoo.auth

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.cheekypoo.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private var userAge: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Go to Login
        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Date Picker
        binding.etDateOfBirth.setOnClickListener { showDatePicker() }

        // Register Button
        binding.btnRegister.setOnClickListener { validateAndRegister() }
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, y, m, d ->
            val dob = "$d/${m + 1}/$y"
            binding.etDateOfBirth.setText(dob)
            userAge = calculateAge(y, m, d)
        }, year, month, day).apply {
            datePicker.maxDate = System.currentTimeMillis()
        }.show()
    }

    private fun calculateAge(year: Int, month: Int, day: Int): Int {
        val today = Calendar.getInstance()
        val birth = Calendar.getInstance().apply { set(year, month, day) }
        var age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) age--
        return age
    }

    private fun validateAndRegister() {
        val name = binding.etFullName.text.toString().trim()
        val dob = binding.etDateOfBirth.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        when {
            name.isEmpty() -> {
                binding.etFullName.error = "Required"
                binding.etFullName.requestFocus()
                return
            }
            dob.isEmpty() -> {
                binding.etDateOfBirth.error = "Required"
                return
            }
            email.isEmpty() -> {
                binding.etEmail.error = "Required"
                return
            }
            password.isEmpty() -> {
                binding.etPassword.error = "Required"
                return
            }
            userAge < 18 -> {
                Toast.makeText(this, "You must be 18+ to register", Toast.LENGTH_LONG).show()
                return
            }
            password.length < 6 -> {
                binding.etPassword.error = "Min 6 characters"
                return
            }
        }

        registerUser(email, password)
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser!!

                    user.sendEmailVerification()
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Verification email sent! Please check your inbox.",
                                Toast.LENGTH_LONG
                            ).show()

                            // Sign out & go to Login
                            auth.signOut()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to send email: ${it.message}", Toast.LENGTH_LONG).show()
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