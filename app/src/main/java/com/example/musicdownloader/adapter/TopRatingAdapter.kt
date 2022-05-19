package com.example.musicdownloader.adapter

import androidx.databinding.ViewDataBinding
import com.example.musicdownloader.databinding.ItemTopRatingBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Music

class TopRatingAdapter(
    layoutID: Int,
    val musics: List<Music>,
    itemClickListener: ItemClickListener<Music>
): BaseAdapter<Music, ItemTopRatingBinding>(layoutID, musics, itemClickListener) {

    override fun setViewHolder(binding: ViewDataBinding): BaseViewHolder<Music, ItemTopRatingBinding> {
        return TopRatingViewHolder(binding as ItemTopRatingBinding)
    }

    inner class TopRatingViewHolder(private val binding: ItemTopRatingBinding) : BaseViewHolder<Music, ItemTopRatingBinding>(binding) {
        override fun bindData(data: Music) {
            binding.music = data
            binding.tvOrder.text = "#${(adapterPosition + 1)}"
        }

        override fun clickListener(data: Music, itemClickListener: ItemClickListener<Music>) {
            binding.layoutTopRatting.setOnClickListener {
                itemClickListener.onClickListener(data)
                MusicManager.setListMusic(musics)
            }
        }
    }
}