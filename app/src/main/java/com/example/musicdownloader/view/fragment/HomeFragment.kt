package com.example.musicdownloader.view.fragment

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.musicdownloader.R
import com.example.musicdownloader.UltraDepthScaleTransformer
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.adapter.GenericAdapter
import com.example.musicdownloader.adapter.TrendingAdapter
import com.example.musicdownloader.databinding.HomeFragmentBinding
import com.example.musicdownloader.model.Item
import com.example.musicdownloader.interfaces.recyclerbindinginterface.GenresBindingInterface
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.recyclerbindinginterface.TopDownloadBindingInterface
import com.example.musicdownloader.recyclerbindinginterface.TopListenedBindingInterface
import com.example.musicdownloader.recyclerbindinginterface.TopRatingBindingInterface
import com.example.musicdownloader.viewmodel.HomeViewModel

class HomeFragment(private val callBack: OnActionCallBack): BaseFragment<HomeFragmentBinding, HomeViewModel>(callBack) {

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
        setupSpinner()
    }

    override fun setUpListener() {

    }

    override fun setUpObserver() {
        binding.viewmodel = mViewModel
        setupTrendingViewPager()

        setupRecyclerview()
    }

    private fun setupRecyclerview(){
        binding.recyclerViewTopRating.adapter = GenericAdapter(
            R.layout.item_top_rating,
            TopRatingBindingInterface,
            object : ItemClickListener<Item> {
                override fun onClickListener(model: Item) {
                    callBack.callBack(KEY_SHOW_PLAY_MUSIC, model)
                }
            })
        binding.recyclerViewTopListened.adapter = GenericAdapter(
            R.layout.item_top_listened,
            TopListenedBindingInterface,
            object : ItemClickListener<Item> {
                override fun onClickListener(model: Item) {
                    Log.d("asdfasdf", "hahaha")
                }

            })
        binding.recyclerViewTopDownload.adapter = GenericAdapter(
            R.layout.item_top_download,
            TopDownloadBindingInterface,
            object : ItemClickListener<Item> {
                override fun onClickListener(model: Item) {
                    Log.d("asdfasdf", "hahaha")
                }

            })
        binding.recyclerViewGenres.adapter = GenericAdapter(
            R.layout.item_genres,
            GenresBindingInterface,
            object : ItemClickListener<Item> {
                override fun onClickListener(model: Item) {
                    Log.d("asdfasdf", "hahaha")
                }

            })
    }

    private fun setupSpinner(){
        val categories = resources.getStringArray(R.array.category)
        val adapter = context?.let {
            ArrayAdapter(it, R.layout.spinner_region, categories).apply {
                setDropDownViewResource(androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item)
            }
        }
        binding.spinnerLanguage.adapter = adapter
        binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Toast.makeText(context, categories[p2], Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }
    private fun setupTrendingViewPager(){
        val viewPager = binding.viewPagerTrending
        mViewModel.trends.observe(this){ it ->
            viewPager.adapter = TrendingAdapter(it as ArrayList<Music>, viewPager, object : ItemClickListener<Music> {
                override fun onClickListener(model: Music) {
                    Log.d("asdfasdf", "hahaha")
                }

            })
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