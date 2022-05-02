package com.example.musicdownloader.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.databinding.ItemTrendingBinding
import com.example.musicdownloader.model.Item

class TrendingAdapter(
    private val items: ArrayList<Item>,
    private val viewPager: ViewPager2,
    private val itemListener: ItemClickListener<Item>):
    RecyclerView.Adapter<TrendingAdapter.TrendingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingViewHolder {
       val binding = ItemTrendingBinding.inflate(
           LayoutInflater.from(parent.context),
           parent, false)

       return TrendingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrendingViewHolder, position: Int) {
        holder.bindData(items[position], itemListener)

        if(position == items.size - 2){
            viewPager.post(runnable)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class TrendingViewHolder(private val binding: ItemTrendingBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(item: Item, itemListener: ItemClickListener<Item>){
            binding.backgroundTrending.setImageResource(item.image)
            binding.tvMusic.text = item.name
            binding.tvSingle.text = item.single
            binding.layoutTrending.setOnClickListener {
                itemListener.onClickListener(item)
            }
        }

    }

    private val runnable = Runnable {
        items.addAll(items)
        notifyDataSetChanged()
    }

}