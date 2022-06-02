package com.example.musicdownloader.view.fragment

import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import com.example.musicdownloader.R
import com.example.musicdownloader.SharedPreferencesManager
import com.example.musicdownloader.adapter.GenericAdapter
import com.example.musicdownloader.adapter.SearchBinding
import com.example.musicdownloader.adapter.SeeAllBinding
import com.example.musicdownloader.databinding.SearchFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.MusicDonwnloadedManager
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.Region
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.viewmodel.SearchViewModel

class SearchFragment : BaseFragment<SearchFragmentBinding, SearchViewModel>(), OnActionCallBack {


    lateinit var callBack: OnActionCallBack

    private val musicItemClickListener = object : ItemClickListener<Music> {
        override fun onClickListener(model: Music) {
            MusicDonwnloadedManager.currentMusicDownloaded = null
            MusicManager.setCurrentMusic(model)
            mViewModel.musics.value?.let { MusicManager.setListMusic(it) }
            callBack.callBack(null, null)
        }
    }

    override fun initBinding(mRootView: View): SearchFragmentBinding {
        return SearchFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<SearchViewModel> {
        return SearchViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.search_fragment
    }


    override fun initViews() {
        callBack = this
        binding.tvRecommend.visibility = View.INVISIBLE
    }

    override fun setUpListener() {}

    override fun setUpObserver() {
        binding.viewmodel = mViewModel
        binding.lifecycleOwner = this

        setupSearchView()

        setupRecyclerview()

    }

    private fun setupSearchView(){
        binding.searchSong.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mViewModel.getMusics(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mViewModel.getMusics(newText)
                return true
            }

        })
    }

    private fun setupRecyclerview(){
        SeeAllBinding.itemClickListener = object : ItemClickListener<Music> {
            override fun onClickListener(model: Music) {
                Log.d("hahaha", model.name!!)
            }

        }
        binding.recyclerViewSongRecommend.adapter = GenericAdapter(
            R.layout.item_top_listened,
            SearchBinding,
            musicItemClickListener)
    }


    override fun callBack(key: String?, data: Any?) {
        val tran = activity?.supportFragmentManager?.beginTransaction()
        (activity as MainActivity).let {
            if(it.playMusicFragment == null){
                it.playMusicFragment = PlayMusicFragment()
                tran?.replace(R.id.container_layout_playing, it.playMusicFragment!!)
                tran?.addToBackStack("playFragment")
            }
            else{
                it.playMusicFragment!!.updateSong()
                tran?.show(it.playMusicFragment!!)
            }
            tran?.commit()
        }
    }


}