package com.example.musicdownloader.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.databinding.ItemTrendingBinding
import com.example.musicdownloader.model.Music

class TrendingAdapter(
    private val musics: ArrayList<Music>,
    private val viewPager: ViewPager2,
    private val itemListener: ItemClickListener<Music>):
    RecyclerView.Adapter<TrendingAdapter.TrendingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingViewHolder {
       val binding = ItemTrendingBinding.inflate(
           LayoutInflater.from(parent.context),
           parent, false)

       return TrendingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrendingViewHolder, position: Int) {
        holder.bindData(musics[position], itemListener)

        if(position == musics.size - 2){
            viewPager.post(runnable)
        }
    }

    override fun getItemCount(): Int {
        return musics.size
    }

    class TrendingViewHolder(private val binding: ItemTrendingBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(music: Music, itemListener: ItemClickListener<Music>){
            binding.music = music
            Log.d("imgae", music.image.toString())
            binding.layoutTrending.setOnClickListener {
                itemListener.onClickListener(music)
            }
            binding.executePendingBindings()
        }

    }

    private val runnable = Runnable {
        musics.addAll(musics)
        notifyDataSetChanged()
    }

}