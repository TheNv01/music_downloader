package com.example.musicdownloader.adapter

import com.example.musicdownloader.databinding.ItemGenresBinding
import com.example.musicdownloader.databinding.ItemGenresSeeAllBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.interfaces.recyclerbindinginterface.RecyclerBindingInterface
import com.example.musicdownloader.model.Genres

object GenresSeeAllBinding : RecyclerBindingInterface<ItemGenresSeeAllBinding, Genres> {
    override fun binData(
        binder: ItemGenresSeeAllBinding,
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