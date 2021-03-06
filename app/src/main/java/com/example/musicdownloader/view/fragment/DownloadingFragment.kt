package com.example.musicdownloader.view.fragment

import android.content.Intent
import android.view.View
import com.example.musicdownloader.R
import com.example.musicdownloader.adapter.DownloadingAdapter
import com.example.musicdownloader.databinding.DownloadingFragmentBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.DownloadingManager
import com.example.musicdownloader.model.MusicDownloading
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.view.dialog.BottomDialog
import com.example.musicdownloader.viewmodel.DownloadViewModel
import com.example.musicdownloader.viewmodel.DownloadingViewModel

class DownloadingFragment : BaseFragment<DownloadingFragmentBinding, DownloadViewModel>(){

    private var bottomSheetDialog: BottomDialog ?= null

    override fun initBinding(mRootView: View): DownloadingFragmentBinding {
        return DownloadingFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<DownloadViewModel> {
        return DownloadViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.downloading_fragment
    }

    override fun initViews() {
    }

    override fun setUpListener() {
    }

    override fun setUpObserver() {
        binding.recyclerViewDownloading.adapter = DownloadingAdapter(
            R.layout.item_downloading,
            DownloadingManager.listDownloading(),
            object : ItemClickListener<MusicDownloading> {
                override fun onClickListener(model: MusicDownloading) {
                    openDownloadingBottomSheet(model)
                }
            }
        )
    }

    private fun openDownloadingBottomSheet(musicDownloading: MusicDownloading) {
        if(bottomSheetDialog == null){
            bottomSheetDialog = BottomDialog(mViewModel.optionsDownloading)
        }

        bottomSheetDialog!!.show((activity as MainActivity).supportFragmentManager, null)
        bottomSheetDialog!!.itemClickListener = object : ItemClickListener<Int>{
            override fun onClickListener(model: Int) {
                when(model){
                    R.drawable.ic_remove ->{
                        DownloadingManager.fetch!!.cancel(musicDownloading.request.id)
                        DownloadingManager.fetch!!.delete(musicDownloading.request.id)
                    }
                    R.drawable.ic_pause_download ->{
                        DownloadingManager.fetch!!.resume(musicDownloading.request.id)
                    }
                    R.drawable.ic_play_not_background ->{
                        DownloadingManager.fetch!!.pause(musicDownloading.request.id)
                    }
                    R.drawable.ic_share ->{
                        shareMusic(musicDownloading)
                    }
                }
            }
        }
    }

    private fun shareMusic(musicDownloading: MusicDownloading){
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
        intent.putExtra(Intent.EXTRA_TEXT,musicDownloading.request.url)
        startActivity(Intent.createChooser(intent, "Share link"))
    }
}