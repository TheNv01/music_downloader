package com.example.musicdownloader.adapter

import androidx.databinding.ViewDataBinding
import androidx.viewpager2.widget.ViewPager2
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.databinding.ItemTrendingBinding
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Music

class TrendingAdapter(
    layoutID: Int,
    var musics: MutableList<Music>,
    private val viewPager: ViewPager2,
    itemClickListener: ItemClickListener<Music>
): BaseAdapter<Music, ItemTrendingBinding>(layoutID, musics, itemClickListener) {

    override fun setViewHolder(binding: ViewDataBinding): BaseViewHolder<Music, ItemTrendingBinding> {
        return TrendingViewHolder(binding as ItemTrendingBinding)
    }

    inner class TrendingViewHolder(private val binding: ItemTrendingBinding) : BaseViewHolder<Music, ItemTrendingBinding>(binding) {
        override fun bindData(data: Music) {
            binding.music = data
            if(adapterPosition == musics.size - 2){
                viewPager.post(runnable)
            }
        }

        override fun clickListener(data: Music, itemClickListener: ItemClickListener<Music>) {
            binding.layoutTrending.setOnClickListener {
                itemClickListener.onClickListener(data)
                MusicManager.setListMusic(musics)
            }
            binding.executePendingBindings()
        }
    }
    private val runnable = Runnable {
        musics.addAll(musics)
        notifyDataSetChanged()
    }

}