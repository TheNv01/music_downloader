package com.example.musicdownloader.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicdownloader.R
import com.example.musicdownloader.model.Item


class SimpleAdapter(private val mList: List<Item>): RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_top_download, parent, false)
        return  SimpleViewHolder(view)
    }

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holderSimple: SimpleViewHolder, position: Int) {
        val item = mList[position]
        holderSimple.tvSingle.text = item.single
        holderSimple.tvMusic.text = item.name
        holderSimple.imgBackground.setImageResource(item.image)

    }

    class SimpleViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvSingle = view.findViewById<TextView>(R.id.tv_single)
        val tvMusic = view.findViewById<TextView>(R.id.tv_music)
        val imgBackground = view.findViewById<ImageView>(R.id.img_top_download)

        fun scale(){
            imgBackground.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
                if(p1){
                    val anim: Animation =
                        AnimationUtils.loadAnimation(imgBackground.context, R.anim.scale_in_tv)
                    imgBackground.startAnimation(anim)
                    anim.fillAfter = true
                } else{
                    val anim: Animation =
                        AnimationUtils.loadAnimation(imgBackground.context, R.anim.scale_out_tv)
                    imgBackground.startAnimation(anim)
                    anim.fillAfter = true
                }
            }
        }


    }
}