package com.example.musicdownloader.view.fragment

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.example.musicdownloader.OnActionCallBack
import com.example.musicdownloader.R
import com.example.musicdownloader.databinding.DownloadFragmentBinding
import com.example.musicdownloader.viewmodel.DownloadViewModel


class DownloadFragment(private val callBack: OnActionCallBack): BaseFragment<DownloadFragmentBinding, DownloadViewModel>(callBack) {
    override fun initBinding(mRootView: View): DownloadFragmentBinding {
        return DownloadFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<DownloadViewModel> {
        return DownloadViewModel::class.java
    }

    override fun getLayoutId(): Int {
       return R.layout.download_fragment
    }

    override fun initViews() {
//        val animation = ObjectAnimator.ofFloat(view, "rotationY", 0.0f, 45f)
//        animation.duration = 3600
//        animation.repeatCount = ObjectAnimator.INFINITE
//        animation.interpolator = AccelerateDecelerateInterpolator()
//         animation.start()
        binding?.imDown?.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
            if(p1){
                val anim: Animation =
                    AnimationUtils.loadAnimation(context, R.anim.scale_in_tv)
                binding?.imDown?.startAnimation(anim)
                anim.fillAfter = true
            } else{
                val anim: Animation =
                    AnimationUtils.loadAnimation(context, R.anim.scale_out_tv)
                binding?.imDown?.startAnimation(anim)
                anim.fillAfter = true
            }
        }
    }

    override fun setUpListener() {

    }

    override fun setUpObserver() {

    }
}