package com.hexihexi.hexihexi.bluetooth

import android.bluetooth.BluetoothDevice

/**
 * Created by yurii on 10/22/17.
 */
interface HexiBluetoothService {
    fun setUpWithDevice(device: BluetoothDevice, bluetoothServiceCallback: BluetoothServiceCallback)
}