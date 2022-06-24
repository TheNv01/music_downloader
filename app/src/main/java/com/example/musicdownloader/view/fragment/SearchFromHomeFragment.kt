package com.example.musicdownloader.view.fragment

import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.example.musicdownloader.MusicService
import com.example.musicdownloader.R
import com.example.musicdownloader.adapter.GenericAdapter
import com.example.musicdownloader.adapter.SearchBinding
import com.example.musicdownloader.databinding.SearchFromHomeFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.MusicDonwnloadedManager
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.viewmodel.SearchFromHomeViewModel
import com.proxglobal.proxads.adsv2.ads.ProxAds
import com.proxglobal.proxads.adsv2.callback.AdsCallback

class SearchFromHomeFragment: BaseFragment<SearchFromHomeFragmentBinding, SearchFromHomeViewModel>(), OnActionCallBack {

    lateinit var callBack: OnActionCallBack

    private val musicItemClickListener = object : ItemClickListener<Music> {
        override fun onClickListener(model: Music) {
            MusicDonwnloadedManager.currentMusicDownloaded = null
            MusicManager.setCurrentMusic(model)
            mViewModel.musics.value?.let { MusicManager.setListMusic(it) }
            showAds(null)
        }
    }

    override fun showAds(action: NavDirections?){
        ProxAds.getInstance().showInterstitial(requireActivity(), "inter", object: AdsCallback() {
            override fun onShow() {
                callBack.callBack(null, null)
            }

            override fun onClosed() {
                (activity as MainActivity).playMusicFragment!!.gotoService(MusicService.ACTION_START)
            }

            override fun onError() {
                Log.d("asdfasdf", "error")
                //callBack.callBack(KEY_SHOW_PLAY_MUSIC, null)
            }
        })
    }


    override fun initBinding(mRootView: View): SearchFromHomeFragmentBinding {
        return SearchFromHomeFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<SearchFromHomeViewModel> {
        return SearchFromHomeViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.search_from_home_fragment
    }

    override fun initViews() {
        callBack = this
        setTextColorHint()
        SearchBinding.isIconMenu = true
        SearchBinding.menuClickListener = object : ItemClickListener<Music> {
            override fun onClickListener(model: Music) {
                optionBottomDialog(model)
            }
        }
        if(isNetworkAvailable()){
            showSmallNative(binding.adContainer)
        }
        else{
            binding.adContainer.visibility = View.GONE
        }
    }

    override fun setUpListener() {
        binding.icClose.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
        (activity as MainActivity).onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (activity as MainActivity).findNavController(R.id.activity_main_nav_host_fragment).popBackStack()
                mViewModel.setListToEmpty()
            }
        })
    }

    override fun setUpObserver() {

        binding.viewmodel = mViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupSearchView()
        setupRecyclerview()
    }

    private fun setTextColorHint(){
        val theTextArea = binding.searchSong.findViewById<View>(androidx.appcompat.R.id.search_src_text) as SearchView.SearchAutoComplete
        theTextArea.setTextColor(Color.WHITE)
        theTextArea.alpha = 0.5f
        theTextArea.setHintTextColor(Color.WHITE)
    }

    private fun setupSearchView(){
        binding.searchSong.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mViewModel.getMusics(query)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun setupRecyclerview() {
        binding.recyclerViewSongRecommend.adapter = GenericAdapter(
            R.layout.item_top_listened,
            SearchBinding,
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