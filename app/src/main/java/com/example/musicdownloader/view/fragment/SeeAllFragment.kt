package com.example.musicdownloader.view.fragment

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.navigation.NavDirections
import androidx.navigation.fragment.navArgs
import com.example.musicdownloader.MusicService
import com.example.musicdownloader.R
import com.example.musicdownloader.SharedPreferencesManager
import com.example.musicdownloader.Utils
import com.example.musicdownloader.adapter.*
import com.example.musicdownloader.databinding.SeeAllFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.MusicDonwnloadedManager
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.Region
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.viewmodel.SeeAllViewModel
import com.proxglobal.proxads.adsv2.ads.ProxAds
import com.proxglobal.proxads.adsv2.callback.AdsCallback

class SeeAllFragment: BaseFragment<SeeAllFragmentBinding, SeeAllViewModel>(), OnActionCallBack {

    lateinit var callBack: OnActionCallBack
    private val args: SeeAllFragmentArgs by navArgs()

    private val musicItemClickListener = object : ItemClickListener<Music> {
        override fun onClickListener(model: Music) {
            MusicDonwnloadedManager.currentMusicDownloaded = null
            mViewModel.musics.value?.let { MusicManager.setListMusic(it) }
            MusicManager.setCurrentMusic(model)
            showAds(null)
        }
    }

    private val menuClickListener = object : ItemClickListener<Music> {
        override fun onClickListener(model: Music) {
            optionBottomDialog(model)
        }

    }

    override fun showAds(action: NavDirections?){
        ProxAds.getInstance().showInterstitialMax(requireActivity(), "inter", object: AdsCallback() {
            override fun onShow() {
                callBack.callBack(null, null)
            }

            override fun onClosed() {
                (activity as MainActivity).playMusicFragment!!.gotoService(MusicService.ACTION_START)
            }

            override fun onError() {
                if(action == null){
                    callBack.callBack(null, null)
                    Handler(Looper.getMainLooper()).postDelayed({
                        (activity as MainActivity).playMusicFragment?.gotoService(MusicService.ACTION_START)
                    }, 10)
                }
            }
        })
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
        callBack = this
        var option = args.option?.substring(0, 1)?.uppercase() + args.option?.substring(1)
        if(option == "Ranking"){
            option = "Rating"
        }
        if(mViewModel.title.value != null){
            mViewModel.title.postValue(args.option)
        }
        binding.tvTitle.text = getString(R.string.title, option)
        showSmallNative(binding.adContainer)
    }

    override fun setUpListener() {
        binding.icBack.setOnClickListener {
            (activity as MainActivity).onBackPressed()
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
        binding.lifecycleOwner = viewLifecycleOwner

        mViewModel.title.observe(viewLifecycleOwner){
            if(it != args.option.toString()){
                loadData(args.option.toString())
            }
        }
        SeeAllBinding.itemClickListener = menuClickListener
        binding.recyclerViewSeeAll.adapter = GenericAdapter(
            R.layout.item_top_listened,
            SeeAllBinding,
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