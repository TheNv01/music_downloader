package com.example.musicdownloader.view.fragment

import android.util.Log
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import com.example.musicdownloader.R
import com.example.musicdownloader.SharedPreferencesManager
import com.example.musicdownloader.adapter.GenreSeeAllAdapter
import com.example.musicdownloader.adapter.TopListenedAdapter
import com.example.musicdownloader.databinding.SeeAllFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Genres
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.Region
import com.example.musicdownloader.viewmodel.SeeAllViewModel

class SeeAllFragment(private val callBack: OnActionCallBack): BaseFragment<SeeAllFragmentBinding, SeeAllViewModel>(callBack) {

    var text = ""

    private val musicItemClickListener = object : ItemClickListener<Music> {
        override fun onClickListener(model: Music) {
            callBack.callBack(HomeFragment.KEY_SHOW_PLAY_MUSIC, model)
            mViewModel.musics.value?.let { MusicManager.setListMusic(it) }
        }
    }

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
        setFragmentResultListener("seeAllKey") { _, bundle ->
            bundle.getString("option").also {
                if (it != null) {
                    Log.d("optionnn", it)
                    loadData(it)
                }
            }
        }
    }

    private fun loadData(option: String){
        SharedPreferencesManager.get<Region>("country").let { region ->
            if (region == null) {
                mViewModel.getMusics(option, null)
            } else {
                mViewModel.getMusics(option, region.regionCode)
            }
        }
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

        mViewModel.musics.observe(this){
            binding.recyclerViewSeeAll.adapter = TopListenedAdapter(
                R.layout.item_top_listened,
                it,
                false,
                musicItemClickListener)
        }
    }
}