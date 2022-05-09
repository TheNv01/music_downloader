package com.example.musicdownloader

import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.musicdownloader.adapter.GenericAdapter
import com.example.musicdownloader.viewmodel.ApiStatus
import com.facebook.shimmer.ShimmerFrameLayout

@BindingAdapter("listData")
fun <T: Any, V: ViewDataBinding> bindRecyclerView(recyclerView: RecyclerView, data: List<T>?) {
    val adapter = recyclerView.adapter as GenericAdapter<T, V>
    adapter.submitList(data)
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("http").build()
        imgView.load(imgUri){
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
        }
    }
}