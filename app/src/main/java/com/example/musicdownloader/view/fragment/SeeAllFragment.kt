package com.example.musicdownloader.view.fragment

import android.util.Log
import android.view.View
import androidx.navigation.fragment.navArgs
import com.example.musicdownloader.R
import com.example.musicdownloader.SharedPreferencesManager
import com.example.musicdownloader.adapter.*
import com.example.musicdownloader.databinding.SeeAllFragmentBinding
import com.example.musicdownloader.interfaces.OnActionCallBack
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.MusicDonwnloadedManager
import com.example.musicdownloader.manager.MusicManager
import com.example.musicdownloader.model.Genres
import com.example.musicdownloader.model.Music
import com.example.musicdownloader.model.Region
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.viewmodel.SeeAllViewModel

class SeeAllFragment: BaseFragment<SeeAllFragmentBinding, SeeAllViewModel>(), OnActionCallBack {

    lateinit var callBack: OnActionCallBack
    private val args: SeeAllFragmentArgs by navArgs()

    private val musicItemClickListener = object : ItemClickListener<Music> {
        override fun onClickListener(model: Music) {
            MusicDonwnloadedManager.currentMusicDownloaded = null
            MusicManager.setCurrentMusic(model)
            callBack.callBack(null, null)
        }
    }

    private val menuClickListener = object : ItemClickListener<Music> {
        override fun onClickListener(model: Music) {
            optionBottomDialog(model)
        }

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
        var option = args.option?.uppercase()
        if(option == "RANKING"){
            option = "RATING"
        }
        binding.tvTitle.text = getString(R.string.title, option)

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
        loadData(args.option.toString())

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