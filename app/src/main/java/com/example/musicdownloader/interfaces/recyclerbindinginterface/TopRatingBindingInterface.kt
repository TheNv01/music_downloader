package com.example.musicdownloader.recyclerbindinginterface

import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.databinding.ItemTopRatingBinding
import com.example.musicdownloader.model.Item

object TopRatingBindingInterface: RecyclerBindingInterface<ItemTopRatingBinding, Item>{
    override fun binData(binder: ItemTopRatingBinding, model: Item, itemListener: ItemClickListener<Item>) {
        binder.backgroundRating.setImageResource(model.image)
        binder.tvMusic.text = model.name
        binder.tvSingle.text = model.single
        binder.layoutTopRatting.setOnClickListener{
            itemListener.onClickListener(model)
        }
    }

}