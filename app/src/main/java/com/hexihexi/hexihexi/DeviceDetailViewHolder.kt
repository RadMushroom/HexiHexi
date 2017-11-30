package com.hexihexi.hexihexi

import android.view.View
import kotlinx.android.synthetic.main.device_option_item_layout.view.*

/**
 * Created by yurii on 10/22/17.
 */
class DeviceDetailViewHolder(view: View): BaseViewHolder<DeviceDetail>(view) {
    override fun bind(model: DeviceDetail) {
        with(itemView) {
            deviceDetailTextView.text = "${model.type.name} : ${model.value}"
        }
    }
}