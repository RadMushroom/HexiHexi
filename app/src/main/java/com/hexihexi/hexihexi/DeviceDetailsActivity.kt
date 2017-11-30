package com.hexihexi.hexihexi

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.hexihexi.hexihexi.bluetooth.BluetoothServiceCallback
import com.hexihexi.hexihexi.bluetooth.Characteristic
import com.hexihexi.hexihexi.bluetooth.HexiBluetoothService
import kotlinx.android.synthetic.main.activity_device_details.*
import javax.inject.Inject

/**
 * Created by yurii on 10/19/17.
 */

class DeviceDetailsActivity : BaseActivity(), BluetoothServiceCallback {

    @Inject
    internal lateinit var bluetoothService: HexiBluetoothService

    private lateinit var device: BluetoothDevice
    private val deviceDetailAdapter = DeviceDetailAdapter()

    companion object {
        private const val EXTRA_DEVICE = "KEY_DEVICE"
        fun getStartIntent(context: Context, it: BluetoothDevice): Intent {
            return Intent(context, DeviceDetailsActivity::class.java).apply {
                putExtra(EXTRA_DEVICE, it)
            }
        }
    }

    override fun getContentView(): Int = R.layout.activity_device_details

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HexiApp.hexiComponent().inject(this)
        device = intent.extras.getParcelable(EXTRA_DEVICE)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = device.name
        deviceOptionsRecyclerView.adapter = deviceDetailAdapter
        bluetoothService.setUpWithDevice(device, this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onConnectionStateChanged(isConnected: Boolean) {

    }

    override fun onNewDataAvailable(characteristic: Characteristic, value: String) {
        Log.i("RECEIVED: "," ${characteristic.name} $value")
        runOnUiThread({
            deviceDetailAdapter.handleDetail(DeviceDetail(characteristic, value))
        })
    }
}
