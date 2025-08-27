package com.example.cheekypoo

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.cheekypoo.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup drawer toggle (hamburger menu)
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.nav_open,
            R.string.nav_close
        )
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Setup Navigation Drawer menu listener
        binding.navigationView.setNavigationItemSelectedListener(this)

        // Setup Bottom Navigation with NavController
        val navController = findNavController(R.id.fragment)
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)

        // Access header views inside Navigation Drawer
        val headerView = binding.navigationView.getHeaderView(0)
        val headerImage = headerView.findViewById<ImageView>(R.id.imageView2)
        val headerTitle = headerView.findViewById<TextView>(R.id.headerTitle)

        headerImage.setOnClickListener {
            Toast.makeText(this, "Header Image Clicked", Toast.LENGTH_SHORT).show()
        }
        headerTitle.setOnClickListener {
            Toast.makeText(this, "Header Title Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle hamburger icon clicks (drawer toggle)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    // Handle drawer menu item clicks
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.favorite -> Toast.makeText(this, "Favorite", Toast.LENGTH_SHORT).show()
            R.id.rateus -> Toast.makeText(this, "Rate Us", Toast.LENGTH_SHORT).show()
            R.id.Developer -> Toast.makeText(this, "Developer", Toast.LENGTH_SHORT).show()
            R.id.shareApp -> Toast.makeText(this, "Share App", Toast.LENGTH_SHORT).show()
            R.id.termandcondition -> Toast.makeText(this, "Terms & Conditions", Toast.LENGTH_SHORT).show()
        }
        binding.drawerLayout.closeDrawers()
        return true
    }
}
