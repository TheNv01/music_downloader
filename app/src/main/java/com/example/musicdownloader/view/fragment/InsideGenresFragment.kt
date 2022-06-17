package com.example.musicdownloader.view.fragment

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.navigation.fragment.navArgs
import com.example.musicdownloader.R
import com.example.musicdownloader.SharedPreferencesManager
import com.example.musicdownloader.adapter.GenericAdapter
import com.example.musicdownloader.adapter.SearchBinding
import com.example.musicdownloader.adapter.SeeAllBinding
import com.example.musicdownloader.databinding.InsideGenresFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.MusicDonwnloadedManager
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.Region
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.viewmodel.InsideGenresViewModel
import com.example.musicdownloader.viewmodel.SplashViewModel

class InsideGenresFragment : BaseFragment<InsideGenresFragmentBinding, InsideGenresViewModel>(), OnActionCallBack {


    lateinit var callBack: OnActionCallBack
    private val args: InsideGenresFragmentArgs by navArgs()

    private val musicItemClickListener = object : ItemClickListener<Music> {
        override fun onClickListener(model: Music) {
            MusicDonwnloadedManager.currentMusicDownloaded = null
            MusicManager.setCurrentMusic(model)
            mViewModel.musics.value?.let { MusicManager.setListMusic(it) }
            callBack.callBack(null, null)
        }
    }

    override fun initBinding(mRootView: View): InsideGenresFragmentBinding {
        return InsideGenresFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<InsideGenresViewModel> {
        return InsideGenresViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.inside_genres_fragment
    }


    override fun initViews() {
        callBack = this
        binding.tvTitle.text = args.genres

        if(mViewModel.title.value != null){
            mViewModel.title.postValue(args.genres)
        }
    }

    override fun setUpListener() {
        binding.icBack.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
    }

    private fun loadData(genres: String){
        SharedPreferencesManager.get<Region>("country").let { region ->
            if (region == null) {
                mViewModel.getMusics(genres, null)
            } else {
                mViewModel.getMusics(genres, region.regionCode)
            }
        }
    }

    override fun setUpObserver() {
        binding.viewmodel = mViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        mViewModel.title.observe(viewLifecycleOwner){
            if(it != args.genres){
                loadData(args.genres)
            }
        }

        SearchBinding.isIconMenu = true

        SearchBinding.menuClickListener = object : ItemClickListener<Music>{
            override fun onClickListener(model: Music) {
                optionBottomDialog(model)
            }

        }
        binding.recyclerViewGenres.adapter = GenericAdapter(
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