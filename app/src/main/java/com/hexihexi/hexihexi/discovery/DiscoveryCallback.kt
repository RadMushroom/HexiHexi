package com.hexihexi.hexihexi.discovery

import android.bluetooth.BluetoothDevice

/**
 * Created by yurii on 10/16/17.
 */

interface DiscoveryCallback {
    fun onDeviceConnected(device: BluetoothDevice)
    fun onScanFinished()
    fun onScanStarted()
    fun onScanError()
}
