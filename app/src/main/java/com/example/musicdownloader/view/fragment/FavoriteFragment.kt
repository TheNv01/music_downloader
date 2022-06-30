package com.example.musicdownloader.view.fragment

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.navigation.NavDirections
import com.example.musicdownloader.MusicService
import com.example.musicdownloader.R
import com.example.musicdownloader.Utils
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
import com.proxglobal.proxads.adsv2.ads.ProxAds
import com.proxglobal.proxads.adsv2.callback.AdsCallback

class FavoriteFragment: BaseFragment<FavoriteFragmentBinding, FavoriteViewModel>(), OnActionCallBack {

    private lateinit var callback: OnActionCallBack

    private val musicItemClickListener = object : ItemClickListener<Music> {
        override fun onClickListener(model: Music) {
            MusicDonwnloadedManager.currentMusicDownloaded = null
            MusicManager.setCurrentMusic(model)
            mViewModel.musics.value?.let { MusicManager.setListMusic(it) }
            showAds(null)
        }
    }

    override fun showAds(action: NavDirections?){
        ProxAds.getInstance().showInterstitialMax(requireActivity(), "inter", object: AdsCallback() {
            override fun onShow() {
                callback.callBack(null, null)
            }

            override fun onClosed() {
                (activity as MainActivity).playMusicFragment!!.gotoService(MusicService.ACTION_START)
            }

            override fun onError() {
                if(action == null){
                    callback.callBack(null, null)
                    Handler(Looper.getMainLooper()).postDelayed({
                        (activity as MainActivity).playMusicFragment?.gotoService(MusicService.ACTION_START)
                    }, 10)
                }
            }
        })
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
        showSmallNative(binding.adContainer)
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