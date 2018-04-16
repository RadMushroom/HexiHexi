package com.hexihexi.hexihexi.view

import android.view.View
import com.hexihexi.hexihexi.model.DeviceDetail
import kotlinx.android.synthetic.main.device_option_item_layout.view.*

class DeviceDetailViewHolder(view: View, onItemPositionClickListener: OnItemPositionClickListener): BaseViewHolder<DeviceDetail>(view) {

    init {
        view.setOnClickListener { onItemPositionClickListener.onItemClicked(adapterPosition) }
    }

    override fun bind(model: DeviceDetail) {
        with(itemView) {
            deviceDetailTextView.text = "${model.type.name} : ${model.value}"
        }
    }
}