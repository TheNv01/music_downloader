package com.example.musicdownloader.interfaces.itemclickinterface

interface ItemClickListener<T: Any> {
    fun onClickListener(model: T)
}