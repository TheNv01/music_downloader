package com.example.musicdownloader.view.fragment


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.example.musicdownloader.OnActionCallBack
import com.example.musicdownloader.R
import com.example.musicdownloader.databinding.SplashFragmentBinding
import com.example.musicdownloader.viewmodel.SplashViewModel


class SplashFragment(private val callBack: OnActionCallBack): BaseFragment<SplashFragmentBinding, SplashViewModel>(callBack) {


    companion object{
        const val KEY_SHOW_HOME = "KEY_SHOW_HOME"
    }

    override fun initBinding(mRootView: View): SplashFragmentBinding {
        return SplashFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<SplashViewModel> {
        return SplashViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.splash_fragment
    }


    override fun initViews() {
        Handler(Looper.getMainLooper()).postDelayed({  }, 5000)
    }
    private fun gotoHome() {
        callBack.callBack(KEY_SHOW_HOME, null)
    }
    override fun setUpListener() {}

    override fun setUpObserver() {}


}