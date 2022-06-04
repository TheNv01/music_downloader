package com.example.musicdownloader.adapter

import com.example.musicdownloader.databinding.ItemTopListenedBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.interfaces.recyclerbindinginterface.RecyclerBindingInterface
import com.example.musicdownloader.model.Music

object SeeAllBinding: RecyclerBindingInterface<ItemTopListenedBinding, Music> {

    var itemClickListener: ItemClickListener<Music>?= null

    override fun binData(
        binder: ItemTopListenedBinding,
        model: Music,
        position: Int,
        itemListener: ItemClickListener<Music>
    ) {
        binder.music = model
        binder.tvId.text = (position + 1).toString() + "."

        binder.layoutTopListened.setOnClickListener{
            itemListener.onClickListener(model)
        }
        binder.popup.setOnClickListener {
            itemClickListener?.onClickListener(model)
        }
    }


}