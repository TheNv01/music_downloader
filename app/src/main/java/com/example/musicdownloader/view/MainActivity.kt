package com.example.musicdownloader.view

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.musicdownloader.OnActionCallBack
import com.example.musicdownloader.R
import com.example.musicdownloader.databinding.ActivityMainBinding
import com.example.musicdownloader.view.fragment.*

class MainActivity : AppCompatActivity(), OnActionCallBack {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

       setupBottomNavigation()
        initView()

    }

    private fun setupBottomNavigation(){
        binding.bottomNavigation.itemIconTintList = null
        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.page_home ->{
                    val homeFragment = HomeFragment(this)
                    showFragment(homeFragment, false, 0, 0)
                    true
                }
                R.id.page_download ->{
                    val downloadFragment = DownloadFragment(this)
                    showFragment(downloadFragment, false, 0, 0)
                    true
                }
                R.id.page_play_list ->{
                    val playlistFragment = PlayListFragment(this)
                    showFragment(playlistFragment, false, 0, 0)
                    true
                }
                R.id.page_setting ->{
                    val settingFragment = SettingFragment(this)
                    showFragment(settingFragment, false, 0, 0)
                    true
                }
                else -> false
            }

        }
    }

    private fun initView() {
        val homeFragment = HomeFragment(this)
        showFragment(homeFragment, false, 0, 0)
    }

    private fun showFragment(
        fragment: Fragment,
        addToBackStack: Boolean,
        anim_start: Int,
        anim_end: Int
    ) {
        if (!isFinishing) {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            if (anim_end != 0 && anim_start != 0) {
                transaction.setCustomAnimations(anim_start, anim_end)
            }
            transaction.replace(R.id.container_view, fragment)
            if (addToBackStack) {
                transaction.addToBackStack("add")
            }
            transaction.commit()
        }
    }

    override fun callBack(key: String?, data: Any?) {
        when (key) {
            SplashFragment.KEY_SHOW_HOME -> {
                val homeFragment = HomeFragment(this)
                showFragment(homeFragment, false, 0, 0)
            }

        }
    }

}