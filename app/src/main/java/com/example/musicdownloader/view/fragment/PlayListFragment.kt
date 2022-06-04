package com.example.musicdownloader.view.fragment

import android.view.View
import com.example.musicdownloader.R
import com.example.musicdownloader.adapter.FragmentAdapter
import com.example.musicdownloader.databinding.PlayListFragmentBinding
import com.example.musicdownloader.viewmodel.PlayListViewModel
import com.google.android.material.tabs.TabLayoutMediator

class PlayListFragment: BaseFragment<PlayListFragmentBinding, PlayListViewModel>() {


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
        setUpViewPager()
    }

    override fun setUpListener() {

    }

    override fun setUpObserver() {
    }

    private fun setUpViewPager(){
        val adapter = FragmentAdapter(this)
        adapter.addFragment(PlaylistOnFragment(), "Online Music")
        adapter.addFragment(PlaylistOffFragment(), "My Music")

        binding.viewPager.adapter = adapter
        binding.viewPager.currentItem = 0
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()

    }
}
