package com.example.musicdownloader

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.musicdownloader.adapter.GenericAdapter
import com.example.musicdownloader.viewmodel.ApiStatus

@BindingAdapter("listData")
fun <T: Any, V: ViewDataBinding> bindRecyclerView(recyclerView: RecyclerView, data: List<T>?) {
    val adapter = recyclerView.adapter as GenericAdapter<T, V>
    adapter.submitList(data)
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String) {
    val reallyImgUrl: String = if(imgUrl.length < 8){
        "http://marstechstudio.com/img-msd/$imgUrl"
    }
    else{
        imgUrl
    }
    reallyImgUrl.let {
        val imgUri = reallyImgUrl.toUri().buildUpon().scheme("http").build()
        imgView.load(imgUri){
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
        }
    }
}

@BindingAdapter("apiStatus")
fun bindStatus(progressBar: ProgressBar, status: ApiStatus?) {
    if(status == ApiStatus.DONE){
        progressBar.visibility = View.GONE
    }
    else{
        progressBar.visibility = View.VISIBLE
    }
}