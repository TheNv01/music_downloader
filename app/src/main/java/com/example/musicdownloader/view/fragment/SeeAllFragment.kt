package com.example.musicdownloader.view.fragment

import android.view.View
import com.example.musicdownloader.R
import com.example.musicdownloader.databinding.SeeAllFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.viewmodel.SeeAllViewModel

class SeeAllFragment(private val callBack: OnActionCallBack): BaseFragment<SeeAllFragmentBinding, SeeAllViewModel>(callBack) {
    override fun initBinding(mRootView: View): SeeAllFragmentBinding {
        return SeeAllFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<SeeAllViewModel> {
        return SeeAllViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.see_all_fragment
    }

    override fun initViews() {

    }

    override fun setUpListener() {

    }

    override fun setUpObserver() {

    }
}