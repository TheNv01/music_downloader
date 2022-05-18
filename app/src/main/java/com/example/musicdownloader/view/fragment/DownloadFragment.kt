package com.example.musicdownloader.view.fragment

import android.view.View
import com.example.musicdownloader.R
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.databinding.DownloadFragmentBinding
import com.example.musicdownloader.viewmodel.DownloadViewModel


class DownloadFragment(): BaseFragment<DownloadFragmentBinding, DownloadViewModel>() {
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


    }

    override fun setUpListener() {

    }

    override fun setUpObserver() {

    }
}