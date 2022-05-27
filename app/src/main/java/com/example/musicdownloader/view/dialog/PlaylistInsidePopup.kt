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

class PlaylistInsidePopup(private val options: List<Option>) : BottomSheetDialogFragment() {

    lateinit var itemClickListener: ItemClickListener<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.playlist_popup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpLayoutTopic(view.findViewById(R.id.layout))

    }

    private fun initTopicView(option: Option): View {
        val view: View = LayoutInflater.from(context)
            .inflate(R.layout.item_option_add_favorite, null, false)
        val icon: ImageView = view.findViewById(R.id.ic_option)
        val tvName = view.findViewById<TextView>(R.id.tv_name)
        icon.setImageResource(option.icon)
        tvName.text = option.name
        return view
    }

    private fun setUpLayoutTopic(linerLayout: LinearLayout) {

        for (option in options) {
            val v: View = initTopicView(option)
            v.setOnClickListener {
                itemClickListener.onClickListener(option.icon)
                dismiss()
            }
            linerLayout.addView(v)
        }
    }

}