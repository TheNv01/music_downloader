package com.example.musicdownloader.view.fragment

import android.view.View
import com.example.musicdownloader.OnActionCallBack
import com.example.musicdownloader.R
import com.example.musicdownloader.databinding.DownloadFragmentBinding
import com.example.musicdownloader.databinding.SettingFragmentBinding
import com.example.musicdownloader.viewmodel.DownloadViewModel
import com.example.musicdownloader.viewmodel.SettingViewModel

class SettingFragment(private val callBack: OnActionCallBack): BaseFragment<SettingFragmentBinding, SettingViewModel>(callBack) {
    override fun initBinding(mRootView: View): SettingFragmentBinding {
        return SettingFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<SettingViewModel> {
        return SettingViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.setting_fragment
    }

    override fun initViews() {

    }

    override fun setUpListener() {

    }

    override fun setUpObserver() {

    }
}