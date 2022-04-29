package com.example.musicdownloader.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicdownloader.OnActionCallBack


abstract class BaseFragment<K: ViewDataBinding, V: ViewModel>(callBack: OnActionCallBack): Fragment() {

    protected var mContext: Context? = null
    private var mRootView: View? = null

    protected val adfas: OnActionCallBack? = null

    protected var mViewModel: V? = null
    protected var binding: K? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

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
        initViews()
        setUpListener()
        setUpObserver()
        mViewModel = ViewModelProvider(requireActivity())[getViewModelClass()]
        return mRootView!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setUpListener()
        setUpObserver()
    }

    protected abstract fun initBinding(mRootView: View): K

    protected abstract fun getViewModelClass(): Class<V>

    protected abstract fun getLayoutId(): Int

    protected abstract fun initViews()

    protected abstract fun setUpListener()

    protected abstract fun setUpObserver()

}