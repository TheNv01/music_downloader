package com.example.musicdownloader.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.musicdownloader.R
import com.example.musicdownloader.databinding.ActivityMainBinding
import com.example.musicdownloader.view.fragment.*
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var playMusicFragment: PlayMusicFragment ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNavigation()
    }

    private fun setupBottomNavigation(){
        binding.bottomView.itemIconTintList = null
        binding.bottomView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.homeFragment ->{
                    findNavController(R.id.activity_main_nav_host_fragment).navigate(R.id.homeFragment)
                    true
                }
                R.id.downloadFragment ->{
                    findNavController(R.id.activity_main_nav_host_fragment).navigate(R.id.downloadFragment)
                    true
                }
                R.id.playListFragment ->{
                    findNavController(R.id.activity_main_nav_host_fragment).navigate(R.id.playListFragment)
                    true
                }
                R.id.settingFragment ->{
                    findNavController(R.id.activity_main_nav_host_fragment).navigate(R.id.settingFragment)
                    true
                }
                else -> false
            }

        }
//        val navController: NavController = findNavController( R.id.activity_main_nav_host_fragment)
//        setupWithNavController(binding.bottomView, navController)
    }


}