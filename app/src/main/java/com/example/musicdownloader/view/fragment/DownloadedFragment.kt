package com.example.musicdownloader.view.fragment

import android.view.View
import com.example.musicdownloader.R
import com.example.musicdownloader.databinding.DownloadedFragmentBinding
import com.example.musicdownloader.viewmodel.DownloadedViewModel

class DownloadedFragment: BaseFragment<DownloadedFragmentBinding, DownloadedViewModel>() {
    override fun initBinding(mRootView: View): DownloadedFragmentBinding {
        return DownloadedFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<DownloadedViewModel> {
        return DownloadedViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.downloaded_fragment
    }

    override fun initViews() {


    }

    override fun setUpListener() {

    }

    override fun setUpObserver() {

    }
}