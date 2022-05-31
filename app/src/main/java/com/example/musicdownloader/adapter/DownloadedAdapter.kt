package com.example.musicdownloader.adapter

import android.graphics.Color
import androidx.databinding.ViewDataBinding
import com.example.musicdownloader.databinding.ItemDownloadedBinding
import com.example.musicdownloader.databinding.ItemTopListenedBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.MusicDonwnloadedManager
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.MusicDownloaded

class DownloadedAdapter(
    layoutID: Int,
    private val musicsDownloaded: List<MusicDownloaded>,
    itemClickListener: ItemClickListener<MusicDownloaded>,
    private val menuClickListener: ItemClickListener<MusicDownloaded>
): BaseAdapter<MusicDownloaded, ItemDownloadedBinding>(layoutID, musicsDownloaded, itemClickListener) {

    override fun setViewHolder(binding: ViewDataBinding): BaseViewHolder<MusicDownloaded, ItemDownloadedBinding> {
        return TopListenedViewHolder(binding as ItemDownloadedBinding)
    }

    inner class TopListenedViewHolder(private val binding: ItemDownloadedBinding) :
        BaseAdapter.BaseViewHolder<MusicDownloaded, ItemDownloadedBinding>(binding) {
        override fun bindData(data: MusicDownloaded) {
            binding.musicdownloaded = data
        }

        override fun clickListener(data: MusicDownloaded, itemClickListener: ItemClickListener<MusicDownloaded>) {
            binding.layoutTopListened.setOnClickListener {
                itemClickListener.onClickListener(data)
                MusicDonwnloadedManager.musicsDownloaded = musicsDownloaded
            }
            binding.popup.setOnClickListener {
                menuClickListener.onClickListener(data)
            }
        }
    }
}