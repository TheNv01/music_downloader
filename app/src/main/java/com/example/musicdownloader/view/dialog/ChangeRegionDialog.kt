package com.example.musicdownloader.view.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import com.example.musicdownloader.MusicService
import com.example.musicdownloader.R
import com.example.musicdownloader.adapter.ChangeRegionAdapter
import com.example.musicdownloader.databinding.ChangeRegionFragmentBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.manager.MediaManager
import com.example.musicdownloader.model.Region
import com.example.musicdownloader.view.MainActivity
import com.example.musicdownloader.viewmodel.ChangeRegionViewModel
import com.proxglobal.proxads.ProxUtils
import com.proxglobal.proxads.adsv2.ads.ProxAds
import com.proxglobal.proxads.adsv2.callback.AdsCallback


class ChangeRegionDialog: DialogFragment(), ItemClickListener<Region> {

    private var mRootView: View ?= null
    private var binding: ChangeRegionFragmentBinding ?= null
    private lateinit var viewModel: ChangeRegionViewModel
    private lateinit var adapter: ChangeRegionAdapter

    private var isUserClickPause = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.full_screen_dialog)
    }

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        mRootView = inflater.inflate(getLayoutId(), container, false)
        viewModel = ViewModelProvider(requireActivity())[ChangeRegionViewModel::class.java]
        binding = initBinding(mRootView)
        setTextColorHint()
        showSmallNative()
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObserver()
    }

    private fun initBinding(mRootView: View?): ChangeRegionFragmentBinding? {
        return mRootView?.let { ChangeRegionFragmentBinding.bind(it) }
    }

    private fun getLayoutId(): Int{
        return R.layout.change_region_fragment
    }

    private fun setUpObserver() {
        adapter = ChangeRegionAdapter(
            R.layout.item_region,
            viewModel.regions,
            this
        )
        binding?.recyclerViewRegion?.adapter = adapter
        setupSearchView()
        dialog!!.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun setupSearchView(){
        binding!!.searchCountry.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter.filter(query)
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun setTextColorHint(){
        val theTextArea = binding!!.searchCountry.findViewById<View>(androidx.appcompat.R.id.search_src_text) as SearchView.SearchAutoComplete
        theTextArea.setTextColor(Color.WHITE)
        theTextArea.alpha = 0.6f
        theTextArea.setHintTextColor(Color.WHITE)
    }

    override fun onClickListener(model: Region) {
        showAds(model)
    }

    private fun showAds(model: Region){
        ProxAds.getInstance().showInterstitialMax(requireActivity(), "inter", object: AdsCallback() {
            override fun onShow() {
                if(!MediaManager.isPause){
                    (activity as MainActivity).playMusicFragment?.gotoService(MusicService.ACTION_PAUSE)
                }
                else{
                    isUserClickPause = true
                }
                val bundle = Bundle()
                bundle.putSerializable("region", model)
                setFragmentResult("requestKey", bundle)
            }
            override fun onClosed() {
                if(!MediaManager.mediaPlayer!!.isPlaying){
                    if(!isUserClickPause){
                        (activity as MainActivity).playMusicFragment?.gotoService(MusicService.ACTION_RESUME)
                    }
                    else{
                        isUserClickPause = false
                    }
                }
                dialog?.dismiss()
            }
            override fun onError() {
                val bundle = Bundle()
                bundle.putSerializable("region", model)
                setFragmentResult("requestKey", bundle)

                dialog?.dismiss()
            }
        })
    }

    private fun showSmallNative(){
        ProxAds.getInstance().showSmallNativeMaxWithShimmer(requireActivity(), getString(R.string.native_id), binding!!.adContainer, object : AdsCallback() {

            override fun onShow() {
                super.onShow()
            }

            override fun onClosed() {
                super.onClosed()
            }

            override fun onError() {
                super.onError()
                binding!!.adContainer.visibility = View.GONE
            }
        })
    }

}