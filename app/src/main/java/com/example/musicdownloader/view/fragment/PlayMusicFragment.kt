package com.example.musicdownloader.view.fragment

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import androidx.constraintlayout.motion.widget.MotionLayout
import com.example.musicdownloader.R
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.databinding.PlayMusicFragmentBinding
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.viewmodel.PlayMusicViewModel
import kotlin.math.abs

class PlayMusicFragment(private val callBack: OnActionCallBack): BaseFragment<PlayMusicFragmentBinding, PlayMusicViewModel>(callBack) {

    companion object{
        const val KEY_SHOW_ADD_FAVORITE = "KEY_SHOW_ADD_FAVORITE"
    }

    override fun initBinding(mRootView: View): PlayMusicFragmentBinding {
        return PlayMusicFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<PlayMusicViewModel> {
        return PlayMusicViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.play_music_fragment
    }

    override fun initViews() {
    }

    override fun setUpListener() {

        setupMotionLayout()
        binding.icFavorite.setOnClickListener {
            callBack.callBack(KEY_SHOW_ADD_FAVORITE, null)
        }
        binding.icClose.setOnClickListener{
            (activity as MainActivity).onBackPressed()
        }
        binding.icBack.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
    }

    override fun setUpObserver() {

    }

    private fun setupMotionLayout(){
        binding.layoutPlayMusic.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {

                (activity as MainActivity).also {
                    it.binding.mainMotionLayout.progress = abs(progress)
                }
            }
            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {

                (activity as MainActivity).also {
                    if(binding.layoutPlayMusic.currentState == R.id.start){
                        it.binding.mainMotionLayout.transitionToStart()
                    }
                }
            }

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }
        })
        binding.layoutPlayMusic.transitionToEnd()
    }

}