package com.example.cheekypoo.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cheekypoo.MainActivity
import com.example.cheekypoo.databinding.ActivityUserProfileDetailsBinding
import com.example.cheekypoo.model.UserModel
import com.example.cheekypoo.utils.Config
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UserProfileDetails : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileDetailsBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveData.setOnClickListener { validateAndSave() }
    }

    private fun validateAndSave() {
        // ─── REMOVE IMAGE CHECK ───
        when {
            binding.userName.text.isNullOrEmpty() -> { binding.userName.error = "Required"; return }
            binding.userNumber.text.isNullOrEmpty() -> { binding.userNumber.error = "Required"; return }
            binding.userEmail.text.isNullOrEmpty() -> { binding.userEmail.error = "Required"; return }
            binding.userAge.text.isNullOrEmpty() -> { binding.userAge.error = "Required"; return }
            binding.userCity.text.isNullOrEmpty() -> { binding.userCity.error = "Required"; return }
            binding.genderGroup.checkedRadioButtonId == -1 -> {
                Toast.makeText(this, "Select Gender", Toast.LENGTH_SHORT).show(); return
            }
            !binding.termsCondition.isChecked -> {
                Toast.makeText(this, "Accept Terms", Toast.LENGTH_SHORT).show(); return
            }
        }

        saveDirectly()   // ← NEW: skip upload
    }

    private fun saveDirectly() {
        val uid = auth.currentUser?.uid ?: return
        Config.showDialog(this)

        val interests = mutableListOf<String>()
        for (i in 0 until binding.interestsGroup.childCount) {
            val chip = binding.interestsGroup.getChildAt(i) as? Chip
            if (chip?.isChecked == true) interests.add(chip.text.toString())
        }

        val gender = when (binding.genderGroup.checkedRadioButtonId) {
            binding.manOption.id -> "Male"
            binding.womanOption.id -> "Female"
            binding.otherOption.id -> "Other"
            else -> ""
        }

        val user = UserModel(
            Number = binding.userNumber.text.toString().trim(),
            Name = binding.userName.text.toString().trim(),
            Email = binding.userEmail.text.toString().trim(),
            Age = binding.userAge.text.toString().trim(),
            Gender = gender,
            Country = binding.userCountry.text.toString().trim(),
            City = binding.userCity.text.toString().trim(),
            State = binding.userState.text.toString().trim(),
            College = binding.userCollege.text.toString().trim(),
            Branch = binding.userBranch.text.toString().trim(),
            Relationship = binding.userRelationship.text.toString().trim(),
            Zodiac = binding.userZodiac.text.toString().trim(),
            Status = binding.userStatus.text.toString().trim(),
            Image = "",               // ← empty image
            Interests = interests
        )

        db.getReference("Users").child(uid).setValue(user)
            .addOnSuccessListener {
                Config.hideDialog()
                Toast.makeText(this, "Profile Saved! 🎉", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Config.hideDialog()
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }
}