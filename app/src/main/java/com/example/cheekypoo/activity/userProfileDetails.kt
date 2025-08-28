//package com.example.cheekypoo.activity
//
//import android.net.Uri
//import android.os.Bundle
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import com.example.cheekypoo.databinding.ActivityUserProfileDetailsBinding
//import com.example.cheekypoo.model.UserModel
//import com.example.cheekypoo.utils.Config
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.storage.FirebaseStorage
//import com.google.android.material.chip.Chip
//
//class UserProfileDetails : AppCompatActivity() {
//
//    private lateinit var binding: ActivityUserProfileDetailsBinding
//    private var imageUri: Uri? = null
//
//    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//        imageUri = uri
//        binding.userImage.setImageURI(imageUri)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityUserProfileDetailsBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Image Picker
//        binding.userImage.setOnClickListener { selectImage.launch("image/*") }
//        binding.changePhotoFab.setOnClickListener { selectImage.launch("image/*") }
//
//        // Save Data
//        binding.saveData.setOnClickListener { validateData() }
//    }
//
//    private fun validateData() {
//        when {
//            binding.userNumber.text.isNullOrEmpty() ||
//                    binding.userName.text.isNullOrEmpty() ||
//                    binding.userEmail.text.isNullOrEmpty() ||
//                    binding.userAge.text.isNullOrEmpty() ||
//                    binding.userCity.text.isNullOrEmpty() ||
//                    !binding.termsCondition.isChecked ||
//                    imageUri == null -> {
//                Toast.makeText(this, "Please fill all fields & accept terms", Toast.LENGTH_SHORT).show()
//            }
//            else -> uploadImage()
//        }
//    }
//
//    private fun uploadImage() {
//        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
//        val storageRef = FirebaseStorage.getInstance().reference
//            .child("Profile").child(uid).child("Profile.jpg")
//
//        Config.showDialog(this)
//
//        storageRef.putFile(imageUri!!)
//            .addOnSuccessListener {
//                storageRef.downloadUrl.addOnSuccessListener { uri ->
//                    storeData(uri.toString())
//                }
//            }
//            .addOnFailureListener {
//                Config.hideDialog()
//                Toast.makeText(this, "Image upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun storeData(imageUrl: String) {
//        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
//
//        // Collect selected interests from ChipGroup
//        val selectedInterests = mutableListOf<String>()
//        for (i in 0 until binding.interestsGroup.childCount) {
//            val chip = binding.interestsGroup.getChildAt(i) as Chip
//            if (chip.isChecked) {
//                selectedInterests.add(chip.text.toString())
//            }
//        }
//
//        // Detect gender
//        val gender = when (binding.genderGroup.checkedRadioButtonId) {
//            binding.manOption.id -> "Male"
//            binding.womanOption.id -> "Female"
//            binding.otherOption.id -> "Other"
//            else -> ""
//        }
//
//        val data = UserModel(
//            Number = binding.userNumber.text.toString(),
//            Name = binding.userName.text.toString(),
//            Email = binding.userEmail.text.toString(),
//            Age = binding.userAge.text.toString(),
//            Gender = gender,
//            Country = binding.userCountry.text.toString(),
//            City = binding.userCity.text.toString(),
//            State = binding.userState.text.toString(),
//            College = binding.userCollege.text.toString(),
//            Branch = binding.userBranch.text.toString(),
//            Relationship = binding.userRelationship.text.toString(),
//            Zodiac = binding.userZodiac.text.toString(),
//            Status = binding.userStatus.text.toString(),
//            Image = imageUrl,
//            Interests = selectedInterests
//        )
//
//        FirebaseDatabase.getInstance().getReference("Users")
//            .child(uid)
//            .setValue(data)
//            .addOnCompleteListener {
//                Config.hideDialog()
//                if (it.isSuccessful) {
//                    Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(this, "Error: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }
//}



package com.example.cheekypoo.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.cheekypoo.MainActivity
import com.example.cheekypoo.databinding.ActivityUserProfileDetailsBinding
import com.example.cheekypoo.model.UserModel
import com.example.cheekypoo.utils.Config
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class UserProfileDetails : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileDetailsBinding
    private var imageUri: Uri? = null

    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
        binding.userImage.setImageURI(imageUri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Image Picker
        binding.userImage.setOnClickListener { selectImage.launch("image/*") }
        binding.changePhotoFab.setOnClickListener { selectImage.launch("image/*") }

        // Save Data
        binding.saveData.setOnClickListener { validateData() }
    }

    private fun validateData() {
        when {
            binding.userNumber.text.isNullOrEmpty() ||
                    binding.userName.text.isNullOrEmpty() ||
                    binding.userEmail.text.isNullOrEmpty() ||
                    binding.userAge.text.isNullOrEmpty() ||
                    binding.userCity.text.isNullOrEmpty() ||
                    !binding.termsCondition.isChecked ||
                    imageUri == null -> {
                Toast.makeText(this, "Please fill all fields & accept terms", Toast.LENGTH_SHORT).show()
            }
            binding.genderGroup.checkedRadioButtonId == -1 -> {
                Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show()
            }
            else -> uploadImage()
        }
    }

    private fun uploadImage() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storageRef = FirebaseStorage.getInstance().reference
            .child("Profile").child(uid).child("Profile.jpg")

        Config.showDialog(this)

        imageUri?.let { uri ->
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        storeData(downloadUri.toString())
                    }
                }
                .addOnFailureListener {
                    Config.hideDialog()
                    Toast.makeText(this, "Image upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun storeData(imageUrl: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Collect selected interests from ChipGroup safely
        val selectedInterests = mutableListOf<String>()
        for (i in 0 until binding.interestsGroup.childCount) {
            val chip = binding.interestsGroup.getChildAt(i)
            if (chip is Chip && chip.isChecked) {
                selectedInterests.add(chip.text.toString())
            }
        }

        // Detect gender
        val gender = when (binding.genderGroup.checkedRadioButtonId) {
            binding.manOption.id -> "Male"
            binding.womanOption.id -> "Female"
            binding.otherOption.id -> "Other"
            else -> ""
        }

        val data = UserModel(
            Number = binding.userNumber.text.toString(),
            Name = binding.userName.text.toString(),
            Email = binding.userEmail.text.toString(),
            Age = binding.userAge.text.toString(),
            Gender = gender,
            Country = binding.userCountry.text.toString(),
            City = binding.userCity.text.toString(),
            State = binding.userState.text.toString(),
            College = binding.userCollege.text.toString(),
            Branch = binding.userBranch.text.toString(),
            Relationship = binding.userRelationship.text.toString(),
            Zodiac = binding.userZodiac.text.toString(),
            Status = binding.userStatus.text.toString(),
            Image = imageUrl,
            Interests = selectedInterests
        )

        FirebaseDatabase.getInstance().getReference("Users")
            .child(uid)
            .setValue(data)
            .addOnCompleteListener {
                Config.hideDialog()
                if (it.isSuccessful) {
                    Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show()
                    // ✅ Redirect to MainActivity after saving
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
