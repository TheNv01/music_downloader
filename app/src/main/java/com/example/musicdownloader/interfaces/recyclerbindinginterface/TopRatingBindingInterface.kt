package com.example.musicdownloader.interfaces.recyclerbindinginterface

import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.databinding.ItemTopRatingBinding
import com.example.musicdownloader.model.Item
import com.example.musicdownloader.model.Music
import java.text.FieldPosition

object TopRatingBindingInterface: RecyclerBindingInterface<ItemTopRatingBinding, Music> {
    override fun binData(
        binder: ItemTopRatingBinding,
        model: Music,
        position: Int,
        itemListener: ItemClickListener<Music>) {

        binder.layoutTopRatting.setOnClickListener{
            itemListener.onClickListener(model)
        }
    }

}