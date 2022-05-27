package com.example.musicdownloader.view.fragment

import android.app.Dialog
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.musicdownloader.R
import com.example.musicdownloader.adapter.TopListenedAdapter
import com.example.musicdownloader.databinding.PlaylistInsideFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.view.dialog.PlaylistInsidePopup
import com.example.musicdownloader.viewmodel.PlaylistInsideViewModel

class PlaylistInsideFragment : BaseFragment<PlaylistInsideFragmentBinding, PlaylistInsideViewModel>(), OnActionCallBack {

    lateinit var callBack: OnActionCallBack
    private val args: PlaylistInsideFragmentArgs by navArgs()

    private val musicItemClickListener = object : ItemClickListener<Music> {
        override fun onClickListener(model: Music) {
            MusicManager.setCurrentMusic(model)
            mViewModel.musics.value?.let { MusicManager.setListMusic(it) }
            callBack.callBack(null, null)
        }
    }

    private val menuClickListener = object : ItemClickListener<Music> {
        override fun onClickListener(model: Music) {
            openSongBottomSheet(model)
        }
    }
    override fun initBinding(mRootView: View): PlaylistInsideFragmentBinding {
        return PlaylistInsideFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<PlaylistInsideViewModel> {
        return PlaylistInsideViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.playlist_inside_fragment
    }

    override fun initViews() {
        callBack = this

    }

    override fun setUpListener() {
        binding.tvNamePlaylist.text = args.playList.name
        binding.icPopup.setOnClickListener {
            openPlaylistBottomSheet()
        }
    }

    private fun openPlaylistBottomSheet() {
        val bottomSheetDialog = PlaylistInsidePopup(mViewModel.optionsPlaylist)
        bottomSheetDialog.show((activity as MainActivity).supportFragmentManager, null)
        bottomSheetDialog.itemClickListener = object : ItemClickListener<Int>{
            override fun onClickListener(model: Int) {
                if(model == R.drawable.ic_delete){
                    mViewModel.deletePlaylist(args.playList.id)
                    (activity as MainActivity).findNavController(R.id.activity_main_nav_host_fragment).popBackStack()
                }
                else{
                    showRenamePlaylistDialog()
                }
            }
        }
    }

    private fun openSongBottomSheet(music: Music) {
        val bottomSheetDialog = PlaylistInsidePopup(mViewModel.optionsSong)
        bottomSheetDialog.show((activity as MainActivity).supportFragmentManager, null)
        bottomSheetDialog.itemClickListener = object : ItemClickListener<Int> {
            override fun onClickListener(model: Int) {
                if (model == R.drawable.ic_delete) {
                    Log.d("remove", "hjahahaa")
                    mViewModel.removeMusic(args.playList.name, args.playList.id, music)
                }
                else{
                    Log.d("elsesss", "hahahaha")
                }
            }
        }
    }

    override fun setUpObserver() {
        mViewModel.existingPlaylist.observe(this){
            loadData()
            mViewModel.musics.observe(this){
                binding.recyclerViewPlaylistInside.adapter = TopListenedAdapter(
                    R.layout.item_top_listened,
                    false,
                    it,
                    musicItemClickListener,
                    menuClickListener)
                binding.tvQuantitySong.text = getString(R.string.number_music, it.size.toString())
            }
        }
    }

    fun showRenamePlaylistDialog() {
        val dialog = Dialog(requireActivity(), R.style.Theme_Dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.create_playlist_fragment)
        val tvCreate = dialog.findViewById<TextView>(R.id.tv_create)
        val edt = dialog.findViewById<EditText>(R.id.edt_playlist)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        tvCreate.text = "DONE"
        dialog.findViewById<TextView>(R.id.tv_title).text = "Name Playlist"
        tvCreate.setOnClickListener{
            mViewModel.renamePlaylist(edt.text.toString(), args.playList.id)
            dialog.dismiss()
            binding.tvNamePlaylist.text = edt.text.toString()
        }
        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun loadData(){
        mViewModel.getMusics(args.playList.id)
    }

    override fun callBack(key: String?, data: Any?) {
        val tran = activity?.supportFragmentManager?.beginTransaction()
        (activity as MainActivity).let {
            if(it.playMusicFragment == null){
                it.playMusicFragment = PlayMusicFragment()
                tran?.replace(R.id.container_layout_playing, it.playMusicFragment!!)
                tran?.addToBackStack("playFragment")
            }
            else{
                it.playMusicFragment!!.updateSong()
                tran?.show(it.playMusicFragment!!)
            }
            tran?.commit()
        }
    }
}