package com.hexihexi.hexihexi.view

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hexihexi.hexihexi.R
import com.hexihexi.hexihexi.model.DeviceDetail

class DeviceDetailAdapter(private val onItemPositionClickListener: OnItemPositionClickListener) : BaseRecyclerAdapter<DeviceDetail>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<DeviceDetail> {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.device_option_item_layout, parent, false)
        return DeviceDetailViewHolder(view, onItemPositionClickListener)
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