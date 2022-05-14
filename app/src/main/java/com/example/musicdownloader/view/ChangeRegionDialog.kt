package com.example.musicdownloader.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import com.example.musicdownloader.R
import com.example.musicdownloader.adapter.ChangeRegionAdapter
import com.example.musicdownloader.databinding.ChangeRegionFragmentBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.model.Region
import com.example.musicdownloader.viewmodel.ChangeRegionViewModel

class ChangeRegionDialog: DialogFragment(), ItemClickListener<Region> {

    private var mRootView: View ?= null
    private var binding: ChangeRegionFragmentBinding ?= null
    private lateinit var viewModel: ChangeRegionViewModel

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

    private fun setUpObserver(){
        binding?.recyclerViewRegion?.adapter = ChangeRegionAdapter(
            R.layout.item_region,
            viewModel.regions,
            this
        )
    }

    override fun onClickListener(model: Region) {
        val bundle = Bundle()
        bundle.putSerializable("region", model)
        setFragmentResult("requestKey", bundle)
        dialog?.dismiss()
    }
}