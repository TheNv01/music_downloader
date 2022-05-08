package com.example.musicdownloader.recyclerbindinginterface

import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.databinding.ItemTopListenedBinding
import com.example.musicdownloader.interfaces.recyclerbindinginterface.RecyclerBindingInterface
import com.example.musicdownloader.model.Item

object TopListenedBindingInterface : RecyclerBindingInterface<ItemTopListenedBinding, Item> {
    override fun binData(binder: ItemTopListenedBinding, model: Item, itemListener: ItemClickListener<Item>) {
        binder.imgTopListened.setImageResource(model.image)
        binder.tvMusic.text = model.name
        binder.tvSingle.text = model.single
        binder.layoutTopListened.setOnClickListener{
            itemListener.onClickListener(model)
        }
    }

}