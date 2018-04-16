package com.hexihexi.hexihexi.view

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import com.hexihexi.hexihexi.R

/**
 * Created by yurii on 10/16/17.
 */

class DevicesAdapter(private var itemClickListener: OnItemPositionClickListener) : BaseRecyclerAdapter<BluetoothDevice>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<BluetoothDevice> {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.device_item_layout, parent, false)
        return DeviceViewHolder(view, itemClickListener)
    }

    fun handleDevice(bluetoothDevice: BluetoothDevice) {
        for (i in 0 until data.size) {
            if (data[i].address == bluetoothDevice.address) {
                updateItem(bluetoothDevice, i)
                return
            }
        }
        addItem(bluetoothDevice)
    }
}
