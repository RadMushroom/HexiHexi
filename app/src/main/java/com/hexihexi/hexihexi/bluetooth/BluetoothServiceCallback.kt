package com.hexihexi.hexihexi.bluetooth

/**
 * Created by yurii on 10/22/17.
 */
interface BluetoothServiceCallback {

    fun onConnectionStateChanged(isConnected: Boolean)
    fun onNewDataAvailable(characteristic: Characteristic, value: String)
}