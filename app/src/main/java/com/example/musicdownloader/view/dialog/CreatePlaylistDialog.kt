package com.example.musicdownloader.view.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.example.musicdownloader.R
import com.example.musicdownloader.databinding.CreatePlaylistFragmentBinding

class CreatePlaylistDialog : DialogFragment(){

    private var mRootView: View?= null
    private var binding: CreatePlaylistFragmentBinding?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_Dialog)
    }

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {

        mRootView = inflater.inflate(getLayoutId(), container, false)
        binding = initBinding(mRootView)
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setupListener()
    }

    private fun initBinding(mRootView: View?): CreatePlaylistFragmentBinding? {
        return mRootView?.let { CreatePlaylistFragmentBinding.bind(it) }
    }

    private fun getLayoutId(): Int{
        return R.layout.create_playlist_fragment
    }

    private fun setupListener(){
        binding?.tvCancel?.setOnClickListener {
            dialog?.dismiss()
        }
    }
}