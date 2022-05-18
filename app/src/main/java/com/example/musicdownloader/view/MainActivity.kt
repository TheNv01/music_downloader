package com.example.musicdownloader.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.musicdownloader.R
import com.example.musicdownloader.databinding.ActivityMainBinding
import com.example.musicdownloader.view.fragment.*

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
     var playMusicFragment: PlayMusicFragment ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNavigation()
        mangerFragment()
    }

    private fun setupBottomNavigation(){
        binding.bottomView.itemIconTintList = null
        binding.bottomView.setupWithNavController(findNavController(R.id.activity_main_nav_host_fragment))
    }

    private fun mangerFragment(){

    }

}