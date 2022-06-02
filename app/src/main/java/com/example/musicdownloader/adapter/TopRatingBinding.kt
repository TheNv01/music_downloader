package com.example.musicdownloader.adapter

import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.databinding.ItemTopRatingBinding
import com.example.musicdownloader.interfaces.recyclerbindinginterface.RecyclerBindingInterface
import com.example.musicdownloader.model.Music

object TopRatingBinding: RecyclerBindingInterface<ItemTopRatingBinding, Music> {
    override fun binData(
        binder: ItemTopRatingBinding,
        model: Music,
        position: Int,
        itemListener: ItemClickListener<Music>) {
        binder.music = model
        binder.layoutTopRatting.setOnClickListener{
            itemListener.onClickListener(model)
        }
    }

}