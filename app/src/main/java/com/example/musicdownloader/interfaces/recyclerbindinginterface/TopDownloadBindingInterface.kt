package com.example.musicdownloader.interfaces.recyclerbindinginterface

import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.databinding.ItemTopDownloadBinding
import com.example.musicdownloader.interfaces.recyclerbindinginterface.RecyclerBindingInterface
import com.example.musicdownloader.model.Item
import com.example.musicdownloader.model.Music

object TopDownloadBindingInterface : RecyclerBindingInterface<ItemTopDownloadBinding, Music> {
    override fun binData(
        binder: ItemTopDownloadBinding,
        model: Music,
        position: Int,
        itemListener: ItemClickListener<Music>) {
        binder.music = model
        binder.layoutTopDownload.setOnClickListener{
            itemListener.onClickListener(model)
        }
    }

}