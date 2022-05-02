package com.example.musicdownloader.view.fragment

import android.view.View
import com.example.musicdownloader.R
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.databinding.PlayListFragmentBinding
import com.example.musicdownloader.viewmodel.PlayListViewModel

class PlayListFragment(private val callBack: OnActionCallBack): BaseFragment<PlayListFragmentBinding, PlayListViewModel>(callBack) {
    override fun initBinding(mRootView: View): PlayListFragmentBinding {
        return PlayListFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<PlayListViewModel> {
        return PlayListViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.play_list_fragment
    }

    override fun initViews() {

    }

    override fun setUpListener() {

    }

    override fun setUpObserver() {

    }
}