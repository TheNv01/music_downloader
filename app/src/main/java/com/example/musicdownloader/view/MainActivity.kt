package com.example.musicdownloader.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.musicdownloader.R
import com.example.musicdownloader.SharedPreferencesManager
import com.example.musicdownloader.databinding.ActivityMainBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.manager.MediaManager
import com.example.musicdownloader.view.fragment.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.proxglobal.rate.ProxRateDialog
import com.proxglobal.rate.RatingDialogListener

class MainActivity : AppCompatActivity(), OnActionCallBack {

    lateinit var binding: ActivityMainBinding
    var playMusicFragment: PlayMusicFragment ?= null
    private lateinit var navController: NavController

    lateinit var firebaseAnalytics: FirebaseAnalytics

    lateinit var callBack: OnActionCallBack


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNavigation()
        initRateDialog()
        pushRateNotFirstRun()
        if(MediaManager.mediaPlayer!!.isPlaying || MediaManager.isPause){
            callBack = this
            callBack.callBack(null, null)
        }
        firebaseAnalytics = Firebase.analytics

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

    private fun pushRateNotFirstRun(){
        if (SharedPreferencesManager.get<Boolean>("firstrun") == null) {

            Log.d("TheNv", "first time")
            SharedPreferencesManager.put( false, "firstrun")
        }
        else{
            ProxRateDialog.showIfNeed(this, supportFragmentManager)
        }
    }

    private fun initRateDialog(){

        val ratingListener = object : RatingDialogListener(){
            override fun onChangeStar(rate: Int) {

            }

            override fun onSubmitButtonClicked(rate: Int, comment: String?) {
                    firebaseAnalytics.logEvent("prox_rating_layout") {
                        param(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
                    }
                    firebaseAnalytics.logEvent("prox_rating_layout") {
                        param("event_type", "rated")
                        param("star", "$rate star")
                        param("comment", comment.toString())
                    }
            }

            override fun onLaterButtonClicked() {

            }

            override fun onDone() {
                //this method will call after dismiss tks dialog
            }
        }

        ProxRateDialog.init(R.layout.dialog_rating, ratingListener)

    }

    override fun callBack(key: String?, data: Any?) {
        val tran = supportFragmentManager.beginTransaction()
        playMusicFragment = PlayMusicFragment()
        tran.replace(R.id.container_layout_playing, playMusicFragment!!)
        tran.addToBackStack("playFragment")
        tran.commit()
    }
}