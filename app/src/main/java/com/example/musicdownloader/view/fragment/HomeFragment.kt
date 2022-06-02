package com.example.musicdownloader.view.fragment

import android.util.Log
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.navigation.findNavController
import com.example.musicdownloader.R
import com.example.musicdownloader.SharedPreferencesManager
import com.example.musicdownloader.UltraDepthScaleTransformer
import com.example.musicdownloader.adapter.*
import com.example.musicdownloader.databinding.HomeFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.adapter.TopListenedBinding
import com.example.musicdownloader.manager.MusicDonwnloadedManager
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Genres
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.Region
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.viewmodel.HomeViewModel

class HomeFragment: BaseFragment<HomeFragmentBinding, HomeViewModel>(), OnActionCallBack {

    lateinit var callBack: OnActionCallBack

    companion object{
        const val KEY_SHOW_PLAY_MUSIC = "KEY_SHOW_PLAY_MUSIC"
    }

    private val musicItemClickListener = object : ItemClickListener<Music> {
        override fun onClickListener(model: Music) {
            MusicDonwnloadedManager.currentMusicDownloaded = null
            MusicManager.setCurrentMusic(model)
            callBack.callBack(KEY_SHOW_PLAY_MUSIC, null)
        }
    }

    private val menuClickListener = object : ItemClickListener<Music> {
        override fun onClickListener(model: Music) {
        }

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
        callBack = this
        context?.let { SharedPreferencesManager.with(it) }

        SharedPreferencesManager.get<Region>("country")?.let {
            binding.imgLanguage.setImageResource(it.regionIcon)
            binding.tvRegion.text = it.regionName
        }
    }

    override fun setUpListener() {

        binding.icSearch.setOnClickListener {
            requireActivity().findNavController(R.id.activity_main_nav_host_fragment).navigate(R.id.searchFragment)
        }

        binding.tvSeeAllDownload.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSeeAllFragment("download")
            requireActivity().findNavController(R.id.activity_main_nav_host_fragment).navigate(action)
        }
        binding.tvSeeAllRating.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSeeAllFragment("ranking")
            requireActivity().findNavController(R.id.activity_main_nav_host_fragment).navigate(action)
        }
        binding.tvSeeAllListened.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSeeAllFragment("listened")
            requireActivity().findNavController(R.id.activity_main_nav_host_fragment).navigate(action)
        }
        binding.tvSeeAllGenres.setOnClickListener {
            requireActivity().findNavController(R.id.activity_main_nav_host_fragment).navigate(R.id.seeAllGenresFragment)
        }
        binding.tvRegion.setOnClickListener {
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
                     binding.tvRegion.text = it.regionName
                     binding.imgLanguage.setImageResource(it.regionIcon)
                 }
             }
        }
    }

    override fun setUpObserver() {

        SharedPreferencesManager.get<Region>("country").let {
            if(it == null){
                mViewModel.factoryMusics("popular", null)
                mViewModel.factoryMusics("ranking", null)
                mViewModel.factoryMusics("listened", null)
                mViewModel.factoryMusics("download", null)
            }
            else{
                mViewModel.factoryMusics("popular", it.regionCode)
                mViewModel.factoryMusics("ranking", it.regionCode)
                mViewModel.factoryMusics("listened", it.regionCode)
                mViewModel.factoryMusics("download", it.regionCode)
            }
        }
        binding.lifecycleOwner = this
        binding.viewmodel = mViewModel
        setupTrendingViewPager()
        setupRecyclerview()
    }

    private fun setupRecyclerview(){

        TopListenedBinding.itemClickListener = object : ItemClickListener<Music>{
            override fun onClickListener(model: Music) {
                Log.d("hahaha", model.name!!)
            }

        }
        binding.recyclerViewTopRating.adapter = GenericAdapter(
            R.layout.item_top_rating,
            TopRatingBinding,
            musicItemClickListener)

        binding.recyclerViewTopListened.adapter = GenericAdapter(
            R.layout.item_top_listened,
            TopListenedBinding,
            musicItemClickListener)
        binding.recyclerViewTopDownload.adapter = GenericAdapter(
            R.layout.item_top_download,
            TopDownloadBinding,
            musicItemClickListener)
        binding.recyclerViewGenres.adapter = GenericAdapter(
            R.layout.item_genres,
            GenresBinding,
            object : ItemClickListener<Genres> {
                override fun onClickListener(model: Genres) {
                    Log.d("asdfasdf", "hahaha")
                }

            })
    }

    private fun setupTrendingViewPager(){
        val viewPager = binding.viewPagerTrending
        mViewModel.trends.observe(this){
            viewPager.adapter = TrendingAdapter(R.layout.item_trending, it as ArrayList<Music>, viewPager, musicItemClickListener)
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