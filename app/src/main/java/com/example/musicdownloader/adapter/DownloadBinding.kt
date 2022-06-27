package com.example.musicdownloader.adapter

import com.example.musicdownloader.databinding.ItemDownloadedBinding
import com.example.musicdownloader.databinding.ItemTopRatingBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.interfaces.recyclerbindinginterface.RecyclerBindingInterface
import com.example.musicdownloader.manager.MusicDonwnloadedManager
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.MusicDownloaded

object DownloadBinding : RecyclerBindingInterface<ItemDownloadedBinding, MusicDownloaded> {

    val menuClickListener: ItemClickListener<MusicDownloaded> ?= null

    override fun binData(
        binder: ItemDownloadedBinding,
        model: MusicDownloaded,
        position: Int,
        itemListener: ItemClickListener<MusicDownloaded>
    ) {
        binder.musicdownloaded = model

        binder.layoutDownloaded.setOnClickListener {
            itemListener.onClickListener(model)
//            MusicDonwnloadedManager.musicsDownloaded = musicsDownloaded
        }
        binder.popup.setOnClickListener {
            menuClickListener?.onClickListener(model)
        }
    }

}