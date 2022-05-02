package com.example.musicdownloader.interfaces.recyclerbindinginterface

import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.databinding.ItemGenresBinding
import com.example.musicdownloader.model.Item
import com.example.musicdownloader.recyclerbindinginterface.RecyclerBindingInterface

object GenresBindingInterface : RecyclerBindingInterface<ItemGenresBinding, Item> {
    override fun binData(binder: ItemGenresBinding, model: Item, itemListener: ItemClickListener<Item>) {
        binder.imgGenres.setImageResource(model.image)
        binder.tvPop.text = model.single
        binder.layoutGenres.setOnClickListener{
            itemListener.onClickListener(model)
        }
    }

}