package com.example.musicdownloader.view.fragment

import android.view.View
import com.example.musicdownloader.R
import com.example.musicdownloader.databinding.DownloadingFragmentBinding
import com.example.musicdownloader.viewmodel.DownloadingViewModel

class DownloadingFragment : BaseFragment<DownloadingFragmentBinding, DownloadingViewModel>() {
    override fun initBinding(mRootView: View): DownloadingFragmentBinding {
        return DownloadingFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<DownloadingViewModel> {
        return DownloadingViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.downloading_fragment
    }

    override fun initViews() {


    }

    override fun setUpListener() {

    }

    override fun setUpObserver() {

    }
}