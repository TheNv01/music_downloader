package com.example.musicdownloader.adapter

import androidx.databinding.ViewDataBinding
import com.example.musicdownloader.R
import com.example.musicdownloader.databinding.ItemGenresSeeAllBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.model.Genres

class GenreSeeAllAdapter(
    layoutID: Int,
    genres: List<Genres>,
    itemClickListener: ItemClickListener<Genres>
): BaseAdapter<Genres, ItemGenresSeeAllBinding>(layoutID, genres, itemClickListener) {

    private var row = 0

    override fun setViewHolder(binding: ViewDataBinding): BaseViewHolder<Genres, ItemGenresSeeAllBinding> {
        return GenreSeeAllViewHolder(binding as ItemGenresSeeAllBinding)
    }

    inner class GenreSeeAllViewHolder(private val binding: ItemGenresSeeAllBinding) : BaseViewHolder<Genres, ItemGenresSeeAllBinding>(binding) {
        override fun bindData(data: Genres) {
            binding.genres = data
        }

        override fun clickListener(data: Genres, itemClickListener: ItemClickListener<Genres>) {
            binding.tvTest.setOnClickListener {
                itemClickListener.onClickListener(data)
                row = adapterPosition
                notifyDataSetChanged()
            }

            if(row == adapterPosition){
                binding.tvTest.setBackgroundResource(R.drawable.gradient_pink)
            }
            else{
                binding.tvTest.setBackgroundResource(R.drawable.background_genre_unselected)
            }
            binding.executePendingBindings()
        }
    }
}