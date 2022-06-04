package com.example.musicdownloader.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.musicdownloader.R
import com.example.musicdownloader.interfaces.itemclickinterface.ItemClickListener
import com.example.musicdownloader.model.Option
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DownloadPopup(private val options: List<Option>) : BottomSheetDialogFragment() {

    lateinit var itemClickListener: ItemClickListener<Int>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_dialog, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpLayout(view.findViewById(R.id.layout))

    }

    private fun initView(option: Option): View {
        val view: View = LayoutInflater.from(context)
            .inflate(R.layout.item_option_add_favorite, null, false)
        val icon: ImageView = view.findViewById(R.id.ic_option)
        val tvName = view.findViewById<TextView>(R.id.tv_name)
        icon.setImageResource(option.icon)
        tvName.text = option.name
        return view
    }

    private fun setUpLayout(linearLayout: LinearLayout) {

        for (option in options) {
            val v: View = initView(option)
            v.setOnClickListener {
                if(option.icon == R.drawable.ic_pause_download){
                    option.icon = R.drawable.ic_play_not_background
                    option.name = "Resume Download"
                }
                else if(option.icon == R.drawable.ic_play_not_background){
                    option.icon = R.drawable.ic_pause_download
                    option.name = "Pause Download"
                }
                itemClickListener.onClickListener(option.icon)
                dismiss()
            }
            linearLayout.addView(v)
        }
    }

}