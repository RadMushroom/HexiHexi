package com.hexihexi.hexihexi.discovery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by yurii on 10/19/17.
 */
class BluetoothBondReceiver(private val onDeviceBondListener: OnDeviceBondListener) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let { onDeviceBondListener.onDeviceBonded(it) }
    }
}