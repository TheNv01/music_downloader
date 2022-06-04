package com.example.musicdownloader.view.fragment

import android.view.View
import com.example.musicdownloader.R
import com.example.musicdownloader.adapter.GenericAdapter
import com.example.musicdownloader.adapter.PlaylistExistBinding
import com.example.musicdownloader.databinding.PlaylistOnFragmentBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.model.Playlist
import com.example.musicdownloader.viewmodel.PlaylistOffViewModel

class PlaylistOffFragment: BaseFragment<PlaylistOnFragmentBinding, PlaylistOffViewModel>() {


    override fun initBinding(mRootView: View): PlaylistOnFragmentBinding {
        return PlaylistOnFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<PlaylistOffViewModel> {
        return PlaylistOffViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.playlist_on_fragment
    }

    override fun initViews() {
    }

    override fun setUpListener() {}

    override fun setUpObserver() {

    }

}