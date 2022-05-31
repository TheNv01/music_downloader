package com.example.musicdownloader.view.fragment

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResultListener
import com.example.musicdownloader.R
import com.example.musicdownloader.adapter.ExistingPlaylistAdapter
import com.example.musicdownloader.databinding.AddToPlaylistFragmentBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.MusicDownloaded
import com.example.musicdownloader.model.Playlist
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.view.dialog.CreatePlaylistDialog
import com.example.musicdownloader.viewmodel.PlayListViewModel

class AddToPlaylistFragment: BaseFragment<AddToPlaylistFragmentBinding, PlayListViewModel>() {

    var musicDownloaded: MusicDownloaded ?= null

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
        setFragmentResultListener("playlistKey") { _, bundle ->
            bundle.get("playlist").also {
                if(it is String){
                    Log.d("afasfasd", it)
                    mViewModel.createPlaylist(Playlist(it, ArrayList()))
                }
            }
        }
    }

    override fun setUpObserver() {

        (activity as MainActivity).onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.supportFragmentManager?.popBackStack("addToPlaylist", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        })
        setupRecyclerView()
        mViewModel.isExist.observe(viewLifecycleOwner){
            if(it){
                Toast.makeText(context, "Baì hát đã tồn tại trong playlist", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView(){
        mViewModel.existingPlaylist.observe(this){
            binding.recyclerViewExistingPlaylist.adapter = ExistingPlaylistAdapter(
                R.layout.item_existing_playlist,
                it as ArrayList<Playlist>,
                true,
                object : ItemClickListener<Playlist> {
                    override fun onClickListener(model: Playlist) {
                        if(musicDownloaded == null){
                            mViewModel.addMusicToPlaylist(model.name, model.id, MusicManager.getCurrentMusic()!!)
                        }
                        else{
                            Log.d("asdfas", "adfasdf")
                        }

                    }
                })
        }

    }
    private fun showCreatePlaylistDialog() {
        val customDialogInfo = CreatePlaylistDialog()
        customDialogInfo.show((activity as MainActivity).supportFragmentManager, "create_playlist_dialog")
    }
}