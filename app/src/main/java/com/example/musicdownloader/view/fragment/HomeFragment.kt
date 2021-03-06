package com.example.musicdownloader.view.fragment

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.LiveData
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.navigation.findNavController
import com.example.musicdownloader.*
import com.example.musicdownloader.adapter.*
import com.example.musicdownloader.databinding.HomeFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.adapter.TopListenedBinding
import com.example.musicdownloader.manager.MediaManager
import com.example.musicdownloader.manager.MusicDonwnloadedManager
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Genres
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.Region
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.viewmodel.HomeViewModel
import com.proxglobal.proxads.ProxUtils
import com.proxglobal.proxads.adsv2.ads.ProxAds
import com.proxglobal.proxads.adsv2.callback.AdsCallback

class HomeFragment: BaseFragment<HomeFragmentBinding, HomeViewModel>(), OnActionCallBack {

    lateinit var callBack: OnActionCallBack
    private lateinit var tvRegion: TextView
    private lateinit var imgLanguage: ImageView


    private fun setMusicClickListener(listMusicLiveData: LiveData<List<Music>> ?= null) =  object : ItemClickListener<Music> {
        override fun onClickListener(model: Music) {
            MusicDonwnloadedManager.currentMusicDownloaded = null
            MusicManager.setCurrentMusic(model)
            listMusicLiveData?.observe(viewLifecycleOwner){
                if(it != null){
                    MusicManager.setListMusic(it)
                }
            }
          showAds(null)
        }
    }


    override fun showAds(action:NavDirections?){
        ProxAds.getInstance().showInterstitialMax(requireActivity(), "inter", object: AdsCallback() {
            override fun onShow() {
                if (action == null) {
                    callBack.callBack(null, null)
                } else {
                    if(!MediaManager.isPause){
                        (activity as MainActivity).playMusicFragment?.gotoService(MusicService.ACTION_PAUSE)
                    }
                    else{
                        isUserClickPause = true
                    }
                    requireActivity().findNavController(R.id.activity_main_nav_host_fragment).navigate(action)
                }
            }

            override fun onClosed() {
                if (action == null) {
                    (activity as MainActivity).playMusicFragment!!.gotoService(MusicService.ACTION_START)
                }
                else{
                    if(!MediaManager.mediaPlayer!!.isPlaying){
                        if(!isUserClickPause){
                            (activity as MainActivity).playMusicFragment?.gotoService(MusicService.ACTION_RESUME)
                        }
                        else{
                            isUserClickPause = false
                        }
                    }
                }
            }

            override fun onError() {
                if(action == null){
                    callBack.callBack(null, null)
                    Handler(Looper.getMainLooper()).postDelayed({
                        (activity as MainActivity).playMusicFragment?.gotoService(MusicService.ACTION_START)
                    }, 10)
                }
                else{
                    requireActivity().findNavController(R.id.activity_main_nav_host_fragment).navigate(action)
                }
            }
        })
    }

    override fun initBinding(mRootView: View): HomeFragmentBinding {
       return HomeFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<HomeViewModel> {
       return HomeViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.home_fragment
    }

    override fun initViews() {
        tvRegion = mRootView.findViewById(R.id.tv_region)
        imgLanguage = mRootView.findViewById(R.id.img_language)
        callBack = this
        setStatusBarColor(R.color.black)
        showBigNative()
    }


    private fun showBigNative(){

        ProxAds.getInstance().showBigNativeMaxWithShimmer(requireActivity(), getString(R.string.native_id), binding.adContainer, object : AdsCallback() {
            override fun onShow() {
                super.onShow()
            }

            override fun onClosed() {
                super.onClosed()
            }

            override fun onError() {
                super.onError()
                binding.adContainer.visibility = View.GONE
            }
        })
    }

    override fun setUpListener() {

        SharedPreferencesManager.get<Region>("country")?.let {
            imgLanguage.setImageResource(it.regionIcon)
            tvRegion.text = it.regionName
        }

        binding.icSearch.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSearchFromHomeFragment()
            requireActivity().findNavController(R.id.activity_main_nav_host_fragment).navigate(action)
        }

        binding.tvSeeAllDownload.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSeeAllFragment("download")
            handleClickSeeAll(action, mViewModel.countClickSeeAll)
        }
        binding.tvSeeAllRating.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSeeAllFragment("ranking")
            handleClickSeeAll(action, mViewModel.countClickSeeAll)
        }
        binding.tvSeeAllListened.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSeeAllFragment("listened")
            handleClickSeeAll(action, mViewModel.countClickSeeAll)
        }
        binding.tvSeeAllGenres.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSeeAllGenresFragment()
            handleClickSeeAll(action, mViewModel.countClickSeeAll)
        }
        binding.layoutCountry.setOnClickListener {
            (activity as MainActivity).findNavController(R.id.activity_main_nav_host_fragment).navigate(R.id.changeRegionDialog)
        }
        setFragmentResultListener("requestKey") { _, bundle ->
             bundle.get("region").also {
                 if(it is Region){
                     SharedPreferencesManager.put(it, "country")
                     mViewModel.factoryMusics("popular", it.regionCode)
                     mViewModel.factoryMusics("ranking", it.regionCode)
                     mViewModel.factoryMusics("listened", it.regionCode)
                     mViewModel.factoryMusics("download", it.regionCode)
                     tvRegion.text = it.regionName
                     imgLanguage.setImageResource(it.regionIcon)
                 }
             }
        }
    }


    override fun setUpObserver() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = mViewModel
        setupTrendingViewPager()
        setupRecyclerview()
    }

    private fun setupRecyclerview(){

        TopListenedBinding.itemClickListener = object : ItemClickListener<Music>{
            override fun onClickListener(model: Music) {
                optionBottomDialog(model)
            }
        }
        binding.recyclerViewTopRating.adapter = GenericAdapter(
            R.layout.item_top_rating,
            TopRatingBinding,
            setMusicClickListener(mViewModel.topRatings))

        binding.recyclerViewTopListened.adapter = GenericAdapter(
            R.layout.item_top_listened,
            TopListenedBinding,
            setMusicClickListener(mViewModel.topListeneds))
        binding.recyclerViewTopDownload.adapter = GenericAdapter(
            R.layout.item_top_download,
            TopDownloadBinding,
            setMusicClickListener(mViewModel.topDownloads))
        binding.recyclerViewGenres.adapter = GenericAdapter(
            R.layout.item_genres,
            GenresBinding,
            object : ItemClickListener<Genres> {
                override fun onClickListener(model: Genres) {
                    val action = HomeFragmentDirections.actionHomeFragmentToInsideGenresFragment(model.name!!)
                    handleClickSeeAll(action, mViewModel.countItemGenres)
                }
            })
    }


    private fun setupTrendingViewPager(){
        val viewPager = binding.viewPagerTrending
        mViewModel.trends.observe(this){
            if(it.isNotEmpty()){
                viewPager.adapter = TrendingAdapter(R.layout.item_trending, it as ArrayList<Music>,setMusicClickListener())
            }
            else{
                viewPager.adapter =TrendingAdapter(R.layout.item_trending, ArrayList(), setMusicClickListener())
            }

        }
        viewPager.clipToPadding = false
        viewPager.clipChildren = false
        viewPager.offscreenPageLimit = 3
        viewPager.getChildAt(0)?.overScrollMode = RecyclerView.SCROLL_AXIS_HORIZONTAL

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer(UltraDepthScaleTransformer())
        viewPager.setPageTransformer(compositePageTransformer)
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