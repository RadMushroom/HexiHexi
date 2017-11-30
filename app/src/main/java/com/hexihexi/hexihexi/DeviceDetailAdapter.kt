package com.hexihexi.hexihexi

import android.view.LayoutInflater
import android.view.ViewGroup

/**
 * Created by yurii on 10/22/17.
 */
class DeviceDetailAdapter : BaseRecyclerAdapter<DeviceDetail>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<DeviceDetail> {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.device_option_item_layout, parent, false)
        return DeviceDetailViewHolder(view)
    }

    fun handleDetail(deviceDetail: DeviceDetail) {
        data.map { it.type }.forEachIndexed { index, characteristic ->
            if (deviceDetail.type.name == characteristic.name) {
                data[index] = deviceDetail
                notifyItemChanged(index)
                return
            }
        }
        addItem(deviceDetail)
    }

}