package com.hexihexi.hexihexi

import android.bluetooth.BluetoothDevice
import android.view.View
import kotlinx.android.synthetic.main.device_item_layout.view.*

/**
 * Created by yurii on 10/16/17.
 */

class DeviceViewHolder(itemView: View, itemClickListener: OnItemPositionClickListener) : BaseViewHolder<BluetoothDevice>(itemView) {

    init {
        itemView.setOnClickListener { itemClickListener.onItemClicked(adapterPosition) }
    }

    override fun bind(device: BluetoothDevice) {
        with(itemView) {
            deviceName.text = device.name
            deviceAddress.text = device.address
            when (device.bondState) {
                BluetoothDevice.BOND_NONE -> deviceStatus.text = "Unpaired"
                BluetoothDevice.BOND_BONDING -> deviceStatus.text = "Pairing..."
                BluetoothDevice.BOND_BONDED -> deviceStatus.text = "Paired"
            }
        }
    }
}