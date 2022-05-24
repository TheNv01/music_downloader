package com.example.musicdownloader.view.fragment

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import com.example.musicdownloader.R
import com.example.musicdownloader.adapter.FragmentAdapter
import com.example.musicdownloader.databinding.DownloadFragmentBinding
import com.example.musicdownloader.viewmodel.DownloadViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class DownloadFragment: BaseFragment<DownloadFragmentBinding, DownloadViewModel>() {
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

        setUpViewPager()
        reduceMarginsInTabs(binding.tabLayout)
    }

    override fun setUpListener() {

    }

    override fun setUpObserver() {

    }

    private fun setUpViewPager(){
        val adapter = FragmentAdapter(this)
        adapter.addFragment(DownloadedFragment(), "Downloaded")
        adapter.addFragment(DownloadingFragment(), "Downloading")

        binding.viewPager.adapter = adapter
        binding.viewPager.currentItem = 0
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()

    }

    private fun reduceMarginsInTabs(tabLayout: TabLayout) {
        val tabStrip = tabLayout.getChildAt(0)
        if (tabStrip is ViewGroup) {
            for (i in 0 until tabStrip.childCount) {
                val tabView = tabStrip.getChildAt(i)
                if (tabView.layoutParams is MarginLayoutParams) {
                    (tabView.layoutParams as MarginLayoutParams).leftMargin = 125
                    (tabView.layoutParams as MarginLayoutParams).rightMargin = 125
                }
            }
            tabLayout.requestLayout()
        }
    }
}