package com.example.musicdownloader.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.musicdownloader.R
import com.proxglobal.proxads.ProxUtils
import com.proxglobal.proxads.ads.callback.AdCallback
import com.proxglobal.proxads.ads.callback.AdClose
import com.proxglobal.proxads.adsv2.ads.ProxAds
import com.proxglobal.proxads.adsv2.callback.AdsCallback


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        initAds()
        showAds()
    }

    private fun showAds(){
        ProxAds.getInstance().showSplashMax(this, object : AdsCallback() {
            override fun onShow() {

            }
            override fun onClosed() {
                gotoMainActivity()
            }
            override fun onError() {
                gotoMainActivity()

            }
        }, ProxUtils.TEST_INTERSTITIAL_MAX_ID, 7000)
    }

    private fun initAds(){
        ProxAds.getInstance()
            .initInterstitialMax(this, ProxUtils.TEST_INTERSTITIAL_ID, "inter")
    }

    private fun gotoMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}