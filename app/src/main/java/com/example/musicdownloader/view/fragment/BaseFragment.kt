package com.example.musicdownloader.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseFragment<K: ViewDataBinding, V: ViewModel>: Fragment() {

    private lateinit var mRootView: View
    protected lateinit var mViewModel: V
    lateinit var binding: K

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View {
        inflater.inflate(getLayoutId(), container, false)?.let {
            binding = initBinding(it)
            mRootView = it
        }
        mViewModel = ViewModelProvider(requireActivity(),
            ViewModelProvider.AndroidViewModelFactory(activity?.application!!))[getViewModelClass()]
        initViews()
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListener()
        setUpObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }

    protected abstract fun initBinding(mRootView: View): K

    protected abstract fun getViewModelClass(): Class<V>

    protected abstract fun getLayoutId(): Int

    protected abstract fun initViews()

    protected abstract fun setUpListener()

    protected abstract fun setUpObserver()

}