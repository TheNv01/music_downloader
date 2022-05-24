package com.example.musicdownloader.view.fragment

import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import com.example.musicdownloader.R
import com.example.musicdownloader.adapter.ExistingPlaylistAdapter
import com.example.musicdownloader.databinding.AddToPlaylistFragmentBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.model.Option
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.view.dialog.CreatePlaylistDialog
import com.example.musicdownloader.viewmodel.PlayListViewModel

class AddToPlaylistFragment: BaseFragment<AddToPlaylistFragmentBinding, PlayListViewModel>() {
    override fun initBinding(mRootView: View): AddToPlaylistFragmentBinding {
        return AddToPlaylistFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<PlayListViewModel> {
        return PlayListViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.add_to_playlist_fragment
    }

    override fun initViews() {
    }

    override fun setUpListener() {
        binding.icBack.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
        binding.imgBackgroundCreate.setOnClickListener {
            showCreatePlaylistDialog()
        }
    }

    override fun setUpObserver() {

        (activity as MainActivity).onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.supportFragmentManager?.popBackStack("addToPlaylist", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        })

        binding.recyclerViewExistingPlaylist.adapter = ExistingPlaylistAdapter(
                R.layout.item_existing_playlist,
                mViewModel.existingPlaylist,
                true,
                requireContext(),
                object : ItemClickListener<Option> {
                    override fun onClickListener(model: Option) {
                        Log.d("asdfasdf", "hahaha")
                    }
                })
    }
    private fun showCreatePlaylistDialog() {
        val customDialogInfo = CreatePlaylistDialog()
        customDialogInfo.show((activity as MainActivity).supportFragmentManager, "create_playlist_dialog")
    }
}