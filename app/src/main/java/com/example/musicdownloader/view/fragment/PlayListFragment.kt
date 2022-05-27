package com.example.musicdownloader.view.fragment

import android.util.Log
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import com.example.musicdownloader.R
import com.example.musicdownloader.adapter.ExistingPlaylistAdapter
import com.example.musicdownloader.databinding.PlayListFragmentBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.model.Playlist
import com.example.musicdownloader.view.MainActivity
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

    }

    override fun setUpListener() {
        binding.imgBackgroundCreate.setOnClickListener {
            (activity as MainActivity).findNavController(R.id.activity_main_nav_host_fragment).navigate(R.id.createPlaylistDialog)
        }
    }

    override fun setUpObserver() {
        mViewModel.existingPlaylist.observe(this){
            adapter = ExistingPlaylistAdapter(
                R.layout.item_existing_playlist,
                it as ArrayList<Playlist>,
                false,
                object : ItemClickListener<Playlist> {
                    override fun onClickListener(model: Playlist) {
                        val action = PlayListFragmentDirections.actionPlayListFragmentToPlaylistInsideFragment(model)
                        requireActivity().findNavController(R.id.activity_main_nav_host_fragment).navigate(action)
                    }
                })
            binding.recyclerViewExistingPlaylist.adapter = adapter
        }
        setFragmentResultListener("playlistKey") { _, bundle ->
            bundle.get("playlist").also {
                if(it is String){
                    Log.d("afasfasd", it)
                    mViewModel.createPlaylist(Playlist(it, ArrayList()))
                }
            }
        }
    }
}
