package com.example.musicdownloader.view.fragment

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import com.example.musicdownloader.R
import com.example.musicdownloader.adapter.ExistingPlaylistAdapter
import com.example.musicdownloader.databinding.AddToPlaylistFragmentBinding
import com.example.musicdownloader.interfaces.itemclickinterface.AddMusicToPlaylist
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.Option
import com.example.musicdownloader.model.Playlist
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.view.dialog.CreatePlaylistDialog
import com.example.musicdownloader.viewmodel.PlayListViewModel

class AddToPlaylistFragment: BaseFragment<AddToPlaylistFragmentBinding, PlayListViewModel>() {

    private lateinit var adapter: ExistingPlaylistAdapter

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
        adapter = ExistingPlaylistAdapter(
            R.layout.item_existing_playlist,
            mViewModel.existingPlaylist.value as ArrayList<Playlist>,
            true,
            requireContext(),
            object : ItemClickListener<Playlist> {
                override fun onClickListener(model: Playlist) {
                    Log.d("asdfasdf", "hahaha")
                }
            })
        adapter.addMusicToPlaylist = object : AddMusicToPlaylist {
            override fun onClickAddMusicListener(namePlaylist: String, music: Music) {
                mViewModel.addMusicToPlaylist(namePlaylist, music)
                Toast.makeText(context, "Add to playlist successfully", Toast.LENGTH_SHORT).show()
            }
        }
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

        binding.recyclerViewExistingPlaylist.adapter = adapter

    }
    private fun showCreatePlaylistDialog() {
        val customDialogInfo = CreatePlaylistDialog()
        customDialogInfo.show((activity as MainActivity).supportFragmentManager, "create_playlist_dialog")
    }
}