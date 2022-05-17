package com.example.musicdownloader.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.musicdownloader.MusicService
import com.example.musicdownloader.R
import com.example.musicdownloader.databinding.ActivityMainBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.view.fragment.*

class MainActivity : AppCompatActivity(), OnActionCallBack {

    lateinit var binding: ActivityMainBinding
    private var playMusicFragment: PlayMusicFragment ?= null


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
                    showFragment(R.id.container_navigation, homeFragment,
                        isAdd = false,
                        addToBackStack = false,
                        anim_start = 0,
                        anim_end = 0
                    )
                    true
                }
                R.id.page_download ->{
                    val downloadFragment = DownloadFragment(this)
                    showFragment(R.id.container_navigation, downloadFragment,
                        isAdd = false,
                        addToBackStack = false,
                        anim_start = 0,
                        anim_end = 0
                    )
                    true
                }
                R.id.page_play_list ->{
                    val playlistFragment = PlayListFragment(this)
                    showFragment(R.id.container_navigation, playlistFragment,
                        isAdd = false,
                        addToBackStack = false,
                        anim_start = 0,
                        anim_end = 0
                    )
                    true
                }
                R.id.page_setting ->{
                    val settingFragment = SettingFragment(this)
                    showFragment(R.id.container_navigation, settingFragment,
                        isAdd = false,
                        addToBackStack = false,
                        anim_start = 0,
                        anim_end = 0
                    )
                    true
                }
                else -> false
            }

        }
    }

    private fun initView() {
        val homeFragment = HomeFragment(this)
        showFragment(R.id.container_navigation, homeFragment,
            isAdd = false,
            addToBackStack = false,
            anim_start = 0,
            anim_end = 0
        )
    }

    private fun showFragment(

        container: Int,
        fragment: Fragment,
        isAdd: Boolean,
        addToBackStack: Boolean,
        anim_start: Int,
        anim_end: Int) {
        if (!isFinishing) {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            if (anim_end != 0 && anim_start != 0) {
                transaction.setCustomAnimations(anim_start, anim_end)
            }
            if(isAdd){
                transaction.add(container, fragment)
            }
            else{
                transaction.replace(container, fragment)
            }

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
            if(it is PlayMusicFragment){

                if (it.binding.layoutPlayMusic.currentState == R.id.end)
                    it.binding.layoutPlayMusic.transitionToStart()
                else{
                    super.onBackPressed()
                    it.gotoService(MusicService.ACTION_CLOSE)
                    playMusicFragment = null
                }
            }
            else{
                supportFragmentManager.popBackStack()
            }
        }
    }

    override fun callBack(key: String?, data: Any?) {
        when (key) {
            HomeFragment.KEY_SHOW_PLAY_MUSIC ->{
                val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                if(playMusicFragment == null){
                    playMusicFragment = PlayMusicFragment(this)
                    transaction.replace(R.id.container_layout_playing, playMusicFragment!!)
                    transaction.addToBackStack("add")
                }
                else{
                    playMusicFragment!!.updateSong()
                    transaction.show(playMusicFragment!!)
                }
                transaction.commit()

            }
            PlayMusicFragment.KEY_SHOW_ADD_FAVORITE ->{
                val addFavoriteFragment = AddFavoriteFragment(this)
                showFragment(R.id.container_layout_playing, addFavoriteFragment,
                    isAdd = true,
                    addToBackStack = true,
                    anim_start = 0,
                    anim_end = 0
                )
            }
            HomeFragment.KEY_SHOW_SEE_ALL ->{
                val seeAllFragment = SeeAllFragment(this)
                seeAllFragment.text = data as String
                showFragment(R.id.container_navigation, seeAllFragment,
                    isAdd = false,
                    addToBackStack = true,
                    anim_start = 0,
                    anim_end = 0
                )
            }
            PlayMusicFragment.KEY_SHOW_SERVICE ->{
                if (data != null ) {
                    val intent = Intent(this, MusicService::class.java)
                    intent.putExtra("action", data as Int)
                    startService(intent)
                }
            }
        }
    }

}