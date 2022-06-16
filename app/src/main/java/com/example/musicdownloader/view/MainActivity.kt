package com.example.musicdownloader.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.musicdownloader.R
import com.example.musicdownloader.databinding.ActivityMainBinding
import com.example.musicdownloader.view.fragment.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var playMusicFragment: PlayMusicFragment ?= null
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNavigation()
    }

    private fun setupBottomNavigation(){
        binding.bottomView.itemIconTintList = null


        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.activity_main_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Setup the bottom navigation view with navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_view)
        bottomNavigationView.setupWithNavController(navController)

    }


}