package com.example.musicdownloader.view.fragment

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.LiveData
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
    private lateinit var tvRegion: TextView
    private lateinit var imgLanguage: ImageView
    private val musicClickListener =  object : ItemClickListener<Music> {
        override fun onClickListener(model: Music) {
            MusicDonwnloadedManager.currentMusicDownloaded = null
            MusicManager.setCurrentMusic(model)
            callBack.callBack(KEY_SHOW_PLAY_MUSIC, null)

        }
    }

    companion object{
        const val KEY_SHOW_PLAY_MUSIC = "KEY_SHOW_PLAY_MUSIC"
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
        context?.let { SharedPreferencesManager.with(it) }


    }

    override fun setUpListener() {

        SharedPreferencesManager.get<Region>("country")?.let {
            imgLanguage.setImageResource(it.regionIcon)
            tvRegion.text = it.regionName
        }

        binding.icSearch.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment( 0)
            requireActivity().findNavController(R.id.activity_main_nav_host_fragment).navigate(action)
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
            musicClickListener)

        binding.recyclerViewTopListened.adapter = GenericAdapter(
            R.layout.item_top_listened,
            TopListenedBinding,
            musicClickListener)
        binding.recyclerViewTopDownload.adapter = GenericAdapter(
            R.layout.item_top_download,
            TopDownloadBinding,
            musicClickListener)
        binding.recyclerViewGenres.adapter = GenericAdapter(
            R.layout.item_genres,
            GenresBinding,
            object : ItemClickListener<Genres> {
                override fun onClickListener(model: Genres) {
                    val action = HomeFragmentDirections.actionHomeFragmentToInsideGenresFragment(model.name!!)
                    requireActivity().findNavController(R.id.activity_main_nav_host_fragment).navigate(action)
                }
            })
    }


    private fun setupTrendingViewPager(){
        val viewPager = binding.viewPagerTrending
        mViewModel.trends.observe(this){
            if(it.isNotEmpty()){
                viewPager.adapter = TrendingAdapter(R.layout.item_trending, it as ArrayList<Music>, viewPager,musicClickListener)
            }
            else{
                viewPager.adapter =TrendingAdapter(R.layout.item_trending, ArrayList(), viewPager, musicClickListener)
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