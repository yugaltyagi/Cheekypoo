package com.example.cheekypoo.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.cheekypoo.R
import com.example.cheekypoo.databinding.FragmentProfileBinding
import com.example.cheekypoo.model.UserModel
import com.example.cheekypoo.utils.Config
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Loading dialog show kar diye (agar tumne Config.showDialog implement kiya hai)
        Config.showDialog(requireContext())

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Current logged in user aur uska phone number le rahe hain
        val currentUser = FirebaseAuth.getInstance().currentUser
        val phoneKey = currentUser?.phoneNumber

        if (phoneKey.isNullOrEmpty()) {
            // Agar user logged in nahi hai ya phone number available nahi hai
            Log.w(TAG, "No current user or phone number is null/empty")
            Config.hideDialog() // dialog ko close kar do
            return binding.root
        }

        val usersRef = FirebaseDatabase.getInstance().getReference("users")
        usersRef.child(phoneKey).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    // Firebase se data mil gaya — UserModel me map kar rahe hain
                    val data = snapshot.getValue(UserModel::class.java)

                    // Safely values set kar rahe hain (agar null ho to empty string set karega)
                    binding.name.setText(data?.Name ?: "")
                    binding.email.setText(data?.Email ?: "")
                    binding.number.setText(data?.Number ?: "")
                    binding.Gender.setText(data?.Gender ?: "")
                    binding.city.setText(data?.City ?: "")

                    // Image safe load: agar Image null ho to placeholder dikhayega
                    val imageUrl = data?.Image ?: ""
                    Glide.with(requireContext())
                        .load(if (imageUrl.isNotEmpty()) imageUrl else null)
                        .placeholder(R.drawable.ic_person)
                        .into(binding.userImage)
                } else {
                    // Agar users node exist nahi karta for this phoneKey
                    Log.i(TAG, "User node does not exist for key: $phoneKey")
                }
                // Data load ho chuka — dialog hide kar do
                Config.hideDialog()
            }
            .addOnFailureListener { ex ->
                // Agar read me failure aaya to log kar do aur dialog hide kar do
                Log.e(TAG, "Failed to read user data", ex)
                Config.hideDialog()
            }

        // Agar tum fields ko read-only (non-editable) banana chahte ho to ye uncomment kar sakte ho:
        // binding.name.isFocusable = false
        // binding.name.isClickable = false
        // binding.name.isCursorVisible = false
        // ...same for baki fields

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "ProfileFragment"
    }
}
