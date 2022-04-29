package com.example.musicdownloader.view.fragment


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicdownloader.OnActionCallBack
import com.example.musicdownloader.R
import com.example.musicdownloader.databinding.HomeFragmentBinding
import com.example.musicdownloader.model.Item
import com.example.musicdownloader.viewmodel.HomeViewModel


class HomeFragment(private val callBack: OnActionCallBack): BaseFragment<HomeFragmentBinding, HomeViewModel>(callBack) {
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
        setupRecyclerView()
    }

    override fun setUpListener() {

    }

    override fun setUpObserver() {

    }

    private fun setupSpinner(){

        val categories = resources.getStringArray(R.array.category)


        val adapter = context?.let {
            ArrayAdapter(
                it,
            android.R.layout.simple_spinner_item,
                categories)
        }

        binding?.spinnerLanguage?.adapter = adapter

        binding?.spinnerLanguage?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Toast.makeText(context, categories[p2], Toast.LENGTH_SHORT).show()

            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
    }

    private fun setupRecyclerView(){

        val data = ArrayList<Item>()

        for (i in 1..5) {
            data.add(Item("Last Christmas", "Devon Lane", R.drawable.test_download))
        }
        val adapter = com.example.musicdownloader.adapter.SimpleAdapter(data)
        binding?.recyclerViewTopDownload?.adapter = adapter
    }
}