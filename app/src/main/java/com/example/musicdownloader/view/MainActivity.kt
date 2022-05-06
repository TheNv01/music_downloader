package com.example.musicdownloader.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.musicdownloader.R
import com.example.musicdownloader.databinding.ActivityMainBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.view.fragment.*

class MainActivity : AppCompatActivity(), OnActionCallBack {

    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        initView()
    }

    private fun setupBottomNavigation(){
        binding.bottomView.itemIconTintList = null
        binding.bottomView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.page_home ->{
                    val homeFragment = HomeFragment(this)
                    showFragment(R.id.container_navigation, homeFragment, false, 0, 0)
                    true
                }
                R.id.page_download ->{
                    val downloadFragment = DownloadFragment(this)
                    showFragment(R.id.container_navigation, downloadFragment, false, 0, 0)
                    true
                }
                R.id.page_play_list ->{
                    val playlistFragment = PlayListFragment(this)
                    showFragment(R.id.container_navigation, playlistFragment, false, 0, 0)
                    true
                }
                R.id.page_setting ->{
                    val settingFragment = SettingFragment(this)
                    showFragment(R.id.container_navigation, settingFragment, false, 0, 0)
                    true
                }
                else -> false
            }

        }
    }

    private fun initView() {
        val homeFragment = HomeFragment(this)
        showFragment(R.id.container_navigation, homeFragment,false, 0, 0)
//        val playMusicFragment = PlayMusicFragment(this)
//        showFragment(playMusicFragment, false, 0, 0)
    }

    private fun showFragment(
        container: Int,
        fragment: Fragment,
        addToBackStack: Boolean,
        anim_start: Int,
        anim_end: Int) {
        if (!isFinishing) {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            if (anim_end != 0 && anim_start != 0) {
                transaction.setCustomAnimations(anim_start, anim_end)
            }
            transaction.replace(container, fragment)
            if (addToBackStack) {
                transaction.addToBackStack("add")
            }
            transaction.commit()
        }
    }

    override fun onBackPressed() {
        (supportFragmentManager.findFragmentById(R.id.container_layout_playing)).also {
            if (it == null) {
                super.onBackPressed()
                return
            }
            it as PlayMusicFragment
            if (it.binding.layoutPlayMusic.currentState == R.id.end)
                it.binding.layoutPlayMusic.transitionToStart()
            else
                super.onBackPressed()
        }
    }

    override fun callBack(key: String?, data: Any?) {
        when (key) {
            HomeFragment.KEY_SHOW_PLAY_MUSIC ->{
                (supportFragmentManager.findFragmentById(R.id.container_layout_playing)).also {
                    if (it == null) {
                        val playMusicFragment =  PlayMusicFragment(this)
                        showFragment(R.id.container_layout_playing, playMusicFragment, true, 0 , 0)
                    }
                }
            }
        }
    }

}