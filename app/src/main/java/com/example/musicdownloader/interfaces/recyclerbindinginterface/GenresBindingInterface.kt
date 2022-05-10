package com.example.musicdownloader.interfaces.recyclerbindinginterface

import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.databinding.ItemGenresBinding
import com.example.musicdownloader.model.Genres
import com.example.musicdownloader.model.Item

object GenresBindingInterface : RecyclerBindingInterface<ItemGenresBinding, Genres> {
    override fun binData(
        binder: ItemGenresBinding,
        model: Genres,
        position: Int,
        itemListener: ItemClickListener<Genres>
    ) {
        binder.genres = model
        binder.layoutGenres.setOnClickListener{
            itemListener.onClickListener(model)
        }
    }

}