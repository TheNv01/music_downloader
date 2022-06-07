package com.example.musicdownloader.view.fragment

import android.app.Dialog
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.findNavController
import com.example.musicdownloader.R
import com.example.musicdownloader.adapter.GenericAdapter
import com.example.musicdownloader.adapter.PlaylistExistBinding
import com.example.musicdownloader.databinding.PlaylistOnFragmentBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.model.Playlist
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.view.dialog.BottomDialog
import com.example.musicdownloader.viewmodel.PlaylistOnViewModel

class PlaylistOnFragment: BaseFragment<PlaylistOnFragmentBinding, PlaylistOnViewModel>() {

    private var idPlaylist: Int = -1


    override fun initBinding(mRootView: View): PlaylistOnFragmentBinding {
        return PlaylistOnFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<PlaylistOnViewModel> {
        return PlaylistOnViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.playlist_on_fragment
    }

    override fun initViews() {
    }

    override fun setUpListener() {
        binding.imgBackgroundCreate.setOnClickListener {
            showCreatePlaylistDialog()
        }
        binding.imgBackgroundFavorite.setOnClickListener {
            val action = PlayListFragmentDirections.actionPlayListFragmentToFavoriteFragment()
            requireActivity().findNavController(R.id.activity_main_nav_host_fragment).navigate(action)
        }
    }

    override fun setUpObserver() {
        binding.viewmodel = mViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setMenuClickListener()
        binding.recyclerViewExistingPlaylist.adapter = GenericAdapter(
            R.layout.item_existing_playlist,
            PlaylistExistBinding,
            object : ItemClickListener<Playlist> {
                override fun onClickListener(model: Playlist) {
                    val action = PlayListFragmentDirections
                        .actionPlayListFragmentToPlaylistInsideFragment(model)
                    requireActivity().findNavController(R.id.activity_main_nav_host_fragment).navigate(action)
                }
            })
    }

    private fun setMenuClickListener(){
        PlaylistExistBinding.menuClickListener = object : ItemClickListener<Playlist> {
            override fun onClickListener(model: Playlist) {
                val bottomSheetDialog = BottomDialog(mViewModel.optionsDownloaded)
                bottomSheetDialog.show((activity as MainActivity).supportFragmentManager, null)
                val playlist = model
                bottomSheetDialog.itemClickListener = object : ItemClickListener<Int> {
                    override fun onClickListener(model: Int) {
                        when(model){
                            R.drawable.ic_rename ->{
                                showRenamePlaylistDialog(id)
                            }
                            R.drawable.ic_delete ->{
                                showConfirmDialog(playlist)
                            }
                        }
                    }
                }
            }
        }
    }

    fun showRenamePlaylistDialog(id : Int) {
        val dialog = Dialog(requireActivity(), R.style.Theme_Dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.create_playlist_fragment)
        val tvCreate = dialog.findViewById<TextView>(R.id.tv_create)
        val edt = dialog.findViewById<EditText>(R.id.edt_playlist)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        tvCreate.text = "DONE"
        dialog.findViewById<TextView>(R.id.tv_title).text = "Name Playlist"
        tvCreate.setOnClickListener{
            mViewModel.renamePlaylist(edt.text.toString(), id)
            dialog.dismiss()
            binding.tvNamePlaylist.text = edt.text.toString()
        }
        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showConfirmDialog(playlist: Playlist) {
        val dialog = Dialog(requireActivity(), R.style.Theme_Dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.comfirm_dialog)
        val tvDone = dialog.findViewById<TextView>(R.id.tv_done)
        tvDone.text = "DELETE"
        val tvContent = dialog.findViewById<TextView>(R.id.tv_content)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)

        tvContent.text = "Do You Want To Remove Playlist?"
        dialog.findViewById<TextView>(R.id.tv_title).text = playlist.name
        tvDone.setOnClickListener{
            dialog.dismiss()
            mViewModel.deletePlaylist(playlist.id)

        }
        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun showCreatePlaylistDialog() {
        val dialog = Dialog(requireActivity(), R.style.Theme_Dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.create_playlist_fragment)
        val tvCreate = dialog.findViewById<TextView>(R.id.tv_create)
        val edt = dialog.findViewById<EditText>(R.id.edt_playlist)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        tvCreate.text = "CREATE"
        dialog.findViewById<TextView>(R.id.tv_title).text = "Create new playlist"
        tvCreate.setOnClickListener{
           idPlaylist = mViewModel.createPlaylist(Playlist(edt.text.toString(), ArrayList())).toInt()
            val action = PlayListFragmentDirections.actionPlayListFragmentToPlaylistNoDataFragment(edt.text.toString(), idPlaylist)
            requireActivity().findNavController(R.id.activity_main_nav_host_fragment).navigate(action)
            dialog.dismiss()
        }
        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

}