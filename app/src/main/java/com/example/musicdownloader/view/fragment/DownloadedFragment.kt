package com.example.musicdownloader.view.fragment

import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResult
import com.example.musicdownloader.R
import com.example.musicdownloader.adapter.DownloadedAdapter
import com.example.musicdownloader.databinding.DownloadedFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.MusicDonwnloadedManager
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.MusicDownloaded
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.view.dialog.DownloadPopup
import com.example.musicdownloader.viewmodel.DownloadedViewModel
import java.io.File


class DownloadedFragment: BaseFragment<DownloadedFragmentBinding, DownloadedViewModel>(), OnActionCallBack {

    private lateinit var adapter: DownloadedAdapter
    private lateinit var callback: OnActionCallBack

    private val itemClickListener = object : ItemClickListener<MusicDownloaded> {
        override fun onClickListener(model: MusicDownloaded) {
            MusicManager.setCurrentMusic(null)
            MusicDonwnloadedManager.currentMusicDownloaded = model
            callback.callBack(HomeFragment.KEY_SHOW_PLAY_MUSIC, null)
        }

    }

    private val menuClickListener = object : ItemClickListener<MusicDownloaded> {
        override fun onClickListener(model: MusicDownloaded) {
            openDownloadingBottomSheet(model)
        }

    }

    override fun initBinding(mRootView: View): DownloadedFragmentBinding {
        return DownloadedFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<DownloadedViewModel> {
        return DownloadedViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.downloaded_fragment
    }

    override fun initViews() {
        callback = this
    }

    override fun setUpListener() {

    }

    override fun setUpObserver() {
        mViewModel.getListMusicDownloaded()
        adapter= DownloadedAdapter(
            R.layout.item_downloaded,
            mViewModel.downloadeds,
            itemClickListener,
            menuClickListener
        )
        binding.recyclerViewDownloaded.adapter = adapter
    }

    private fun openDownloadingBottomSheet(musicDownloaded: MusicDownloaded) {
        val bottomSheetDialog = DownloadPopup(mViewModel.optionsDownloaded)
        bottomSheetDialog.show((activity as MainActivity).supportFragmentManager, null)
        bottomSheetDialog.itemClickListener = object : ItemClickListener<Int>{
            override fun onClickListener(model: Int) {
                when(model){
                    R.drawable.ic_add_to_playlist ->{
                        callback.callBack(AddFavoriteFragment.KEY_SHOW_ADD_TO_PLAYLIST, musicDownloaded)
                    }
                    R.drawable.ic_delete ->{
                        musicDownloaded.uri?.let { File(it).delete() }
                        mViewModel.getListMusicDownloaded()
                        adapter.notifyDataSetChanged()
                    }
                    R.drawable.ic_bell ->{
                        Log.d("hahahaa", "hjahaaa")
                        RingtoneManager.setActualDefaultRingtoneUri(
                            context,
                            RingtoneManager.TYPE_RINGTONE,
                            Uri.parse(musicDownloaded.uri)
                        )
                    }

                }
            }
        }
    }


    override fun callBack(key: String?, data: Any?) {
        val tran = (activity as MainActivity).supportFragmentManager.beginTransaction()
        when(key){
            HomeFragment.KEY_SHOW_PLAY_MUSIC ->{
                (activity as MainActivity).let {
                    if(it.playMusicFragment == null){
                        it.playMusicFragment = PlayMusicFragment()
                        tran.replace(R.id.container_layout_playing, it.playMusicFragment!!)
                        tran.addToBackStack("playFragment")
                    }
                    else{
                        it.playMusicFragment!!.updateSong()
                        tran.show(it.playMusicFragment!!)
                    }
                    tran.commit()
                }
            }
            AddFavoriteFragment.KEY_SHOW_ADD_TO_PLAYLIST ->{
                val addToPlaylistFragment = AddToPlaylistFragment()
                addToPlaylistFragment.musicDownloaded = data as MusicDownloaded
                tran.add(R.id.container_layout_playing, addToPlaylistFragment)
                tran.addToBackStack("addToPlaylist")
                tran.commit()
            }
        }
    }

}