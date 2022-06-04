package com.example.musicdownloader.view.fragment

import android.app.Dialog
import android.content.Intent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.musicdownloader.R
import com.example.musicdownloader.adapter.GenericAdapter
import com.example.musicdownloader.adapter.PlaylistInsideBinding
import com.example.musicdownloader.databinding.PlaylistInsideFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.MusicDonwnloadedManager
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.view.dialog.BottomDialog
import com.example.musicdownloader.viewmodel.PlaylistInsideViewModel

class PlaylistInsideFragment : BaseFragment<PlaylistInsideFragmentBinding, PlaylistInsideViewModel>(), OnActionCallBack {

    lateinit var callBack: OnActionCallBack
    private val args: PlaylistInsideFragmentArgs by navArgs()

    private val musicItemClickListener = object : ItemClickListener<Music> {
        override fun onClickListener(model: Music) {
            MusicDonwnloadedManager.currentMusicDownloaded = null
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
        binding.tvAddSong.setOnClickListener {
            val action = PlaylistInsideFragmentDirections
                .actionPlaylistInsideFragmentToSearchFragment( 2, args.playList)
            requireActivity().findNavController(R.id.activity_main_nav_host_fragment).navigate(action)
        }
    }

    private fun openPlaylistBottomSheet() {
        val bottomSheetDialog = BottomDialog(mViewModel.optionsPlaylist)
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
        val bottomSheetDialog = BottomDialog(mViewModel.optionsSong)
        bottomSheetDialog.show((activity as MainActivity).supportFragmentManager, null)
        bottomSheetDialog.itemClickListener = object : ItemClickListener<Int> {
            override fun onClickListener(model: Int) {
                when(model){
                    R.drawable.ic_delete -> {
                        mViewModel.removeMusic(args.playList.name, args.playList.id, music)
                    }
                    R.drawable.ic_share -> {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
                        intent.putExtra(Intent.EXTRA_TEXT, music.audio)
                        startActivity(Intent.createChooser(intent, "Share via"));
                    }
                    R.drawable.ic_bell -> {
                        mViewModel.removeMusic(args.playList.name, args.playList.id, music)
                    }
                    R.drawable.ic_favorite -> {
                        mViewModel.removeMusic(args.playList.name, args.playList.id, music)
                    }

                }

            }
        }
    }

    override fun setUpObserver() {
        PlaylistInsideBinding.itemClickListener = menuClickListener
        binding.viewmodel = mViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setupRecyclerview()
        mViewModel.existingPlaylist.observe(viewLifecycleOwner){
            loadData()
        }
//        mViewModel.musics.observe(viewLifecycleOwner){
//            binding.tvQuantitySong.text = getString(R.string.number_music,it.size.toString())
//
//        }
    }

    private fun setupRecyclerview(){
        binding.recyclerViewPlaylistInside.adapter = GenericAdapter(
            R.layout.item_top_listened,
            PlaylistInsideBinding,
            musicItemClickListener)
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