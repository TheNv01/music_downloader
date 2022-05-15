package com.example.musicdownloader

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.example.musicdownloader.viewmodel.ApiStatus

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String) {
    val reallyImgUrl: String = if(imgUrl.length < 15){
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

@BindingAdapter("fromResource")
fun setBackground(imgView: ImageView, resource: Int) {
    imgView.setImageResource(resource)
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

@BindingAdapter("apiStatusView")
fun bindStatusView(view: View, status: ApiStatus?) {
    if(status == ApiStatus.DONE){
        view.visibility = View.VISIBLE
    }
    else{
        view.visibility = View.INVISIBLE
    }
}