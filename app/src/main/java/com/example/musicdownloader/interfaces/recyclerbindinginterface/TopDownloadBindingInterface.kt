package com.example.musicdownloader.recyclerbindinginterface

import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.databinding.ItemTopDownloadBinding
import com.example.musicdownloader.model.Item

object TopDownloadBindingInterface : RecyclerBindingInterface<ItemTopDownloadBinding, Item>{
    override fun binData(binder: ItemTopDownloadBinding, model: Item, itemListener: ItemClickListener<Item>) {
        binder.imgTopDownload.setImageResource(model.image)
        binder.tvMusic.text = model.name
        binder.tvSingle.text = model.single
        binder.layoutTopDownload.setOnClickListener{
            itemListener.onClickListener(model)
        }
    }

}