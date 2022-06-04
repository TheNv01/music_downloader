package com.example.musicdownloader.view.fragment

import android.util.Log
import android.view.View
import androidx.navigation.findNavController
import com.example.musicdownloader.R
import com.example.musicdownloader.adapter.GenericAdapter
import com.example.musicdownloader.adapter.GenresSeeAllBinding
import com.example.musicdownloader.databinding.SeeAllGenresFragmentBinding
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.model.Genres
import com.example.musicdownloader.viewmodel.SeeAllGenresViewModel

class SeeAllGenresFragment : BaseFragment<SeeAllGenresFragmentBinding, SeeAllGenresViewModel>() {

    override fun initBinding(mRootView: View): SeeAllGenresFragmentBinding {
        return SeeAllGenresFragmentBinding.bind(mRootView)
    }

    override fun getViewModelClass(): Class<SeeAllGenresViewModel> {
        return SeeAllGenresViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.see_all_genres_fragment
    }


    override fun initViews() {
    }

    override fun setUpListener() {

    }

    override fun setUpObserver() {
        binding.viewmodel = mViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.recyclerViewSeeAllGenres.adapter = GenericAdapter(
            R.layout.item_genres_see_all,
            GenresSeeAllBinding,
            object : ItemClickListener<Genres> {
                override fun onClickListener(model: Genres) {
                    val action = SeeAllGenresFragmentDirections.actionSeeAllGenresFragmentToInsideGenresFragment(model.name!!)
                    requireActivity().findNavController(R.id.activity_main_nav_host_fragment).navigate(action)
                }

            })
    }


}