package com.example.musicdownloader.view.fragment

import android.util.Log
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.musicdownloader.R
import com.example.musicdownloader.UltraDepthScaleTransformer
import com.example.musicdownloader.adapter.*
import com.example.musicdownloader.databinding.HomeFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Genres
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.Region
import com.example.musicdownloader.view.ChangeRegionDialog
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.viewmodel.HomeViewModel

class HomeFragment(private val callBack: OnActionCallBack): BaseFragment<HomeFragmentBinding, HomeViewModel>(callBack) {

    companion object{
        const val KEY_SHOW_PLAY_MUSIC = "KEY_SHOW_PLAY_MUSIC"
        const val KEY_SHOW_SEE_ALL = "KEY_SHOW_SEE_ALL"
        const val TOP_DOWNLOAD = "TOP DOWNLOAD"
        const val TOP_RATING = "TOP RATING"
        const val TOP_LISTENED = "TOP LISTENED"
        const val GENRES = "GENRES"
    }

    private val musicItemClickListener = object : ItemClickListener<Music> {
        override fun onClickListener(model: Music) {
            callBack.callBack(KEY_SHOW_PLAY_MUSIC, model)
            mViewModel.topDownloads.value?.let { MusicManager.setListMusic(it) }
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

    }

    override fun setUpListener() {
        binding.tvSeeAllDownload.setOnClickListener {
            callBack.callBack(KEY_SHOW_SEE_ALL, TOP_DOWNLOAD)
        }
        binding.tvSeeAllRating.setOnClickListener {
            callBack.callBack(KEY_SHOW_SEE_ALL, TOP_RATING)
        }
        binding.tvSeeAllListened.setOnClickListener {
            callBack.callBack(KEY_SHOW_SEE_ALL, TOP_LISTENED)
        }
        binding.tvSeeAllGenres.setOnClickListener {
            callBack.callBack(KEY_SHOW_SEE_ALL, GENRES)
        }

        binding.tvRegion.setOnClickListener {
            changeRegionDialog()
        }
        setFragmentResultListener("requestKey") { _, bundle ->
            val region = bundle.get("region") as Region
            Log.d("asdfas", region.regionName)
        }
    }

    override fun setUpObserver() {
        binding.lifecycleOwner = this
        binding.viewmodel = mViewModel
        setupTrendingViewPager()
        setupRecyclerview()
    }

    private fun setupRecyclerview(){

        mViewModel.topRatings.observe(this){
            binding.recyclerViewTopRating.adapter = TopRatingAdapter(
                R.layout.item_top_rating,
                it,
                musicItemClickListener)
        }
        mViewModel.topListeneds.observe(this){
            binding.recyclerViewTopListened.adapter = TopListenedAdapter(
                R.layout.item_top_listened,
                it,
                musicItemClickListener)
        }
        mViewModel.topDownloads.observe(this){
            binding.recyclerViewTopDownload.adapter = TopDownloadAdapter(
                R.layout.item_top_download,
                it,
                musicItemClickListener)
        }
        mViewModel.genres.observe(this){
            binding.recyclerViewGenres.adapter = GenresAdapter(
                R.layout.item_genres,
                it,
                object : ItemClickListener<Genres> {
                    override fun onClickListener(model: Genres) {
                        Log.d("asdfasdf", "hahaha")
                    }

                })
        }
    }

    private fun changeRegionDialog() {
        val changeRegionDialog = ChangeRegionDialog()
        changeRegionDialog.show((activity as MainActivity).supportFragmentManager, "region_dialog")
    }

    private fun setupTrendingViewPager(){
        val viewPager = binding.viewPagerTrending
        mViewModel.trends.observe(this){
            viewPager.adapter = TrendingAdapter(R.layout.item_trending, it as MutableList<Music>, viewPager, musicItemClickListener)
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

}