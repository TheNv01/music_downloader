package com.example.musicdownloader.view.fragment

import android.util.Log
import android.view.View
import com.example.musicdownloader.R
import com.example.musicdownloader.adapter.ExistingPlaylistAdapter
import com.example.musicdownloader.databinding.PlayListFragmentBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.model.Option
import com.example.musicdownloader.viewmodel.PlayListViewModel

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

    }

    override fun setUpListener() {

    }

    override fun setUpObserver() {
        binding.recyclerViewExistingPlaylist.adapter = ExistingPlaylistAdapter(
            R.layout.item_existing_playlist,
            mViewModel.existingPlaylist,
            false,
            object : ItemClickListener<Option> {
                override fun onClickListener(model: Option) {
                    Log.d("asdfasdf", "hahaha")
                }
            })
    }
}
