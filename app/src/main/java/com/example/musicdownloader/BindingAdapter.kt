package com.example.musicdownloader

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.musicdownloader.adapter.GenericAdapter
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.viewmodel.ApiStatus

@BindingAdapter("listData")
fun <T: Any, V: ViewDataBinding> bindRecyclerView(recyclerView: RecyclerView, data: List<T>?) {
    if(data != null){
        val adapter = recyclerView.adapter as GenericAdapter<T, V>
        adapter.submitList(data)
    }

}

@BindingAdapter("setText")
fun <T: Any, V: ViewDataBinding> bindTextView(textView: TextView, data: String?) {
    if(data == null || data == ""){
        textView.text = "UNKNOW"
    }
    else{

        textView.text = data
    }

}

@BindingAdapter("list", "position")
fun bindImagePlaylist(imgView: ImageView, musics: List<Music>?, position: Int) {

    if(musics != null) {
        when (position) {
            1 -> {
                if (musics.isNotEmpty()) {
                    bindImage(imgView, musics[0].image)
                }
                else{
                    imgView.setImageResource(R.drawable.bg_playlist)
                }
            }
            2 -> {
                if (musics.size >= 2) {
                    bindImage(imgView, musics[1].image)
                }
                else{
                    imgView.setImageResource(R.drawable.bg_playlist)
                }
            }
            3 -> {
                if (musics.size >= 3) {
                    bindImage(imgView, musics[2].image)
                }
                else{
                    imgView.setImageResource(R.drawable.bg_playlist)
                }
            }
            4 -> {
                if (musics.size >= 4) {
                    bindImage(imgView, musics[3].image)
                }
                else{
                    imgView.setImageResource(R.drawable.bg_playlist)
                }
            }

        }
    }

}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {

    if(imgUrl == null){
        imgView.setImageResource(R.drawable.bg_playlist)
    }
    else{
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
}

@BindingAdapter("imageBitmap")
fun bindImage(imgView: ImageView, bitmap: Bitmap?) {
    if(bitmap != null){
        imgView.setImageBitmap(bitmap)
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

@BindingAdapter("intToString")
fun bindTextView(textView: TextView, sizeList: Int) {
    if(sizeList < 2){
        textView.text = sizeList.toString().plus(" song")
    }
    else{
        textView.text = sizeList.toString().plus(" songs")
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

@BindingAdapter("setFavorite")
fun bindStatusFavorite(imgView: ImageView, isExisted: Boolean) {
    if(isExisted){
        imgView.setImageResource(R.drawable.ic_favorite_selected)
    }
    else{
        imgView.setImageResource(R.drawable.ic_favorite)
    }
}