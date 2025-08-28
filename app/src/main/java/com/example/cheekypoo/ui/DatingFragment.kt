package com.example.cheekypoo.ui

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import com.example.cheekypoo.adapter.DatingAdapter
import com.example.cheekypoo.databinding.FragmentDatingBinding
import com.example.cheekypoo.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction

class DatingFragment : Fragment() {

    private var _binding: FragmentDatingBinding? = null
    private val binding get() = _binding!!

    private lateinit var manager: CardStackLayoutManager
    private lateinit var adapter: DatingAdapter
    private var userList: ArrayList<UserModel> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDatingBinding.inflate(inflater, container, false)

        setupCardStack()
        fetchUsers()

        return binding.root
    }

    /**
     * Setup CardStack Layout Manager + Adapter
     */
    private fun setupCardStack() {
        manager = CardStackLayoutManager(requireContext(), object : CardStackListener {
            override fun onCardDragging(direction: Direction?, ratio: Float) {}

            override fun onCardSwiped(direction: Direction?) {
                if (manager.topPosition == userList.size) {
                    Toast.makeText(requireContext(), "This is the last card!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCardRewound() {}
            override fun onCardCanceled() {}
            override fun onCardAppeared(view: View?, position: Int) {}
            override fun onCardDisappeared(view: View?, position: Int) {}
        })

        // Card stack config (Tinder style)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)

        binding.cardStackView.layoutManager = manager
        binding.cardStackView.itemAnimator = DefaultItemAnimator()

        // Empty adapter initially
        adapter = DatingAdapter(requireContext(), userList)
        binding.cardStackView.adapter = adapter
    }

    /**
     * Fetch user data from Firebase Realtime Database
     */
    private fun fetchUsers() {
        val database = FirebaseDatabase.getInstance()
        database.getReference("users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d(TAG, "onDataChange: $snapshot")
                    if (snapshot.exists()) {
                        userList.clear()
                        for (data in snapshot.children) {
                            val model = data.getValue(UserModel::class.java)
                            model?.let { userList.add(it) }
                        }
                        userList.shuffle()

                        // Update adapter with new data
                        adapter = DatingAdapter(requireContext(), userList)
                        binding.cardStackView.adapter = adapter

                    } else {
                        Toast.makeText(requireContext(), "No users found!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        requireContext(),
                        "Data fetch failed: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
