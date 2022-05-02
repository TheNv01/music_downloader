package com.example.musicdownloader.view.fragment

import android.view.View
import com.example.musicdownloader.R
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.databinding.PlayMusicFragmentBinding
import com.example.musicdownloader.viewmodel.PlayMusicViewModel

class PlayMusicFragment(private val callBack: OnActionCallBack): BaseFragment<PlayMusicFragmentBinding, PlayMusicViewModel>(callBack) {
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

    }

    override fun setUpObserver() {

    }
}