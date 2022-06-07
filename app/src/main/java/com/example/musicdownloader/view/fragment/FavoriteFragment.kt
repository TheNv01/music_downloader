package com.example.musicdownloader.view.fragment

import android.view.View
import com.example.musicdownloader.R
import com.example.musicdownloader.adapter.GenericAdapter
import com.example.musicdownloader.adapter.SearchBinding
import com.example.musicdownloader.adapter.SeeAllBinding
import com.example.musicdownloader.databinding.FavoriteFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.MusicDonwnloadedManager
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.viewmodel.FavoriteViewModel

class FavoriteFragment: BaseFragment<FavoriteFragmentBinding, FavoriteViewModel>(), OnActionCallBack {

    private lateinit var callback: OnActionCallBack

    private val musicItemClickListener = object : ItemClickListener<Music> {
        override fun onClickListener(model: Music) {
            MusicDonwnloadedManager.currentMusicDownloaded = null
            MusicManager.setCurrentMusic(model)
            callback.callBack(null, null)
        }
    }


    override fun initBinding(mRootView: View): FavoriteFragmentBinding {
        return FavoriteFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<FavoriteViewModel> {
        return FavoriteViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.favorite_fragment
    }

    override fun initViews() {
        callback = this
    }

    override fun setUpListener() {
        binding.icBack.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
    }

    override fun setUpObserver() {
        binding.viewmodel = mViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setupRecyclerview()
    }

    private fun setupRecyclerview() {

        SeeAllBinding.itemClickListener = object : ItemClickListener<Music> {
            override fun onClickListener(model: Music) {
                optionBottomDialog(model)
            }
        }

        binding.recyclerFavorite.adapter = GenericAdapter(
            R.layout.item_top_listened,
            SeeAllBinding,
            musicItemClickListener
        )
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