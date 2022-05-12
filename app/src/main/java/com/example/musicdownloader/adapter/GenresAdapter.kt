package com.example.musicdownloader.adapter

import androidx.databinding.ViewDataBinding
import com.example.musicdownloader.databinding.ItemGenresBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.model.Genres

class GenresAdapter(
    layoutID: Int,
    genres: List<Genres>,
    itemClickListener: ItemClickListener<Genres>
): BaseAdapter<Genres, ItemGenresBinding>(layoutID, genres, itemClickListener) {

    override fun setViewHolder(binding: ViewDataBinding): BaseViewHolder<Genres, ItemGenresBinding> {
        return GenresViewHolder(binding as ItemGenresBinding)
    }

    inner class GenresViewHolder(private val binding: ItemGenresBinding) : BaseViewHolder<Genres, ItemGenresBinding>(binding) {
        override fun bindData(data: Genres) {
            binding.genres = data
        }

        override fun clickListener(data: Genres, itemClickListener: ItemClickListener<Genres>) {
           binding.layoutGenres.setOnClickListener {
               itemClickListener.onClickListener(data)
           }
        }
    }
}