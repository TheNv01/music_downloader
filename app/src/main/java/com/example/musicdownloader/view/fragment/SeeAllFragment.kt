package com.example.musicdownloader.view.fragment

import android.util.Log
import android.view.View
import com.example.musicdownloader.R
import com.example.musicdownloader.adapter.GenreSeeAllAdapter
import com.example.musicdownloader.databinding.SeeAllFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.model.Genres
import com.example.musicdownloader.viewmodel.SeeAllViewModel

class SeeAllFragment(private val callBack: OnActionCallBack): BaseFragment<SeeAllFragmentBinding, SeeAllViewModel>(callBack) {

    var text = ""

    override fun initBinding(mRootView: View): SeeAllFragmentBinding {
        return SeeAllFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<SeeAllViewModel> {
        return SeeAllViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.see_all_fragment
    }

    override fun initViews() {
        binding.tvTitle.text = text
    }

    override fun setUpListener() {

    }

    override fun setUpObserver() {
        binding.viewmodel = mViewModel
        binding.lifecycleOwner = this

        mViewModel.allGenre.observe(this){
            binding.recyclerViewGenres.adapter = GenreSeeAllAdapter(
                R.layout.item_genres_see_all,
                it,
                object : ItemClickListener<Genres> {
                    override fun onClickListener(model: Genres) {
                        Log.d("asdfasdf", "hahaha")
                    }

                })
        }
    }
}