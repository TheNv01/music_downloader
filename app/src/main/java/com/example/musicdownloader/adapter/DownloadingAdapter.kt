package com.example.musicdownloader.adapter

import android.util.Log
import androidx.databinding.ViewDataBinding
import com.example.musicdownloader.databinding.ItemDownloadingBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.DownloadingManager
import com.example.musicdownloader.manager.MusicDonwnloadedManager
import com.example.musicdownloader.model.MusicDownloading
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2core.DownloadBlock

class DownloadingAdapter(
    layoutID: Int,
    private val musicdownloadings: ArrayList<MusicDownloading>,
    itemClickListener: ItemClickListener<MusicDownloading>
): BaseAdapter<MusicDownloading, ItemDownloadingBinding>(layoutID, musicdownloadings, itemClickListener) {

    override fun setViewHolder(binding: ViewDataBinding): BaseViewHolder<MusicDownloading, ItemDownloadingBinding> {
        return TopDownloadViewHolder(binding as ItemDownloadingBinding)
    }

    inner class TopDownloadViewHolder(private val binding: ItemDownloadingBinding) : BaseAdapter.BaseViewHolder<MusicDownloading, ItemDownloadingBinding>(binding) {
        override fun bindData(data: MusicDownloading) {
            binding.musicdownloading = data
            DownloadingManager.fetch!!.addListener(
                object : FetchListener {
                    override fun onQueued(download: Download, waitingOnNetwork: Boolean) {}
                    override fun onRemoved(download: Download) {}
                    override fun onCompleted(download: Download) {
                        if(data.request.id == download.id){
                            musicdownloadings.remove(data)
                            notifyDataSetChanged()
                        }
                    }
                    override fun onDeleted(download: Download) {
                        if(data.request.id == download.id){
                            musicdownloadings.remove(data)
                            notifyDataSetChanged()
                        }
                    }
                    override fun onDownloadBlockUpdated(download: Download, downloadBlock: DownloadBlock, totalBlocks: Int) {}
                    override fun onError(download: Download, error: Error, throwable: Throwable?) {
                        Log.d("error", error.name)
                        if(data.request.id == download.id){
                            DownloadingManager.fetch!!.retry(data.request.id)
                        }
                    }
                    override fun onPaused(download: Download) {}
                    override fun onResumed(download: Download) {}
                    override fun onStarted(download: Download, downloadBlocks: List<DownloadBlock>, totalBlocks: Int) {}
                    override fun onWaitingNetwork(download: Download) {}
                    override fun onAdded(download: Download) {}
                    override fun onCancelled(download: Download) {}
                    override fun onProgress(
                        download: Download,
                        etaInMilliSeconds: Long,
                        downloadedBytesPerSecond: Long
                    ) {
                        if(data.request.id == download.id){
                            val progress = download.progress
                            binding.tvProgress.text = progress.toString().plus("%")
                            binding.progressHorizontal.progress = progress
                        }
                    }

                })
        }

        override fun clickListener(data: MusicDownloading, itemClickListener: ItemClickListener<MusicDownloading>) {
            binding.popup.setOnClickListener {
                itemClickListener.onClickListener(data)
            }
        }
    }
}