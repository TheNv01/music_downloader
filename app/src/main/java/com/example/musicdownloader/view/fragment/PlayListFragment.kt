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

    private lateinit var adapter: ExistingPlaylistAdapter

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
        adapter = ExistingPlaylistAdapter(
            R.layout.item_existing_playlist,
            mViewModel.existingPlaylist,
            false,
            requireContext(),
            object : ItemClickListener<Option> {
                override fun onClickListener(model: Option) {
                    Log.d("asdfasdf", "hahaha")
                }
            })
    }

    override fun setUpListener() {
        binding.tvDelete.setOnClickListener {
            if(binding.tvDelete.text.equals("Delete")){
                binding.tvDelete.text = "Cancel"
                adapter.isDelete = true
                adapter.notifyDataSetChanged()
            }
            else{
                binding.tvDelete.text = "Delete"
                adapter.isDelete = false
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun setUpObserver() {
        binding.recyclerViewExistingPlaylist.adapter = adapter
    }
}
